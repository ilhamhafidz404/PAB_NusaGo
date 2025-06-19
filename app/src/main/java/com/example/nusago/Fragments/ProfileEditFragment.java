package com.example.nusago.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.nusago.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.example.nusago.Fetcher.ProfileFetcher;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileEditFragment extends Fragment {

    private EditText etName, etEmail, etPassword, etConfirmPassword;
    private ProgressDialog progressDialog;
    private int userId = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_edit, container, false);

//        Handle Back
        FloatingActionButton btnBack = view.findViewById(R.id.fab_back_edit_account);
        btnBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new ProfileFragment())
                    .addToBackStack(null)
                    .commit();
        });

        etName     = view.findViewById(R.id.et_name);
        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);
        etConfirmPassword = view.findViewById(R.id.et_confirm_password);

        //
        SharedPreferences sp = requireActivity()
                .getSharedPreferences("auth", Context.MODE_PRIVATE);

        userId = sp.getInt("id", 0);                 // id user
        String savedName  = sp.getString("name", ""); // "" = default jika kosong
        String savedEmail = sp.getString("email", "");

        //
        etName.setText(savedName);
        etEmail.setText(savedEmail);
        etEmail.setEnabled(false);

        Button btnSave = view.findViewById(R.id.btn_save);
        btnSave.setOnClickListener(v -> updateProfile());

        return view;
    }

    public void updateProfile() {
        String name     = etName.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirm_password = etConfirmPassword.getText().toString().trim();

        // Ambil ID user dari SharedPreferences
        userId = requireActivity()
                .getSharedPreferences("auth", Context.MODE_PRIVATE)
                .getInt("id", 0); // default 0 jika tidak ditemukan

        if(!password.equals(confirm_password)){
            Toast.makeText(getContext(), "Password dan Konfirmasi Tidak Sama", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tampilkan loading
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Menyimpan perubahan...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ProfileFetcher.putProfile(userId, name, password, new ProfileFetcher.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                progressDialog.dismiss();

                // decode JSON
                try {
                    JSONObject json = new JSONObject(response);

                    String status = json.getString("status");
                    String message = json.getJSONObject("data").getString("message");

                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();

                        requireActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, new ProfileFragment())
                                .commit();
                    });

                } catch (JSONException e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
                    requireActivity().runOnUiThread(() -> Toast.makeText(
                            getContext(),
                            "Format response JSON salah",
                            Toast.LENGTH_SHORT
                    ).show());
                }
            }

            @Override
            public void onError(Exception e) {
                progressDialog.dismiss();
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Update gagal. Coba lagi.", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
