package com.example.nusago.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nusago.Models.News;
import com.example.nusago.R;

import java.util.ArrayList;

public class AdminNewsAdapter extends RecyclerView.Adapter<AdminNewsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<News> newsList;

    public interface OnActionListener {
        void onUpdate(News news);
        void onDelete(News news);
    }

    private OnActionListener actionListener;

    public AdminNewsAdapter(Context context, ArrayList<News> newsList, OnActionListener listener) {
        this.context = context;
        this.newsList = newsList;
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_news_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        News news = newsList.get(position);
        holder.tvTitle.setText(news.getTitle());
        holder.tvDescription.setText(news.getDescription());

        holder.btnUpdate.setOnClickListener(v -> actionListener.onUpdate(news));
        holder.btnDelete.setOnClickListener(v -> actionListener.onDelete(news));
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription;
        Button btnUpdate, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDescription = itemView.findViewById(R.id.tv_description);
            btnUpdate = itemView.findViewById(R.id.btn_update);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}

