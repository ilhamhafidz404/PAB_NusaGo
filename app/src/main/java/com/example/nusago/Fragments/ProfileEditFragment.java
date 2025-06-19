package com.example.nusago.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.nusago.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ProfileEditFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile_edit, container, false);

        FloatingActionButton btnEdit = view.findViewById(R.id.fab_back_edit_account);

        btnEdit.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }
}
