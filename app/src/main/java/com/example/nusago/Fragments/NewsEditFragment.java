package com.example.nusago.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nusago.Fetcher.NewsFetcher;
import com.example.nusago.Models.News;
import com.example.nusago.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class NewsEditFragment extends Fragment {

    private EditText etTitle, etDescription, etBody, etImageUrl;
    private int newsId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_news_edit, container, false);

        etTitle       = view.findViewById(R.id.et_title);
        etDescription = view.findViewById(R.id.et_description);
        etBody        = view.findViewById(R.id.et_body);
        etImageUrl    = view.findViewById(R.id.et_image_url);
        View btnSubmit = view.findViewById(R.id.btn_submit);

        FloatingActionButton fabBack = view.findViewById(R.id.fab_back);
        fabBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        // Ambil ID dari arguments
        if (getArguments() != null) {
            newsId = getArguments().getInt("news_id", 0);
        }

        if (newsId != 0) {
            fetchNewsDetail(newsId);
        }

        btnSubmit.setOnClickListener(v -> submitEdit());

        return view;
    }

    private void fetchNewsDetail(int id) {
        NewsFetcher.getNewsById(id, new NewsFetcher.SingleCallback() {
            @Override
            public void onResult(News news) {
                requireActivity().runOnUiThread(() -> {
                    etTitle.setText(news.getTitle());
                    etDescription.setText(news.getDescription());
                    etBody.setText(news.getBody());
                    etImageUrl.setText(news.getImage());
                });
            }

            @Override
            public void onError(Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Gagal mengambil data berita", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private void submitEdit() {
        String title = etTitle.getText().toString().trim();
        String desc  = etDescription.getText().toString().trim();
        String body  = etBody.getText().toString().trim();
        String image = etImageUrl.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(desc) || TextUtils.isEmpty(body)) {
            Toast.makeText(getContext(), "Judul, deskripsi, dan isi berita wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE);
        int userId = prefs.getInt("id", 0);

        NewsFetcher.updateNews(newsId, title, desc, body, image, userId, new NewsFetcher.PostCallback() {
            @Override
            public void onSuccess(String message) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Berita berhasil diperbarui", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack(); // kembali ke detail/home
                });
            }

            @Override
            public void onError(Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Gagal memperbarui berita", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
}
