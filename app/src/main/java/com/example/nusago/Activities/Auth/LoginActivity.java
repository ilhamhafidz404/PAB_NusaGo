package com.example.nusago.Activities.Auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class LoginActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.auth_activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.auth_login_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void handleLogin(View view) {
        EditText username = findViewById(R.id.et_email);
        EditText password = findViewById(R.id.et_password);

        String user = username.getText().toString();
        String pass = password.getText().toString();


        // Loading
        ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        AuthFetcher.postLogin(user, pass, new AuthFetcher.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                progressDialog.dismiss(); // sembunyikan loading

                Log.d("LOGIN_SUCCESS", response);
                try {
                    JSONObject json = new JSONObject(response);
                    String message = json.getString("message");
                    JSONObject data = json.getJSONObject("data");

                    int id              = data.getInt("id");
                    String name         = data.getString("name");
                    String email        = data.getString("email");
                    String role         = data.getString("role");
                    String verified_at  = data.getString("verified_at");

                    Class<?> nextActivity;

                    if ("admin".equalsIgnoreCase(role)) {
                        nextActivity = com.example.nusago.Activities.Admin.MainActivity.class;
                    } else if ("manager".equalsIgnoreCase(role)) {
                        nextActivity = com.example.nusago.Activities.Manager.MainActivity.class;
                    } else {
                        nextActivity = com.example.nusago.MainActivity.class;
                    }

//                    Class<?> nextActivity = MainActivity.class;

                    // Create Session
                    sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.putInt("id", id);
                    editor.putString("name", name);
                    editor.putString("email", email);
                    editor.putString("role", role);
                    editor.putString("verified_at", verified_at);
                    editor.apply();

                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, nextActivity));
                    });

                    finish();

                } catch (JSONException e) {
                    progressDialog.dismiss(); // pastikan loading di-dismiss jika error
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(
                            LoginActivity.this,
                            "Format response JSON salah",
                            Toast.LENGTH_SHORT
                    ).show());
                }
            }

            @Override
            public void onError(Exception e) {
                progressDialog.dismiss(); // sembunyikan loading jika error
                Log.e("LOGIN_ERROR", "Gagal login", e);
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, "Login gagal. Coba lagi.", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    public void goToRegister(View v){
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }
}
