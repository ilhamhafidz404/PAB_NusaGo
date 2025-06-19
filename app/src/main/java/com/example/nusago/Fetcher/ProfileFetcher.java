// path: app/src/main/java/com/example/nusago/fetcher/ProfileFetcher.java

package com.example.nusago.Fetcher;

import android.util.Log;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class ProfileFetcher {

    public interface ApiCallback {
        void onSuccess(String response);
        void onError(Exception e);
    }

    public static void putProfile(int id, String name, String password, ApiCallback cb) {
        new Thread(() -> {
            try {
                Log.d("PUT_PROFILE", "Memulai update profile...");

                URL url = new URL("https://nusago.alope.id/update-profile");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                String postData = "id=" + id +
                        "&name=" + URLEncoder.encode(name, "UTF-8") +
                        "&password=" + URLEncoder.encode(password, "UTF-8");

                Log.d("PUT_PROFILE", "POST data: " + postData);

                OutputStream os = conn.getOutputStream();
                os.write(postData.getBytes(StandardCharsets.UTF_8));
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                Log.d("PUT_PROFILE", "HTTP Response Code: " + responseCode);

                InputStream is;
                if (responseCode >= 200 && responseCode < 300) {
                    is = conn.getInputStream();
                } else {
                    is = conn.getErrorStream();
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();

                Log.d("PUT_PROFILE", "Response: " + result.toString());

                conn.disconnect();

                cb.onSuccess(result.toString());

            } catch (Exception e) {
                Log.e("PUT_PROFILE", "Error saat update profile", e);
                cb.onError(e);
            }
        }).start();
    }
}
