package com.petabyte.plate.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.StorageReference;
import com.petabyte.plate.R;
import com.petabyte.plate.data.HomeAwardsData;
import com.petabyte.plate.ui.activity.ScrollableImageActivity;
import com.petabyte.plate.utils.GlideApp;

import java.util.ArrayList;
import java.util.List;

public class HomePostListAdapter extends RecyclerView.Adapter<HomePostListAdapter.ViewHolder> {

    private List<HomeAwardsData> datum = new ArrayList<>();
    private StorageReference reference;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_homecard_small, parent, false);
        return new HomePostListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBind(datum.get(position), reference);
    }

    @Override
    public int getItemCount() {
        return datum.size();
    }

    public void setReference(StorageReference reference) {
        this.reference = reference;
    }

    public void addItem(HomeAwardsData data) {
        datum.add(data);
    }

    public void removeAllItem() {
        datum = new ArrayList<>();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private CardView cardView;

        private HomeAwardsData data;

        private View itemView;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            this.itemView = itemView;

            imageView = (ImageView)itemView.findViewById(R.id.image_v_homecard_small);
            cardView = (CardView)itemView.findViewById(R.id.card_v_homecard_small);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), ScrollableImageActivity.class);
                    intent.putExtra("Title", data.getTitle());
                    intent.putExtra("ImageUri", data.getMainImage());
                    itemView.getContext().startActivity(intent);
                }
            });
        }

        public void onBind(HomeAwardsData data, StorageReference reference) {
            this.data = data;

            //Picasso.get().load(data.getMainImage()).centerCrop().fit().into(imageView);
            GlideApp.with(itemView.getContext()).load(reference.child(data.getCardImage())).into(imageView);
        }
    }
}
