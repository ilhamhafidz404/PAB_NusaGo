package com.example.nusago.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

import androidx.annotation.*;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.nusago.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class NewsDetailFragment extends Fragment {

    private static final String TAG = "NEWS_DETAIL";

    private int newsId;
    private ImageView imgHeader;
    private TextView  tvTitle, tvDate, tvBody;
    private Button    btnOrder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_news_detail, container, false);

        imgHeader = v.findViewById(R.id.img_news);
        tvTitle   = v.findViewById(R.id.tv_title);
        tvDate    = v.findViewById(R.id.tv_date);
        tvBody    = v.findViewById(R.id.tv_body);
        btnOrder  = v.findViewById(R.id.btn_order_ticket);
        btnOrder.setVisibility(View.GONE);

        FloatingActionButton fabBack = v.findViewById(R.id.fab_back);
        fabBack.setOnClickListener(view ->
                requireActivity().getSupportFragmentManager().popBackStack());

        if (getArguments() != null) {
            newsId = getArguments().getInt("news_id", 0);
        }
        Log.d(TAG, "News ID diterima: " + newsId);

        if (newsId != 0) {
            getNewsDetail(newsId);
        } else {
            Log.e(TAG, "ID berita tidak valid");
            Toast.makeText(getContext(), "ID berita tidak valid", Toast.LENGTH_SHORT).show();
        }

        return v;
    }

    private void getNewsDetail(int id) {
        Log.d(TAG, "Memulai fetch detail berita…");
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                HttpURLConnection conn = null;
                try {
                    URL url = new URL("https://nusago.alope.id/show-news?id=" + id);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    int code = conn.getResponseCode();
                    Log.d(TAG, "HTTP Code: " + code);

                    InputStream is = (code >= 200 && code < 300)
                            ? conn.getInputStream() : conn.getErrorStream();

                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) sb.append(line);
                    br.close();
                    return sb.toString();

                } catch (Exception e) {
                    Log.e(TAG, "Error fetch detail", e);
                    return null;
                } finally {
                    if (conn != null) conn.disconnect();
                }
            }

            @Override
            protected void onPostExecute(String result) {
                if (result == null) {
                    Toast.makeText(getContext(),
                            "Gagal mengambil detail berita", Toast.LENGTH_SHORT).show();
                    return;
                }
                parseAndBind(result);
            }
        }.execute();
    }

    private void parseAndBind(String jsonStr) {
        try {
            JSONObject root = new JSONObject(jsonStr);
            if (!"success".equalsIgnoreCase(root.optString("status"))) {
                Toast.makeText(getContext(), "Gagal memuat detail berita", Toast.LENGTH_SHORT).show();
                return;
            }
            JSONObject data = root.getJSONObject("data");

            String title = data.getString("title");
            String image = data.optString("image", "");
            String body  = data.getString("body");
            String date  = data.getString("created_at");
            int    withEvent = data.optInt("with_event", 0);

            tvTitle.setText(title);
            tvDate.setText(date);
            tvBody.setText(body);

            if (!image.isEmpty())
                Glide.with(requireContext()).load(image).into(imgHeader);

            if (withEvent == 1) {
                btnOrder.setVisibility(View.VISIBLE);
                btnOrder.setOnClickListener(v -> handleOrderTicket());
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Format JSON salah", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleOrderTicket() {
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("auth", Context.MODE_PRIVATE);
        int userId = prefs.getInt("id", 0);

        if (userId == 0) {
            Toast.makeText(getContext(), "User belum login", Toast.LENGTH_SHORT).show();
            return;
        }

        String invoice = "INV" + System.currentTimeMillis();

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    URL url = new URL("https://nusago.alope.id/store-transaction");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    String postData = "invoice=" + invoice + "&user_id=" + userId;

                    OutputStream os = conn.getOutputStream();
                    os.write(postData.getBytes());
                    os.flush();
                    os.close();

                    int code = conn.getResponseCode();
                    Log.d(TAG, "POST transaksi code: " + code);

                    return (code == 200 || code == 201);

                } catch (Exception e) {
                    Log.e(TAG, "Gagal membuat transaksi", e);
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    Toast.makeText(getContext(), "Tiket berhasil dipesan", Toast.LENGTH_SHORT).show();
                    // Bisa redirect ke daftar transaksi
                } else {
                    Toast.makeText(getContext(), "Gagal memesan tiket", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
}
