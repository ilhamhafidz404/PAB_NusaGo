package com.example.nusago.Fetcher;

import android.os.AsyncTask;

import com.example.nusago.Models.News;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class NewsFetcher extends AsyncTask<Void, Void, ArrayList<News>> {

    /* ────────── Callback ────────── */
    public interface Callback {
        void onResult(ArrayList<News> newsList);
        void onError(Exception e);
    }

    private final Callback callback;

    public NewsFetcher(Callback callback) {
        this.callback = callback;
    }

    /* ────────── Background work ────────── */
    @Override
    protected ArrayList<News> doInBackground(Void... voids) {
        ArrayList<News> newsList = new ArrayList<>();

        try {
            URL url = new URL("https://nusago.alope.id/news");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            /* HTTP 200 = OK */
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

                /* Baca respons */
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                br.close();

                /* Parse top‑level JSON */
                JSONObject root = new JSONObject(sb.toString());
                if (!root.optString("status").equalsIgnoreCase("success")) {
                    throw new Exception("API returned non‑success status");
                }

                /* Ambil array data */
                JSONArray dataArr = root.getJSONArray("data");
                for (int i = 0; i < dataArr.length(); i++) {
                    JSONObject o = dataArr.getJSONObject(i);

                    int id            = o.getInt("id");
                    String title      = o.getString("title");
                    String image      = o.isNull("image") ? null : o.getString("image");
                    String desc       = o.getString("description");
                    String body       = o.getString("body");
                    int userId        = o.getInt("user_id");
                    String createdAt  = o.getString("created_at");

                    newsList.add(new News(id, title, image, desc, body, userId, createdAt));
                }

            } else {
                throw new Exception("HTTP error code: " + conn.getResponseCode());
            }

        } catch (Exception e) {
            /* Laporkan ke onError & hentikan onResult */
            if (callback != null) callback.onError(e);
            return null;          // pastikan onPostExecute tidak memanggil onResult
        }

        return newsList;
    }

    /* ────────── Hasil ke UI thread ────────── */
    @Override
    protected void onPostExecute(ArrayList<News> news) {
        if (news != null && callback != null) {
            callback.onResult(news);
        }
    }
}
