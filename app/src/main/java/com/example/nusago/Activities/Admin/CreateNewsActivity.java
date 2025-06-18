package com.example.nusago.Activities.Admin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nusago.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;

public class CreateNewsActivity extends AppCompatActivity {

    private static final String API_URL = "https://nusago.alope.id/store-news"; // ganti jika berbeda

    private EditText etTitle, etDescription, etBody, etImageUrl;
    private Button btnSubmit;
    private ProgressDialog progressDialog;

    private SharedPreferences authPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_create_news);

        authPrefs = getSharedPreferences("auth", Context.MODE_PRIVATE);

        etTitle       = findViewById(R.id.et_title);
        etDescription = findViewById(R.id.et_description);
        etBody        = findViewById(R.id.et_body);
        etImageUrl    = findViewById(R.id.et_image_url);
        btnSubmit     = findViewById(R.id.btn_submit);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Menyimpan berita...");
        progressDialog.setCancelable(false);

        btnSubmit.setOnClickListener(v -> handleSubmit());
    }

    /* ───────────────────────── 1. Validasi & Kirim ───────────────────────── */
    private void handleSubmit() {
        String title = etTitle.getText().toString().trim();
        String desc  = etDescription.getText().toString().trim();
        String body  = etBody.getText().toString().trim();
        String image = etImageUrl.getText().toString().trim(); // boleh kosong

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(desc) || TextUtils.isEmpty(body)) {
            Toast.makeText(this, "Judul, deskripsi, dan body wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = authPrefs.getInt("id", -1); // ambil id admin
        if (userId == -1) {
            Toast.makeText(this, "Session user tidak valid", Toast.LENGTH_SHORT).show();
            return;
        }

        /* Jalankan task kirim API */
        new PostNewsTask(title, desc, body, image, userId).execute();
    }

    /* ───────────────────────── 2. AsyncTask kirim POST ───────────────────────── */
    private class PostNewsTask extends AsyncTask<Void, Void, String> {

        private final String title, desc, body, image;
        private final int userId;
        private Exception error;

        PostNewsTask(String title, String desc, String body, String image, int userId) {
            this.title = title;
            this.desc  = desc;
            this.body  = body;
            this.image = image;
            this.userId = userId;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(API_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setConnectTimeout(7000);
                conn.setReadTimeout(7000);
                conn.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");

                /* Bangun data POST form‑urlencoded */
                String postData = "title=" + URLEncoder.encode(title, "UTF-8") +
                        "&description=" + URLEncoder.encode(desc, "UTF-8") +
                        "&body=" + URLEncoder.encode(body, "UTF-8") +
                        "&image=" + URLEncoder.encode(image, "UTF-8") +
                        "&user_id=" + userId;

                OutputStream os = conn.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                bw.write(postData);
                bw.flush();
                bw.close();
                os.close();

                int responseCode = conn.getResponseCode();
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(
                                responseCode >= 400 ? conn.getErrorStream() : conn.getInputStream()
                        )
                );

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                br.close();
                conn.disconnect();
                return sb.toString();

            } catch (Exception e) {
                error = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(String resp) {
            progressDialog.dismiss();

            if (error != null) {
                error.printStackTrace();
                Toast.makeText(CreateNewsActivity.this,
                        "Gagal terhubung ke server", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                JSONObject obj = new JSONObject(resp);
                String status = obj.optString("status").toLowerCase(Locale.ROOT);
                String message = obj.optString("message", "No message");

                if ("success".equals(status)) {
                    Toast.makeText(CreateNewsActivity.this,
                            "Berita berhasil disimpan", Toast.LENGTH_LONG).show();
                    finish(); // kembali ke halaman sebelumnya
                } else {
                    Toast.makeText(CreateNewsActivity.this,
                            "Gagal: " + message, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(CreateNewsActivity.this,
                        "Format respons tidak valid", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
