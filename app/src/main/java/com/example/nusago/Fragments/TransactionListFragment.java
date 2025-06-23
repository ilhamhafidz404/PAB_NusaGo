package com.example.nusago.Fragments;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.*;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import com.example.nusago.Adapters.TransactionAdapter;
import com.example.nusago.Fetcher.TransactionFetcher;
import com.example.nusago.Models.Transaction;
import com.example.nusago.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TransactionListFragment extends Fragment {

    private RecyclerView rv;
    private TransactionAdapter adapter;
    private final List<Transaction> data = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_transaction_list, container, false);

        FloatingActionButton btnExport = v.findViewById(R.id.fab_export_transaction);
        btnExport.setOnClickListener(view -> exportTransaction());

        rv = v.findViewById(R.id.recyclerViewTransactions);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TransactionAdapter(getContext(), data);
        rv.setAdapter(adapter);

        fetchTransactions();
        return v;
    }

    private void exportTransaction() {
        String url = "https://nusago.alope.id/export-transaction"; // pastikan sesuai route
        String fileName = "transactions_" + System.currentTimeMillis() + ".xls";

        DownloadManager.Request req = new DownloadManager.Request(Uri.parse(url));
        req.setTitle("Export Transaksi");
        req.setDescription("Mengunduh file transaksi…");
        req.setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        req.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS, fileName);
        req.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE);

        DownloadManager dm =
                (DownloadManager) requireContext().getSystemService(Context.DOWNLOAD_SERVICE);
        dm.enqueue(req);

        Toast.makeText(getContext(), "Export dimulai… File akan tersimpan di Downloads",
                Toast.LENGTH_SHORT).show();
    }


    private void fetchTransactions() {
        new TransactionFetcher(new TransactionFetcher.Callback() {
            @Override
            public void onResult(ArrayList<Transaction> list) {
                requireActivity().runOnUiThread(() -> {
                    data.clear();
                    data.addAll(list);
                    adapter.notifyDataSetChanged();
                });
            }
            @Override
            public void onError(Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Gagal memuat transaksi", Toast.LENGTH_SHORT).show());
            }
        }).execute();
    }
}
