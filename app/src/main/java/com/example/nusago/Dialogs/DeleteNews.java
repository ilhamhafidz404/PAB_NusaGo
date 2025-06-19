package com.example.nusago.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nusago.Adapters.NewsAdapter;
import com.example.nusago.Fetcher.NewsFetcher;
import com.example.nusago.R;

public class DeleteNews extends DialogFragment {

    private static final String ARG_ID   = "news_id";
    private static final String ARG_POS  = "adapter_pos";

    /* factory method */
    public static DeleteNews newInstance(int newsId, int adapterPos) {
        DeleteNews d = new DeleteNews();
        Bundle b = new Bundle();
        b.putInt(ARG_ID,  newsId);
        b.putInt(ARG_POS, adapterPos);
        d.setArguments(b);
        return d;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        int id  = getArguments().getInt(ARG_ID);
        int pos = getArguments().getInt(ARG_POS);

        return new AlertDialog.Builder(requireContext())
                .setTitle("Hapus Berita")
                .setMessage("Yakin ingin menghapus berita ini?")
                .setPositiveButton("Hapus", (d, which) -> {
                    NewsFetcher.deleteNews(id, new NewsFetcher.DeleteCallback() {
                        @Override public void onSuccess(String res) {
                            requireActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "Berhasil dihapus", Toast.LENGTH_SHORT).show();
                                // hapus dari adapter
                                RecyclerView rv = requireActivity().findViewById(R.id.recyclerViewNews);
                                NewsAdapter adapter = (NewsAdapter) rv.getAdapter();
                                if (adapter != null) {
                                    adapter.removeAt(pos);
                                }
                            });
                        }
                        @Override public void onError(Exception e) {
                            requireActivity().runOnUiThread(() ->
                                    Toast.makeText(getContext(), "Gagal hapus", Toast.LENGTH_SHORT).show());
                        }
                    });
                })
                .setNegativeButton("Batal", null)
                .create();
    }
}
