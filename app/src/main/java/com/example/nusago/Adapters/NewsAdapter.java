package com.example.nusago.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.nusago.Models.News;
import com.example.nusago.R;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.CardViewHolder> {

    /* ------------------------------------------------------------------ */
    /* Konstruktor & field                                                */
    /* ------------------------------------------------------------------ */
    private final Context context;
    private final List<News> cardList;           // daftar berita
    private OnItemClickListener listener;        // listener klik

    public interface OnItemClickListener {
        void onItemClick(int newsId);            // kirim ID
    }
    public void setOnItemClickListener(OnItemClickListener l) {
        this.listener = l;
    }

    public NewsAdapter(Context context, List<News> cardList) {
        this.context  = context;
        this.cardList = cardList;
    }

    /* ------------------------------------------------------------------ */
    /* ViewHolder                                                         */
    /* ------------------------------------------------------------------ */
    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_news, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        News item = cardList.get(position);

        // judul, deskripsi, tanggal
        holder.title.setText(item.getTitle());
        holder.description.setText(item.getDescription());
        holder.date.setText(item.getCreatedAt());

        // gambar (Glide)
        Glide.with(context)
                .load(item.getImage())                  // URL atau drawable
                .transform(new RoundedCorners(30))
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    /* ------------------------------------------------------------------ */
    /* Inner class ViewHolder                                             */
    /* ------------------------------------------------------------------ */
    public class CardViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView  title, description, date, location;   // location opsional

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView    = itemView.findViewById(R.id.image_card);
            title        = itemView.findViewById(R.id.title_card);
            description  = itemView.findViewById(R.id.desc_card);
            date         = itemView.findViewById(R.id.date_card);
            location     = itemView.findViewById(R.id.location_card);

            /*  ───── Klik item ───── */
            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (listener != null
                        && pos != RecyclerView.NO_POSITION) {
                    int newsId = cardList.get(pos).getId();   // ← kirim ID
                    listener.onItemClick(newsId);
                }
            });
        }
    }
}
