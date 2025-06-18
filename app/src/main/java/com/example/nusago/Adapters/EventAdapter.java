package com.example.nusago.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.nusago.Models.Event;
import com.example.nusago.R;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<Event> events;

    public EventAdapter(List<Event> events) {
        this.events = events;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, desc, date, location;

        public EventViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_card);
            title = itemView.findViewById(R.id.title_card);
            desc = itemView.findViewById(R.id.desc_card);
            date = itemView.findViewById(R.id.date_card);
            location = itemView.findViewById(R.id.location_card);
        }
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_event, parent, false);
        return new EventViewHolder(v);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.title.setText(event.getTitle());
        holder.desc.setText(event.getDescription());
        holder.date.setText(event.getDate());
        holder.location.setText(event.getLocation());
        holder.image.setImageResource(event.getImageResId());
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}

