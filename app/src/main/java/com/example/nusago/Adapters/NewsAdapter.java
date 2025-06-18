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

    private Context context;
    private List<News> cardList;

    public NewsAdapter(Context context, List<News> cardList) {
        this.context = context;
        this.cardList = cardList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_news, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        News item = cardList.get(position);
        Glide.with(context)
                .load(item.getImage())
                .transform(new RoundedCorners(30))
                .into(holder.imageView);
        holder.title.setText(item.getTitle());
        holder.description.setText(item.getDescription());
        holder.date.setText(item.getCreatedAt());
//        holder.location.setText(item.getUserId());
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title, description, date, location;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_card);
            title = itemView.findViewById(R.id.title_card);
            description = itemView.findViewById(R.id.desc_card);
            date = itemView.findViewById(R.id.date_card);
            location = itemView.findViewById(R.id.location_card);
        }
    }
}
