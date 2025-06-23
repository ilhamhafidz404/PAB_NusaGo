package com.example.nusago.Fetcher;

import android.util.Log;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class UserFetcher {

    public interface ApiCallback {
        void onSuccess(String response);
        void onError(Exception e);
    }

    /**
     * GET /users
     * Mendapatkan seluruh data user
     */
    public static void getUser(ApiCallback cb) {
        new Thread(() -> {
            Log.d("GET_USER", "Memulai get user...");

            try {
                URL url = new URL("https://nusago.alope.id/users");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setDoInput(true);  // ← hanya menerima data

                int responseCode = conn.getResponseCode();
                Log.d("GET_USER", "HTTP Response Code: " + responseCode);

                InputStream is = (responseCode >= 200 && responseCode < 300)
                        ? conn.getInputStream()
                        : conn.getErrorStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
                conn.disconnect();

                Log.d("GET_USER", "Response: " + result);
                cb.onSuccess(result.toString());

            } catch (Exception e) {
                Log.e("GET_USER", "Error saat get user", e);
                cb.onError(e);
            }
        }).start();
    }

    /**
     * POST /update-user
     * Mengupdate user berdasarkan ID
     */
    public static void putUser(int id, String name, String password, ApiCallback cb) {
        new Thread(() -> {
            try {
                Log.d("PUT_USER", "Memulai update profile...");

                URL url = new URL("https://nusago.alope.id/update-user");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                String postData = "id=" + id +
                        "&name=" + URLEncoder.encode(name, "UTF-8") +
                        "&password=" + URLEncoder.encode(password, "UTF-8");

                Log.d("PUT_USER", "POST data: " + postData);

                OutputStream os = conn.getOutputStream();
                os.write(postData.getBytes(StandardCharsets.UTF_8));
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                Log.d("PUT_USER", "HTTP Response Code: " + responseCode);

                InputStream is = (responseCode >= 200 && responseCode < 300)
                        ? conn.getInputStream()
                        : conn.getErrorStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
                conn.disconnect();

                Log.d("PUT_USER", "Response: " + result);
                cb.onSuccess(result.toString());

            } catch (Exception e) {
                Log.e("PUT_USER", "Error saat update profile", e);
                cb.onError(e);
            }
        }).start();
    }
}
