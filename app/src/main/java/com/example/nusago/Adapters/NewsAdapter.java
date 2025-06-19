package com.example.nusago.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.nusago.Models.News;
import com.example.nusago.R;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.CardViewHolder> {

    /* ────────────── field utama ────────────── */
    private final Context context;
    private final List<News> newsList;
    private final String userRole;               // <-- admin / user
    private OnItemClickListener clickListener;
    private AdminActionListener adminListener;

    /* ────────────── listener umum ───────────── */
    public interface OnItemClickListener { void onItemClick(int newsId); }
    public void setOnItemClickListener(OnItemClickListener l) { this.clickListener = l; }

    /* ────────────── listener admin ──────────── */
    public interface AdminActionListener {
        void onEdit(int newsId);
        void onDelete(int newsId);
    }
    public void setAdminActionListener(AdminActionListener l) { this.adminListener = l; }

    /* ────────────── konstruktor ─────────────── */
    public NewsAdapter(Context context, List<News> newsList, String userRole) {
        this.context   = context;
        this.newsList  = newsList;
        this.userRole  = userRole;  // simpan role
    }

    /* ────────────── adapter std. ────────────── */
    @NonNull @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_news, parent, false);
        return new CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder h, int pos) {
        News item = newsList.get(pos);

        h.title.setText(item.getTitle());
        h.description.setText(item.getDescription());
        h.date.setText(item.getCreatedAt());

        Glide.with(context)
                .load(item.getImage())
                .transform(new RoundedCorners(30))
                .into(h.imageView);

        /* tampilkan / sembunyikan tombol admin */
        if ("admin".equalsIgnoreCase(userRole)) {
            h.date.setVisibility(View.GONE);
            h.adminButtons.setVisibility(View.VISIBLE);
        } else {
            h.date.setText(item.getCreatedAt());
            h.date.setVisibility(View.VISIBLE);
            h.adminButtons.setVisibility(View.GONE);
        }

        /* tombol edit */
        h.btnEdit.setOnClickListener(v -> {
            if (adminListener != null) adminListener.onEdit(item.getId());
        });

        /* tombol delete */
        h.btnDelete.setOnClickListener(v -> {
            if (adminListener != null) adminListener.onDelete(item.getId());
        });
    }

    @Override public int getItemCount() { return newsList.size(); }

    /* ────────────── ViewHolder ──────────────── */
    class CardViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title, description, date;
        LinearLayout adminButtons;
        Button btnEdit, btnDelete;

        CardViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView     = itemView.findViewById(R.id.image_card);
            title         = itemView.findViewById(R.id.title_card);
            description   = itemView.findViewById(R.id.desc_card);
            date          = itemView.findViewById(R.id.date_card);

            adminButtons  = itemView.findViewById(R.id.admin_buttons);
            btnEdit       = itemView.findViewById(R.id.btn_edit);
            btnDelete     = itemView.findViewById(R.id.btn_delete);

            /* klik item normal */
            itemView.setOnClickListener(v -> {
                int p = getAdapterPosition();
                if (clickListener != null && p != RecyclerView.NO_POSITION) {
                    clickListener.onItemClick(newsList.get(p).getId());
                }
            });
        }
    }

    public void removeAt(int position) {
        newsList.remove(position);
        notifyItemRemoved(position);
    }

    public void removeById(int id) {
        for (int i = 0; i < newsList.size(); i++) {
            if (newsList.get(i).getId() == id) {
                newsList.remove(i);
                notifyItemRemoved(i);
                return;
            }
        }
    }

}
