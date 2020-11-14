package com.petabyte.plate.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.petabyte.plate.R;
import com.petabyte.plate.data.HomeCardData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HomeHorizontalListAdapter extends RecyclerView.Adapter<HomeHorizontalListAdapter.ViewHolder> {

    private List<HomeCardData> datas = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_homecard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBind(datas.get(position));
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public void addItem(HomeCardData data) {
        datas.add(data);
    }

    /**
     * Inner class that extends RecyclerView.ViewHolder
     */
    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView titleTextView;
        private TextView descriptionTextView;
        private ImageView imageView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = (TextView)itemView.findViewById(R.id.title_tv_v_homecard);
            descriptionTextView = (TextView)itemView.findViewById(R.id.description_tv_v_homecard);
            imageView = (ImageView)itemView.findViewById(R.id.image_v_homecard);

            // Add CardView's ClickListener here..
        }

        private void onBind(HomeCardData data) {

            titleTextView.setText(data.getTitle());
            descriptionTextView.setText(data.getDescription());
            Picasso.get().load(("https://firebasestorage.googleapis.com/v0/b/plate-f5144.appspot.com/o/food.png?alt=media&token=99a94b9e-10eb-4eeb-93cb-c79061a8ebc5")).fit().centerCrop().into(imageView);
        }
    }
}
