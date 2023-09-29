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

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {
    private List<NewsClass> list;
    private NewsAdapter.ItemClickListener clickListener;

    public NewsAdapter(List<NewsClass> list, NewsAdapter.ItemClickListener clickListener) {
        this.list = list;
        this.clickListener  = clickListener;
    }

    @NonNull
    @Override
    public NewsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
        return new NewsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsAdapter.MyViewHolder holder, int position) {
        holder.news_title.setText(list.get(position).getTitle());
        holder.news_bio.setText(list.get(position).getBio());

        String imageUrl = list.get(position).getImage();
        Uri uri = Uri.parse(imageUrl);
        Picasso.get().load(uri).into(holder.news_image);

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
        ImageView news_image;
        TextView news_title;
        TextView news_bio;
        public MyViewHolder(View view) {
            super(view);
            news_image = view.findViewById(R.id.newsImage);
            news_title = view.findViewById(R.id.newsTitle);
            news_bio = view.findViewById(R.id.newsBio);
        }
    }
    public interface ItemClickListener {
        public void onItemClick(NewsClass news);
    }
}