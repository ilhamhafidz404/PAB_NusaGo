package com.example.nusago.Activities.Auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.nusago.Fetcher.AuthFetcher;
import com.example.nusago.R;

import org.json.JSONException;
import org.json.JSONObject;

public class RegistrationActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.auth_activity_registration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.auth_register_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void onRegister(View view){
        EditText Et_name     = findViewById(R.id.et_name);
        EditText Et_email    = findViewById(R.id.et_email);
        EditText Et_password = findViewById(R.id.et_password);

        String name         = Et_name.getText().toString();
        String email        = Et_email.getText().toString();
        String password     = Et_password.getText().toString();

        // Loading
        ProgressDialog progressDialog = new ProgressDialog(RegistrationActivity.this);
        progressDialog.setMessage("Registration Processing...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        AuthFetcher.postRegister(name, email, password, "user", new AuthFetcher.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                progressDialog.dismiss(); // sembunyikan loading
                Log.d("REGISTER SUCCESS", response);

                // decode JSON
                try {
                    JSONObject json = new JSONObject(response);
                    String message = json.getString("message");

                    // action success on UI
                    runOnUiThread(() -> {
                        Toast.makeText(RegistrationActivity.this, message, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                    });

                } catch (JSONException e) {
                    progressDialog.dismiss(); // sembunyikan loading
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(
                            RegistrationActivity.this,
                            "Format response JSON salah",
                            Toast.LENGTH_SHORT
                    ).show());
                }
            }

            @Override
            public void onError(Exception e) {
                progressDialog.dismiss();
                Log.e("REGISTER_ERROR", "Gagal Registrasi", e);
                runOnUiThread(() -> {
                    Toast.makeText(RegistrationActivity.this, "Registrasi gagal. Coba lagi.", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    public void goToLogin(View v){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
