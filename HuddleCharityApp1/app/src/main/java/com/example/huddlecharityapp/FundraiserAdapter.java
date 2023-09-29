package com.example.huddlecharityapp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;


public class FundraiserAdapter extends RecyclerView.Adapter<FundraiserAdapter.MyViewHolder> {
    private List<FundraiserClass> list;
    private ItemClickListener clickListener;


    public FundraiserAdapter(List<FundraiserClass> list, ItemClickListener clickListener) {
        this.list = list;
        this.clickListener  = clickListener;

    }
    @NonNull
    @Override
    public FundraiserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fundraiser, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FundraiserAdapter.MyViewHolder holder, int position) {

        holder.fund_title.setText(list.get(position).getTitle());
        holder.fund_bio.setText(list.get(position).getBio());
        holder.fund_end.setText(list.get(position).getEndDate());
        holder.fund_target.setText("£" + list.get(position).getTarget());
        holder.fund_current.setText("£" + String.valueOf(list.get(position).getCurrent()));

        holder.progressBar.setMax(Integer.parseInt(list.get(position).getTarget()));
        holder.progressBar.setProgress(list.get(position).getCurrent());

        String imageUrl = list.get(position).getImage();
        Uri uri = Uri.parse(imageUrl);
        Picasso.get().load(uri).into(holder.fund_image);

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
        ImageView fund_image;
        TextView fund_title;
        TextView fund_bio;
        TextView fund_end;
        TextView fund_target;
        TextView fund_current;
        ProgressBar progressBar;

        public MyViewHolder(View view) {
            super(view);
            progressBar = view.findViewById(R.id.progressBar);
            fund_image = view.findViewById(R.id.fundImage);
            fund_title = view.findViewById(R.id.fundTitle);
            fund_bio = view.findViewById(R.id.fundBio);
            fund_end = view.findViewById(R.id.fundEnd);
            fund_target = view.findViewById(R.id.goalValue);
            fund_current = view.findViewById(R.id.currentValue);
        }
    }
    public interface ItemClickListener {

        public void onItemClick(FundraiserClass fundraiser);
    }
}


