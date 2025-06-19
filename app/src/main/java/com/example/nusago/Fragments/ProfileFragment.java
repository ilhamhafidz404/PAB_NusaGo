package com.example.nusago.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.nusago.Activities.Auth.LoginActivity;
import com.example.nusago.Activities.Auth.RegistrationActivity;
import com.example.nusago.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ProfileFragment extends Fragment {

    private SharedPreferences sharedPreferences;

    private TextView tvName, tvEmail, tvRole;
    private Button btnLogin, btnRegister, btnLogout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        sharedPreferences = requireActivity().getSharedPreferences("auth", Context.MODE_PRIVATE);

        tvName = view.findViewById(R.id.tv_name);
        tvEmail = view.findViewById(R.id.tv_email);
        tvRole = view.findViewById(R.id.tv_role);

        btnLogin = view.findViewById(R.id.btn_login);
        btnRegister = view.findViewById(R.id.btn_register);
        btnLogout = view.findViewById(R.id.btn_logout);

        FloatingActionButton btnEdit = view.findViewById(R.id.fab_edit_account);

        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            // Ambil data akun dari sharedPreferences
            String name = sharedPreferences.getString("name", "-");
            String email = sharedPreferences.getString("email", "-");
            String role = sharedPreferences.getString("role", "-");

            tvName.setText("Nama: " + name);
            tvEmail.setText("Email: " + email);
            tvRole.setText("Role: " + role);

            // Sembunyikan login & register, tampilkan logout
            btnLogin.setVisibility(View.GONE);
            btnRegister.setVisibility(View.GONE);
            btnLogout.setVisibility(View.VISIBLE);

            // Tampilkan FAB edit akun
            btnEdit.setVisibility(View.VISIBLE);

            // Set click listener
            btnEdit.setOnClickListener(v -> {
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new ProfileEditFragment())
                        .addToBackStack(null)
                        .commit();
            });

        } else {
            // User belum login
            tvName.setText("Nama: -");
            tvEmail.setText("Email: -");
            tvRole.setText("Role: -");

            btnLogin.setVisibility(View.VISIBLE);
            btnRegister.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.GONE);

            // Sembunyikan FAB edit akun
            btnEdit.setVisibility(View.GONE);
        }

        // Aksi tombol Login
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            startActivity(intent);
        });

        // Aksi tombol Register
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), RegistrationActivity.class);
            startActivity(intent);
        });

        // Aksi tombol Logout
        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            // Refresh fragment
            requireActivity().recreate(); // Atau load ulang fragment jika ingin lebih ringan
        });

        return view;
    }
}
