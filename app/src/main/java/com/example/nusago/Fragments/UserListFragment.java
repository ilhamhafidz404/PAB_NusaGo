package com.example.nusago.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import com.example.nusago.Adapters.UserAdapter;
import com.example.nusago.Models.User;
import com.example.nusago.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.*;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class UserListFragment extends Fragment {

    RecyclerView recyclerView;
    UserAdapter adapter;
    List<User> userList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);

        FloatingActionButton btnBack = view.findViewById(R.id.fab_back_edit_account);
        btnBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new ProfileFragment())
                    .addToBackStack(null)
                    .commit();
        });

        recyclerView = view.findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new UserAdapter(getContext(), userList, user -> deleteUser(user));
        recyclerView.setAdapter(adapter);

        fetchUsers();

        return view;
    }

    private void fetchUsers() {
        new Thread(() -> {
            try {
                URL url = new URL("https://nusago.alope.id/users");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                InputStream is = responseCode >= 200 && responseCode < 300
                        ? conn.getInputStream()
                        : conn.getErrorStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) result.append(line);
                reader.close();

                JSONObject json = new JSONObject(result.toString());
                JSONArray users = json.getJSONObject("data").getJSONArray("data");

                userList.clear();
                for (int i = 0; i < users.length(); i++) {
                    JSONObject u = users.getJSONObject(i);
                    int id = u.getInt("id");
                    String name = u.getString("name");
                    String email = u.getString("email");
                    String role = u.getString("role");
                    String deleted_at = u.getString("deleted_at");

                    userList.add(new User(id, name, email, role, deleted_at));
                }

                requireActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());

            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Gagal mengambil data", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    private void deleteUser(User user) {
        new Thread(() -> {
            try {
                String urlString = "https://nusago.alope.id/delete-user?id=" + user.getId();
                Log.d("DELETE_USER", "URL: " + urlString);

                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                Log.d("DELETE_USER", "Response Code: " + responseCode);

                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    Log.d("DELETE_USER", "Response Body: " + response.toString());

                    requireActivity().runOnUiThread(() -> {
                        fetchUsers();
                        Toast.makeText(getContext(), "User Diubah Statusnya", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    Log.e("DELETE_USER", "Gagal hapus user. Kode: " + responseCode);
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Gagal hapus user", Toast.LENGTH_SHORT).show()
                    );
                }

                conn.disconnect();
            } catch (Exception e) {
                Log.e("DELETE_USER", "Exception saat hapus user", e);
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
}
