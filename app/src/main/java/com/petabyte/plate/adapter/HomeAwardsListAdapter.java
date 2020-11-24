package com.petabyte.plate.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.petabyte.plate.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HomeAwardsListAdapter extends RecyclerView.Adapter<HomeAwardsListAdapter.ViewHolder> {

    private List<String> datas = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_homecard_small, parent, false);
        return new HomeAwardsListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBind(datas.get(position));
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public void addItem(String str) {
        datas.add(str);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = (ImageView)itemView.findViewById(R.id.image_v_homecard_small);
        }

        public void onBind(String uri) {
            Picasso.get().load(uri).centerCrop().fit().into(imageView);
        }
    }
}
