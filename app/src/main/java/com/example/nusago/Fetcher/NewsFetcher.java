package com.example.nusago.Fetcher;

import android.os.AsyncTask;
import android.util.Log;

import com.example.nusago.Models.News;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class NewsFetcher extends AsyncTask<Void, Void, ArrayList<News>> {

    /* ---------------------- Callback utama ---------------------- */
    public interface Callback {
        void onResult(ArrayList<News> newsList);
        void onError(Exception e);
    }

    private final WeakReference<Callback> callbackRef;
    private Exception exception;

    public NewsFetcher(Callback callback) {
        this.callbackRef = new WeakReference<>(callback);
    }

    /* ---------------------- Jalankan di background ---------------------- */
    @Override
    protected ArrayList<News> doInBackground(Void... voids) {
        ArrayList<News> newsList = new ArrayList<>();

        try {
            URL url = new URL("https://nusago.alope.id/news");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                br.close();

                JSONObject root = new JSONObject(sb.toString());
                if (!root.optString("status").equalsIgnoreCase("success")) {
                    throw new Exception("API returned non-success status");
                }

                JSONArray dataArr = root.getJSONArray("data");
                for (int i = 0; i < dataArr.length(); i++) {
                    JSONObject o = dataArr.getJSONObject(i);

                    int id           = o.getInt("id");
                    String title     = o.getString("title");
                    String image     = o.isNull("image") ? null : o.getString("image");
                    String desc      = o.getString("description");
                    String body      = o.getString("body");
                    int userId       = o.getInt("user_id");
                    String createdAt = o.getString("created_at");

                    newsList.add(new News(id, title, image, desc, body, userId, createdAt));
                }

            } else {
                throw new Exception("HTTP error code: " + conn.getResponseCode());
            }

        } catch (Exception e) {
            this.exception = e;
            return null;
        }

        return newsList;
    }

    /* ---------------------- Kirim hasil ke UI ---------------------- */
    @Override
    protected void onPostExecute(ArrayList<News> result) {
        Callback cb = callbackRef.get();
        if (cb == null) return;

        if (exception != null) {
            cb.onError(exception);
        } else {
            cb.onResult(result);
        }
    }

    /* ---------------------- Hapus berita (static) ---------------------- */
    public interface DeleteCallback {
        void onSuccess(String response);
        void onError(Exception e);
    }

    public static void deleteNews(int newsId, DeleteCallback cb) {
        new Thread(() -> {
            final String TAG = "DELETE_NEWS";
            HttpURLConnection conn = null;
            try {
                URL url = new URL("https://nusago.alope.id/destroy-news");
                Log.d(TAG, "Request URL  : " + url);

                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                // ----- Kirim data -----
                String postData = "id=" + newsId;
                Log.d(TAG, "POST data    : " + postData);

                OutputStream os = conn.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                // ----- Cek response -----
                int code = conn.getResponseCode();
                Log.d(TAG, "HTTP Code    : " + code);

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(
                                code >= 200 && code < 300
                                        ? conn.getInputStream()     // success stream
                                        : conn.getErrorStream()     // error stream
                        )
                );

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                br.close();

                Log.d(TAG, "Response raw : " + sb);

                if (code >= 200 && code < 300) {
                    cb.onSuccess(sb.toString());
                } else {
                    cb.onError(new Exception("HTTP " + code + ": " + sb));
                }

            } catch (Exception e) {
                Log.e(TAG, "Error delete", e);
                cb.onError(e);
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }
}
