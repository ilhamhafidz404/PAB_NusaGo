package com.example.nusago.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nusago.Adapters.NewsAdapter;
import com.example.nusago.Fetcher.NewsFetcher;
import com.example.nusago.Models.News;
import com.example.nusago.R;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private EditText etSearch;
    private NewsAdapter newsAdapter;

    private final List<News> newsList = new ArrayList<>();

    private static final int LIMIT = 20;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewSearch);
        progressBar  = view.findViewById(R.id.progress_search);
        etSearch     = view.findViewById(R.id.et_search);

        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("auth", Context.MODE_PRIVATE);
        String role  = prefs.getString("role", "user");
        int userId   = prefs.getInt("id", 0);

        etSearch.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                String keyword = etSearch.getText().toString().trim();
                fetchNews(keyword);
                return true;
            }
            return false;
        });


        /* Buat adapter */
        newsAdapter = new NewsAdapter(requireContext(), newsList, role, userId);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(newsAdapter);

        /* Klik item → detail */
        newsAdapter.setOnItemClickListener(newsId -> {
            Bundle bundle = new Bundle();
            bundle.putInt("news_id", newsId);

            NewsDetailFragment detail = new NewsDetailFragment();
            detail.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, detail)
                    .addToBackStack(null)
                    .commit();
        });

        newsAdapter.setAdminActionListener(new NewsAdapter.AdminActionListener() {
            @Override public void onEdit(int id) {
                Bundle b = new Bundle();
                b.putInt("news_id", id);

                NewsEditFragment f = new NewsEditFragment();
                f.setArguments(b);

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, f)
                        .addToBackStack(null)
                        .commit();
            }

            @Override public void onDelete(int id) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Konfirmasi Hapus")
                        .setMessage("Yakin hapus berita ini?")
                        .setPositiveButton("Ya", (d,w) -> deleteNewsAndRefresh(id))
                        .setNegativeButton("Batal", null)
                        .show();
            }
        });

        etSearch.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                fetchNews(etSearch.getText().toString().trim());
                return true;
            }
            return false;
        });

        fetchNews(""); // awal: tampilkan semua
        return view;
    }

    private void fetchNews(String query) {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        new NewsFetcher(LIMIT, query, new NewsFetcher.Callback() {
            @Override public void onResult(ArrayList<News> fetched) {
                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    newsList.clear();
                    newsList.addAll(fetched);
                    newsAdapter.notifyDataSetChanged();
                });
            }
            @Override public void onError(Exception e) {
                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(),
                            "Gagal memuat data", Toast.LENGTH_SHORT).show();
                });
            }
        }).execute();
    }

    private void deleteNewsAndRefresh(int id) {
        new NewsFetcher.DeleteCallback() {
            @Override public void onSuccess(String res) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(),
                            "Berita dihapus", Toast.LENGTH_SHORT).show();
                    fetchNews(etSearch.getText().toString().trim());
                });
            }
            @Override public void onError(Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(),
                                "Gagal menghapus", Toast.LENGTH_SHORT).show()
                );
            }
        };
        NewsFetcher.deleteNews(id, new NewsFetcher.DeleteCallback() {
            @Override
            public void onSuccess(String res) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Berita dihapus", Toast.LENGTH_SHORT).show();
                    fetchNews(etSearch.getText().toString().trim());
                });
            }

            @Override
            public void onError(Exception e) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Gagal menghapus berita", Toast.LENGTH_SHORT).show();
                });
            }
        });

    }
}
