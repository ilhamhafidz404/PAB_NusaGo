package com.example.nusago.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.nusago.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NewsDetailFragment extends Fragment {

    private static final String TAG = "NEWS_DETAIL";

    private int newsId;
    private ImageView imgHeader;
    private TextView tvTitle, tvDate, tvBody;

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

        FloatingActionButton fabBack = v.findViewById(R.id.fab_back);
        fabBack.setOnClickListener(view ->
                requireActivity().getSupportFragmentManager().popBackStack());

        // Ambil ID dari arguments
        if (getArguments() != null) {
            newsId = getArguments().getInt("news_id", 0);
        }
        Log.d(TAG, "News ID diterima: " + newsId);

        if (newsId != 0) {
            getNewsDetail(newsId);
        } else {
            Log.e(TAG, "ID berita tidak valid");
        }

        return v;
    }

    /* ───────────────────────────
       AsyncTask fetch detail news
       ─────────────────────────── */
    private void getNewsDetail(int id) {
        Log.d(TAG, "Memulai fetch detail berita…");
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                HttpURLConnection conn = null;
                try {
                    URL url = new URL("https://nusago.alope.id/show-news?id=" + id);
                    Log.d(TAG, "Request URL: " + url);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    int code = conn.getResponseCode();
                    Log.d(TAG, "HTTP Response Code: " + code);

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) sb.append(line);
                    reader.close();

                    Log.d(TAG, "Response raw: " + sb);
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

                try {
                    JSONObject json = new JSONObject(result);
                    String status = json.getString("status");

                    if (status.equals("success")) {
                        JSONObject data = json.getJSONObject("data");

                        String title = data.getString("title");
                        String image = data.getString("image");
                        String description = data.getString("description");
                        String body = data.getString("body");
                        String date = data.getString("created_at");

                        // Atur data ke view
                        tvTitle.setText(title);
                        tvDate.setText(date);
                        tvBody.setText(body);
                        Glide.with(requireContext()).load(image).into(imgHeader);
                    } else {
                        Toast.makeText(getContext(), "Gagal memuat detail berita", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Format JSON salah", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
}
