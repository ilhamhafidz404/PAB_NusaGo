package com.example.nusago.Activities.Admin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nusago.Activities.Auth.LoginActivity;
import com.example.nusago.Adapters.AdminNewsAdapter;
import com.example.nusago.Fetcher.NewsFetcher;
import com.example.nusago.Models.News;
import com.example.nusago.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerViewNews;
    AdminNewsAdapter newsAdapter;
    ArrayList<News> newsList = new ArrayList<>();
    SharedPreferences sharedPreferences;
    Button btnAddNews, btnLogout, btnViewAccounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_main);

        sharedPreferences = getSharedPreferences("auth", Context.MODE_PRIVATE);

        recyclerViewNews = findViewById(R.id.recyclerViewNews);
        btnAddNews = findViewById(R.id.btn_add_news);
        btnLogout = findViewById(R.id.btn_logout);
        btnViewAccounts = findViewById(R.id.btn_view_accounts);

        recyclerViewNews.setLayoutManager(new LinearLayoutManager(this));
        newsAdapter = new AdminNewsAdapter(this, newsList, new AdminNewsAdapter.OnActionListener() {
            @Override
            public void onUpdate(News news) {
                Intent intent = new Intent(MainActivity.this, CreateNewsActivity.class);
                intent.putExtra("news", news);
                startActivity(intent);
            }

            @Override
            public void onDelete(News news) {
                // TODO: Implement delete logic
                Toast.makeText(MainActivity.this, "Hapus: " + news.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
        recyclerViewNews.setAdapter(newsAdapter);

        btnAddNews.setOnClickListener(v -> {
            startActivity(new Intent(this, CreateNewsActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            sharedPreferences.edit().clear().apply();
            Toast.makeText(this, "Logout berhasil", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        btnViewAccounts.setOnClickListener(v -> {
            Toast.makeText(this, "Fitur 'Lihat Akun' belum tersedia", Toast.LENGTH_SHORT).show();
        });

        fetchNews();
    }

    private void fetchNews() {
        new NewsFetcher(new NewsFetcher.Callback() {
            @Override
            public void onResult(ArrayList<News> fetchedNews) {
                newsList.clear();
                newsList.addAll(fetchedNews);
                newsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(MainActivity.this, "Gagal mengambil berita", Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }
}
