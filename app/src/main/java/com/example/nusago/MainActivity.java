package com.example.nusago;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.nusago.Fetcher.NewsFetcher;
import com.example.nusago.Fragments.HomeFragment;
import com.example.nusago.Fragments.ProfileFragment;
import com.example.nusago.Fragments.SearchFragment;
import com.example.nusago.Models.News;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ArrayList<News> newsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        /* ───── Fetch News dari API ───── */
        new NewsFetcher(new NewsFetcher.Callback() {
            @Override
            public void onResult(ArrayList<News> fetchedNews) {
                newsList = fetchedNews;
                openHomeFragment();           // tampilkan Home setelah data didapat
            }

            @Override
            public void onError(Exception e) {
                Log.e("API_ERROR", "Error fetching news", e);
                openHomeFragment();           // tetap tampilkan Home walau list kosong
            }
        }).execute();

        /* ───── Bottom Navigation listener ───── */
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                openHomeFragment();
                return true;
            } else if (id == R.id.nav_search) {
                return loadFragment(new SearchFragment());
            } else if (id == R.id.nav_profile) {
                return loadFragment(new ProfileFragment());
            }
            return false;
        });

        /* Pilih tab Home saat pertama kali */
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }

    /* Helper membuka Home dan meng‐pass data */
    private void openHomeFragment() {
        HomeFragment fragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("news_list", newsList);
        fragment.setArguments(bundle);
        loadFragment(fragment);
    }

    /* Helper untuk transaksi fragment */
    private boolean loadFragment(Fragment f) {
        if (f == null) return false;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, f)
                .commit();
        return true;
    }
}
