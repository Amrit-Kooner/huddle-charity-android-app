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

public class CharityAdapter extends RecyclerView.Adapter<CharityAdapter.MyViewHolder> {
    private List<CharityClass> list;
    private CharityAdapter.ItemClickListener clickListener;

    public CharityAdapter(List<CharityClass> list, CharityAdapter.ItemClickListener clickListener) {
        this.list = list;
        this.clickListener  = clickListener;
    }

    @NonNull
    @Override
    public CharityAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_charity, parent, false);
        return new CharityAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CharityAdapter.MyViewHolder holder, int position) {
        holder.char_title.setText(list.get(position).getTitle());
        holder.char_bio.setText(list.get(position).getBio());

        String imageUrl = list.get(position).getImage();
        Uri uri = Uri.parse(imageUrl);
        Picasso.get().load(uri).into(holder.char_image);

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
        ImageView char_image;
        TextView char_title;
        TextView char_bio;
        public MyViewHolder(View view) {
            super(view);
            char_image = view.findViewById(R.id.charImage);
            char_title = view.findViewById(R.id.charTitle);
            char_bio = view.findViewById(R.id.charBio);
        }
    }
    public interface ItemClickListener {

        public void onItemClick(CharityClass charity);
    }
}
