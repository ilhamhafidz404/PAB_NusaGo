package com.example.nusago.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nusago.Adapters.EventAdapter;
import com.example.nusago.Adapters.NewsAdapter;
import com.example.nusago.Models.Event;
import com.example.nusago.Models.News;
import com.example.nusago.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerViewEvents, recyclerViewNews;
    private EventAdapter eventAdapter;
    private NewsAdapter newsAdapter;

    private final List<Event> eventList = new ArrayList<>();
    private List<News> newsList = new ArrayList<>();

    private TextView tvLoading;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerViewEvents = v.findViewById(R.id.recyclerViewEvents);
        recyclerViewNews   = v.findViewById(R.id.recyclerViewNews);

        /* ───── Event dummy (bisa diganti API) ───── */
//        eventList.add(new Event("Seren Taun",
//                "Seren taun adalah upacara adat yang dilakukan ...",
//                "10 Juni 2025", "Kuningan", R.drawable.header));
//        eventList.add(new Event("Cingcowong",
//                "Cingcowong adalah sebuah tradisi atau ritual ya..",
//                "10 Juni 2025", "Kuningan", R.drawable.header));

        eventAdapter = new EventAdapter(eventList);
        recyclerViewEvents.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        recyclerViewEvents.setAdapter(eventAdapter);

        /* ───── Ambil data News dari arguments ───── */
        if (getArguments() != null) {
            Object obj = getArguments().getSerializable("news_list");
            if (obj instanceof ArrayList<?>) {
                //noinspection unchecked
                newsList = (ArrayList<News>) obj;
            }
        }

        tvLoading = v.findViewById(R.id.tv_loading);

        tvLoading.setVisibility(View.VISIBLE);
        recyclerViewNews.setVisibility(View.GONE);

        if (getArguments() != null) {
            Object obj = getArguments().getSerializable("news_list");
            if (obj instanceof ArrayList<?>) {
                //noinspection unchecked
                newsList = (ArrayList<News>) obj;
            }
        }

        if (!newsList.isEmpty()) {
            tvLoading.setVisibility(View.GONE);
            recyclerViewNews.setVisibility(View.VISIBLE);
        }

        newsAdapter = new NewsAdapter(requireContext(), newsList);

        newsAdapter = new NewsAdapter(requireContext(), newsList);

        /* ── handle klik ─────────────────────────────────────────── */
        newsAdapter.setOnItemClickListener(newsId -> {
            Bundle bundle = new Bundle();
            bundle.putInt("news_id", newsId);

            NewsDetailFragment detailFragment = new NewsDetailFragment();
            detailFragment.setArguments(bundle);

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)   // ganti ID container sesuai layout activity
                    .addToBackStack(null)
                    .commit();
        });
        /* ────────────────────────────────────────────────────────── */

        recyclerViewNews.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewNews.setAdapter(newsAdapter);


        return v;
    }
}

