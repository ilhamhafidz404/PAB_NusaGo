package com.example.nusago.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nusago.Adapters.EventAdapter;
import com.example.nusago.Adapters.NewsAdapter;
import com.example.nusago.Fetcher.NewsFetcher;
import com.example.nusago.Models.Event;
import com.example.nusago.Models.News;
import com.example.nusago.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerViewEvents, recyclerViewNews;
    private EventAdapter eventAdapter;
    private NewsAdapter newsAdapter;
    private TextView tvLoading;

    private final List<Event> eventList = new ArrayList<>();
    private List<News> newsList = new ArrayList<>();

    @Override
    public void onResume() {
        super.onResume();
        fetchNews();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerViewEvents = v.findViewById(R.id.recyclerViewEvents);
        recyclerViewNews = v.findViewById(R.id.recyclerViewNews);
        tvLoading = v.findViewById(R.id.tv_loading);

        eventAdapter = new EventAdapter(eventList);
        recyclerViewEvents.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        recyclerViewEvents.setAdapter(eventAdapter);

        if (getArguments() != null) {
            Object obj = getArguments().getSerializable("news_list");
            if (obj instanceof ArrayList<?>) {
                newsList = (ArrayList<News>) obj;
            }
        }

        if (newsList.isEmpty()) {
            tvLoading.setVisibility(View.VISIBLE);
            recyclerViewNews.setVisibility(View.GONE);
        } else {
            tvLoading.setVisibility(View.GONE);
            recyclerViewNews.setVisibility(View.VISIBLE);
        }

        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("auth", Context.MODE_PRIVATE);
        String role   = prefs.getString("role", "user");
        int    userId = prefs.getInt("id", 0);

        newsAdapter = new NewsAdapter(requireContext(), newsList, role, userId);

        newsAdapter.setOnItemClickListener(newsId -> {
            Bundle bundle = new Bundle();
            bundle.putInt("news_id", newsId);

            NewsDetailFragment detailFragment = new NewsDetailFragment();
            detailFragment.setArguments(bundle);

            if (isAdded()) {
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, detailFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        newsAdapter.setAdminActionListener(new NewsAdapter.AdminActionListener() {
            @Override
            public void onEdit(int id) {
                Bundle b = new Bundle();
                b.putInt("news_id", id);

                NewsEditFragment f = new NewsEditFragment();
                f.setArguments(b);

                if (isAdded()) {
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, f)
                            .addToBackStack(null)
                            .commit();
                }
            }

            @Override
            public void onDelete(int id) {
                if (!isAdded()) return;

                new AlertDialog.Builder(requireContext())
                        .setTitle("Konfirmasi Hapus")
                        .setMessage("Apakah Anda yakin ingin menghapus berita ini?")
                        .setPositiveButton("Ya", (dialog, which) -> {
                            NewsFetcher.deleteNews(id, new NewsFetcher.DeleteCallback() {
                                @Override
                                public void onSuccess(String response) {
                                    if (!isAdded()) return;

                                    requireActivity().runOnUiThread(() -> {
                                        if (!isAdded()) return;
                                        Toast.makeText(requireContext(), "Berita berhasil dihapus", Toast.LENGTH_SHORT).show();
                                        fetchNews();
                                    });
                                }

                                @Override
                                public void onError(Exception e) {
                                    if (!isAdded()) return;
                                    requireActivity().runOnUiThread(() ->
                                            Toast.makeText(requireContext(), "Gagal menghapus", Toast.LENGTH_SHORT).show()
                                    );
                                }
                            });
                        })
                        .setNegativeButton("Batal", null)
                        .show();
            }
        });

        recyclerViewNews.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewNews.setAdapter(newsAdapter);

        return v;
    }

    private void fetchNews() {
        if (!isAdded()) return;

        tvLoading.setVisibility(View.VISIBLE);
        recyclerViewNews.setVisibility(View.GONE);

        new NewsFetcher(new NewsFetcher.Callback() {
            @Override
            public void onResult(ArrayList<News> updatedNewsList) {
                if (!isAdded()) return;

                requireActivity().runOnUiThread(() -> {
                    if (!isAdded()) return;
                    tvLoading.setVisibility(View.GONE);
                    recyclerViewNews.setVisibility(View.VISIBLE);

                    newsList.clear();
                    newsList.addAll(updatedNewsList);
                    newsAdapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onError(Exception e) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    if (!isAdded()) return;
                    Toast.makeText(requireContext(), "Gagal memuat data berita", Toast.LENGTH_SHORT).show();
                });
            }
        }).execute();
    }
}
