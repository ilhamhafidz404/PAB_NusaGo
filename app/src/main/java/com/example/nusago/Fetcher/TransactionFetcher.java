package com.example.nusago.Fetcher;

import android.os.AsyncTask;
import com.example.nusago.Models.Transaction;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class TransactionFetcher extends AsyncTask<Void,Void,ArrayList<Transaction>> {

    public interface Callback {
        void onResult(ArrayList<Transaction> list);
        void onError(Exception e);
    }

    private final Callback cb;
    private Exception err;

    public TransactionFetcher(Callback cb) { this.cb = cb; }

    @Override
    protected ArrayList<Transaction> doInBackground(Void... voids) {
        ArrayList<Transaction> list = new ArrayList<>();
        try {
            URL url = new URL("https://nusago.alope.id/transactions");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line; while ((line = br.readLine()) != null) sb.append(line);
            br.close(); conn.disconnect();

            JSONObject root = new JSONObject(sb.toString());
            JSONObject dataObj = root.getJSONObject("data");
            JSONArray arr = dataObj.getJSONArray("data");

            for (int i=0;i<arr.length();i++) {
                JSONObject o = arr.getJSONObject(i);
                list.add(new Transaction(
                        o.getInt("id"),
                        o.getString("invoice"),
                        o.getInt("user_id"),
                        o.getString("name"),
                        o.getString("created_at")
                ));
            }
        } catch(Exception e){ err = e; }
        return list;
    }

    @Override
    protected void onPostExecute(ArrayList<Transaction> list) {
        if (err != null) cb.onError(err); else cb.onResult(list);
    }
}
