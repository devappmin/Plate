package com.petabyte.plate.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.StorageReference;
import com.petabyte.plate.R;
import com.petabyte.plate.data.HomeCardData;
import com.petabyte.plate.ui.activity.DetailActivity;
import com.petabyte.plate.utils.GlideApp;

import java.util.ArrayList;
import java.util.Locale;

public class MoreListAdapter extends RecyclerView.Adapter<MoreListAdapter.ViewHolder> {

    private ArrayList<HomeCardData> datum;
    private StorageReference reference;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_more_list_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBind(datum.get(position), reference);
    }

    @Override
    public int getItemCount() {
        return datum.size();
    }

    public void setDatum(ArrayList<HomeCardData> datum) {
        this.datum = datum;
    }

    public void setReference(StorageReference reference) {
        this.reference = reference;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView titleTextView;
        private TextView subtitleTextView;
        private TextView descriptionTextView;
        private TextView priceTextView;

        private View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView)itemView.findViewById(R.id.image_v_v_more_list_card);
            titleTextView = (TextView)itemView.findViewById(R.id.title_tv_v_more_list_card);
            subtitleTextView = (TextView)itemView.findViewById(R.id.subtitle_tv_v_more_list_card);
            descriptionTextView = (TextView)itemView.findViewById(R.id.description_tv_v_more_list_card);
            priceTextView = (TextView)itemView.findViewById(R.id.price_tv_v_more_list_card);

            view = itemView;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(view.getContext(), DetailActivity.class);
                    intent.putExtra("title", titleTextView.getText().toString());
                    view.getContext().startActivity(intent);
                }
            });
        }

        @SuppressLint("SetTextI18n")
        private void onBind(HomeCardData data, StorageReference reference) {
            titleTextView.setText(data.getTitle());
            subtitleTextView.setText(data.getSubtitle());
            descriptionTextView.setText(data.getDescription());
            priceTextView.setText(String.format(Locale.KOREA, "%,d", data.getPrice()) + "원");

            GlideApp.with(view.getContext()).load(reference.child("dining/" + data.getImageUri())).fitCenter().centerCrop().into(imageView);
        }
    }
}
