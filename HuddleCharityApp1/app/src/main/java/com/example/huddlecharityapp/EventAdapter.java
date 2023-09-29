package com.example.huddlecharityapp;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.MyViewHolder> {
    private List<EventClass> list;
    private EventAdapter.ItemClickListener clickListener;

    public EventAdapter(List<EventClass> list, EventAdapter.ItemClickListener clickListener) {
        this.list = list;
        this.clickListener  = clickListener;
    }

    @NonNull
    @Override
    public EventAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.MyViewHolder holder, int position) {
        holder.event_title.setText(list.get(position).getTitle());
        holder.event_bio.setText(list.get(position).getBio());

        String imageUrl = list.get(position).getImage();
        Uri uri = Uri.parse(imageUrl);
        Picasso.get().load(uri).into(holder.event_image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClick(list.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView event_image;
        TextView event_title;
        TextView event_bio;
        public MyViewHolder(View view) {
            super(view);
            event_image = view.findViewById(R.id.eventImage);
            event_title = view.findViewById(R.id.eventTitle);
            event_bio = view.findViewById(R.id.eventBio);
        }
    }
    public interface ItemClickListener {
        public void onItemClick(EventClass event);
    }
}
