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
import java.net.URLEncoder;
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
                    int id = o.getInt("id");
                    String title = o.getString("title");
                    String image = o.isNull("image") ? null : o.getString("image");
                    String desc = o.getString("description");
                    String body = o.getString("body");
                    int userId = o.getInt("user_id");
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

    /* ---------------------- DELETE berita (static) ---------------------- */
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

                String postData = "id=" + newsId;
                Log.d(TAG, "POST data    : " + postData);

                OutputStream os = conn.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                int code = conn.getResponseCode();
                Log.d(TAG, "HTTP Code    : " + code);

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(
                                code >= 200 && code < 300
                                        ? conn.getInputStream()
                                        : conn.getErrorStream()
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

    /* ---------------------- CREATE berita (static) ---------------------- */
    public interface PostCallback {
        void onSuccess(String message);
        void onError(Exception e);
    }

    public static void postNews(String title, String desc, String body,
                                String imageUrl, int userId, PostCallback cb) {

        new Thread(() -> {
            final String TAG = "POST_NEWS";
            long t0 = System.currentTimeMillis();          // waktu mulai

            HttpURLConnection conn = null;
            try {
                URL url = new URL("https://nusago.alope.id/store-news");
                Log.d(TAG, "Request URL   : " + url);

                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String data = "title=" + URLEncoder.encode(title, "UTF-8")
                        + "&description=" + URLEncoder.encode(desc, "UTF-8")
                        + "&body=" + URLEncoder.encode(body, "UTF-8")
                        + "&image=" + URLEncoder.encode(imageUrl, "UTF-8")
                        + "&user_id=" + URLEncoder.encode(String.valueOf(userId), "UTF-8");

                Log.d(TAG, "POST Data     : " + data);

                /* ─── Kirim payload ─── */
                OutputStream os = conn.getOutputStream();
                os.write(data.getBytes());
                os.flush();
                os.close();
                Log.d(TAG, "Payload sent  ✔");

                /* ─── Response ─── */
                int code = conn.getResponseCode();
                Log.d(TAG, "HTTP Code     : " + code);

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(
                                code >= 200 && code < 300
                                        ? conn.getInputStream()
                                        : conn.getErrorStream()
                        )
                );
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                br.close();
                Log.d(TAG, "Response Body : " + sb);

                long elapsed = System.currentTimeMillis() - t0;
                Log.d(TAG, "Elapsed (ms)  : " + elapsed);

                if (code >= 200 && code < 300) {
                    cb.onSuccess(sb.toString());
                } else {
                    cb.onError(new Exception("HTTP " + code + ": " + sb));
                }

            } catch (Exception e) {
                Log.e(TAG, "POST Error    : " + e.getMessage(), e);
                cb.onError(e);
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    public interface SingleCallback {
        void onResult(News news);
        void onError(Exception e);
    }

    public static void getNewsById(int newsId, SingleCallback cb) {

        new Thread(() -> {
            final String TAG = "GET_NEWS_BY_ID";
            long t0 = System.currentTimeMillis();          // mulai timer
            HttpURLConnection conn = null;

            try {
                URL url = new URL("https://nusago.alope.id/show-news?id=" + newsId);

                Log.d(TAG, "Request URL  : " + url);

                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int code = conn.getResponseCode();
                Log.d(TAG, "HTTP Code    : " + code);

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(
                                code >= 200 && code < 300
                                        ? conn.getInputStream()
                                        : conn.getErrorStream()
                        )
                );
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                br.close();

                Log.d(TAG, "Response raw : " + sb);
                Log.d(TAG, "Elapsed (ms) : " + (System.currentTimeMillis() - t0));

                if (code >= 200 && code < 300) {
                    // parse JSON
                    JSONObject root = new JSONObject(sb.toString());
                    if (!root.optString("status").equalsIgnoreCase("success")) {
                        throw new Exception("API returned non-success status");
                    }

                    JSONObject o = root.getJSONObject("data");
                    News news = new News(
                            o.getInt("id"),
                            o.getString("title"),
                            o.isNull("image") ? null : o.getString("image"),
                            o.getString("description"),
                            o.getString("body"),
                            o.getInt("user_id"),
                            o.getString("created_at")
                    );
                    cb.onResult(news);

                } else { // error code
                    cb.onError(new Exception("HTTP " + code + ": " + sb));
                }

            } catch (Exception e) {
                Log.e(TAG, "Error fetching by ID", e);
                cb.onError(e);

            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    public static void updateNews(int newsId,
                                  String title,
                                  String desc,
                                  String body,
                                  String imageUrl,
                                  int userId,
                                  PostCallback cb) {

        new Thread(() -> {
            final String TAG = "UPDATE_NEWS";
            long t0 = System.currentTimeMillis();
            HttpURLConnection conn = null;

            try {
                URL url = new URL("https://nusago.alope.id/update-news");
                Log.d(TAG, "Request URL   : " + url);

                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String data = "id=" + URLEncoder.encode(String.valueOf(newsId), "UTF-8")
                        + "&title=" + URLEncoder.encode(title, "UTF-8")
                        + "&description=" + URLEncoder.encode(desc, "UTF-8")
                        + "&body=" + URLEncoder.encode(body, "UTF-8")
                        + "&image=" + URLEncoder.encode(imageUrl, "UTF-8")
                        + "&user_id=" + URLEncoder.encode(String.valueOf(userId), "UTF-8");

                Log.d(TAG, "POST Data     : " + data);
                conn.getOutputStream().write(data.getBytes());
                conn.getOutputStream().close();

                int code = conn.getResponseCode();
                Log.d(TAG, "HTTP Code     : " + code);


                BufferedReader br = new BufferedReader(
                        new InputStreamReader(
                                code >= 200 && code < 300
                                        ? conn.getInputStream()
                                        : conn.getErrorStream()
                        )
                );
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                br.close();

                Log.d(TAG, "Response raw  : " + sb);
                Log.d(TAG, "Elapsed (ms)  : " + (System.currentTimeMillis() - t0));

                /* ===== Parse JSON untuk ambil status & message ===== */
                String resMsg = "Unknown";
                String resStatus = "error";

                Log.d(TAG, "Response raw  : " + sb);  // sb berisi body respons dari server
                try {
                    JSONObject root = new JSONObject(sb.toString());
                    resStatus = root.optString("status", "error");
                    resMsg    = root.optString("message", resMsg);
                } catch (Exception je) {
                    Log.w(TAG, "Response bukan JSON yang valid");
                }

                if (code >= 200 && code < 300 && resStatus.equalsIgnoreCase("success")) {
                    Log.i(TAG, "Update sukses : " + resMsg);
                    cb.onSuccess(resMsg);
                } else {
                    Log.e(TAG, "Update gagal  : " + resMsg);
                    cb.onError(new Exception("HTTP " + code + ": " + resMsg));
                }

            } catch (Exception e) {
                Log.e(TAG, "Error update", e);
                cb.onError(e);
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }


}
