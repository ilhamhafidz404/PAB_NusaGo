package com.example.nusago.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nusago.Adapters.UserAdapter;
import com.example.nusago.Fetcher.UserFetcher;
import com.example.nusago.Models.User;
import com.example.nusago.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserListFragment extends Fragment {

    RecyclerView recyclerView;
    UserAdapter adapter;
    List<User> userList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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

        adapter = new UserAdapter(getContext(), userList, new UserAdapter.OnUserActionListener() {
            @Override
            public void onDelete(User user) {
                // TODO: Implement API delete user
                Toast.makeText(getContext(), "Hapus user: " + user.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(adapter);

        fetchUsers();

        return view;
    }

    private void fetchUsers() {
        UserFetcher.getUser(new UserFetcher.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    userList.clear();

                    JSONObject root = new JSONObject(response);
                    if (!"success".equalsIgnoreCase(root.optString("status"))) {
                        throw new JSONException("status not success");
                    }

                    JSONObject dataObj = root.getJSONObject("data");   // lapisan pertama
                    JSONArray arr      = dataObj.getJSONArray("data"); // array user sebenarnya

                    userList.clear();
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);

                        int id       = obj.getInt("id");
                        String name  = obj.getString("name");
                        String email = obj.getString("email");
                        String role  = obj.getString("role");

                        userList.add(new User(id, name, email, role));
                    }
                    requireActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
                } catch (Exception e) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Format data error", Toast.LENGTH_SHORT).show()
                    );
                }
            }

            @Override
            public void onError(Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Gagal mengambil data user", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
}
