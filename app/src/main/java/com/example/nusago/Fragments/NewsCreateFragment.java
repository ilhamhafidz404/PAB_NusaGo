package com.example.nusago.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nusago.Fetcher.NewsFetcher;
import com.example.nusago.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class NewsCreateFragment extends Fragment {

    private EditText etTitle, etDescription, etBody, etImageUrl;

    private CheckBox cbWithEvent;
    private Button btnSubmit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_news_create, container, false);

        etTitle       = view.findViewById(R.id.et_title);
        etDescription = view.findViewById(R.id.et_description);
        etBody        = view.findViewById(R.id.et_body);
        etImageUrl    = view.findViewById(R.id.et_image_url);
        btnSubmit     = view.findViewById(R.id.btn_submit);
        cbWithEvent = view.findViewById(R.id.cb_with_event);


        btnSubmit.setOnClickListener(v -> submitNews());

        FloatingActionButton fabBack = view.findViewById(R.id.fab_back);
        fabBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        return view;
    }

    private void submitNews() {
        String title = etTitle.getText().toString().trim();
        String desc  = etDescription.getText().toString().trim();
        String body  = etBody.getText().toString().trim();
        String image = etImageUrl.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(desc) || TextUtils.isEmpty(body)) {
            Toast.makeText(getContext(), "Judul, deskripsi, dan isi berita wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = requireActivity().getSharedPreferences("auth", Context.MODE_PRIVATE);
        int userId = prefs.getInt("id", 0);

        // Handle checkbox "With Event"
        int withEvent = cbWithEvent.isChecked() ? 1 : 0;

        NewsFetcher.postNews(title, desc, body, image, userId, withEvent, new NewsFetcher.PostCallback() {
            @Override
            public void onSuccess(String message) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Berita berhasil disimpan", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                });
            }

            @Override
            public void onError(Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Gagal menyimpan berita", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

}
