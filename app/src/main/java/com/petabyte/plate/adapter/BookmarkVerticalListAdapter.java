package com.petabyte.plate.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.petabyte.plate.R;
import com.petabyte.plate.data.BookmarkCardViewData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class BookmarkVerticalListAdapter extends RecyclerView.Adapter<BookmarkVerticalListAdapter.ViewHolder>{
    private List<BookmarkCardViewData> datas = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_bookmarkcard, parent, false);
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

    public void addItem(BookmarkCardViewData data) {
        datas.add(data);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView diningName;
        private TextView diningDate;
        private TextView diningLocation;
        private ImageView imageView;
        private CheckBox checkBox;
        private DatabaseReference databaseReference;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            diningName = (TextView) itemView.findViewById(R.id.dining_name_bookmarkcard);
            diningDate = (TextView) itemView.findViewById(R.id.dining_date_bookmarkcard);
            diningLocation = (TextView) itemView.findViewById(R.id.dining_location_bookmarkcard);
            imageView = (ImageView) itemView.findViewById(R.id.image_view_bookmarkcard);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox_bookmarkcard);

            databaseReference = FirebaseDatabase.getInstance().getReference();

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        Log.d("Checked", "true");
                    } else {
                        Log.d("Checked", "false");
                    }
                }
            });


        }

        private void onBind(BookmarkCardViewData data) {

            diningName.setText(data.getDiningName());
            diningDate.setText(data.getDiningDate());
            diningLocation.setText(data.getDiningLocation());
            Picasso.get().load(("https://firebasestorage.googleapis.com/v0/b/plate-f5144.appspot.com/o/food.png?alt=media&token=99a94b9e-10eb-4eeb-93cb-c79061a8ebc5")).fit().centerCrop().into(imageView);
        }
    }
}
