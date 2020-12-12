package com.petabyte.plate.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
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
import com.petabyte.plate.data.ResultDetailData;
import com.petabyte.plate.ui.activity.DetailActivity;
import com.petabyte.plate.utils.GlideApp;
import com.petabyte.plate.utils.LogTags;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

public class ResultBottomSheetListAdapter extends RecyclerView.Adapter<ResultBottomSheetListAdapter.ViewHolder> {

    private List<ResultDetailData> datum = new ArrayList<>();
    private StorageReference storageReference;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_result_bottom_sheet_card, parent, false);

        storageReference = FirebaseStorage.getInstance().getReference();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBind(datum.get(position), storageReference);
    }

    @Override
    public int getItemCount() {
        return datum.size();
    }

    public void addData(ResultDetailData data) {
        datum.add(data);
    }

    public void removeAllData() {
        datum = new ArrayList<>();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView titleTextView;
        private TextView locationTextView;
        private TextView priceTextView;

        private View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.itemView = itemView;

            imageView = (ImageView)itemView.findViewById(R.id.image_v_bs_result_card);
            titleTextView = (TextView)itemView.findViewById(R.id.title_tv_bs_result_card);
            locationTextView = (TextView)itemView.findViewById(R.id.location_tv_bs_result_card);
            priceTextView = (TextView)itemView.findViewById(R.id.price_tv_bs_result_card);
        }

        @SuppressLint("SetTextI18n")
        public void onBind(final ResultDetailData data, StorageReference storageReference) {
            titleTextView.setText(data.getTitle());
            locationTextView.setText(data.getLocation().get("location") + "\n" + data.getLocation().get("detail"));
            priceTextView.setText(String.format(Locale.KOREA, "%,d", data.getPrice()) + "원");
            GlideApp.with(itemView.getContext()).load(storageReference.child("dining/" + data.getDiningUID() + "/" + data.getImages().get(1))).fitCenter().centerCrop().into(imageView);
            Log.d(LogTags.IMPORTANT,"dining/" + data.getDiningUID() + "/" + data.getImages().get(0));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), DetailActivity.class);
                    intent.putExtra("title", data.getTitle());
                    intent.putExtra("diningUid", data.getDiningUID());
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}
