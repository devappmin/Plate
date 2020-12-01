package com.petabyte.plate.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.StorageReference;
import com.petabyte.plate.R;
import com.petabyte.plate.data.ImageSlideData;
import com.petabyte.plate.utils.GlideApp;
import com.petabyte.plate.utils.LogTags;

import java.util.ArrayList;
import java.util.List;

public class ImageSlideAdapter extends RecyclerView.Adapter<ImageSlideAdapter.ViewHolder> {

    private StorageReference reference;
    private List<ImageSlideData> datum = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_image_slide, parent, false);
        return new ImageSlideAdapter.ViewHolder(v);
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

    public void addData(ImageSlideData data) {
        datum.add(data);
    }

    public void removeAllData() {
        datum = new ArrayList<>();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        private View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.itemView = itemView;

            imageView = (ImageView)itemView.findViewById(R.id.image_v_image_slide);
        }

        public void onBind(ImageSlideData data, StorageReference reference) {
            Log.d(LogTags.IMPORTANT, data.getImage());
            GlideApp.with(itemView.getContext()).load(reference.child(data.getImage())).centerCrop().into(imageView);
        }
    }
}
