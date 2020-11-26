package com.petabyte.plate.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.petabyte.plate.R;
import com.petabyte.plate.data.BookmarkCardViewData;
import com.petabyte.plate.ui.activity.DetailActivity;
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

        private TextView diningTitle;
        private TextView diningSubtitle;
        private TextView diningDate;
        private TextView diningTime;
        private TextView diningLocation;
        private TextView diningDetailLocation;
        private ImageView diningImage;
        private CheckBox checkBox;
        private DatabaseReference databaseReference;
        private Context context;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            context = itemView.getContext();

            diningTitle = (TextView) itemView.findViewById(R.id.dining_title_bookmarkcard);
            diningSubtitle = (TextView) itemView.findViewById(R.id.dining_subtitle_bookmarkcard);
            diningDate = (TextView) itemView.findViewById(R.id.dining_date_bookmarkcard);
            diningTime = (TextView) itemView.findViewById(R.id.dining_time_bookmarkcard);
            diningLocation = (TextView) itemView.findViewById(R.id.dining_location_bookmarkcard);
            diningDetailLocation = (TextView) itemView.findViewById(R.id.dining_detail_location_bookmarkcard);
            diningImage = (ImageView) itemView.findViewById(R.id.dining_image_bookmarkcard);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox_bookmarkcard);

            databaseReference = FirebaseDatabase.getInstance().getReference();

            diningTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), DetailActivity.class);
                    intent.putExtra("title", diningTitle.getText().toString());
                    v.getContext().startActivity(intent);
                }
            });

            diningLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//주소 textview를 누르면 google maps 어플리케이션으로 연결
                    String address = diningLocation.getText().toString().replace(' ', '+');
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q="+address));
                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    v.getContext().startActivity(intent);
                }
            });

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

            diningTitle.setText(data.getDiningTitle());
            diningSubtitle.setText(data.getDiningSubtitle());
            diningDate.setText(data.getDiningDate());
            diningTime.setText(data.getDiningTime());
            diningLocation.setText(data.getDiningLocation());
            diningDetailLocation.setText(data.getDiningDetailLocation());
            GradientDrawable drawable = (GradientDrawable)context.getDrawable(R.drawable.image_radius);
            diningImage.setBackground(drawable);
            diningImage.setClipToOutline(true);
            Picasso.get().load(("https://firebasestorage.googleapis.com/v0/b/plate-f5144.appspot.com/o/chef.png?alt=media&token=8e789939-497e-4b18-95f4-e526f50e7917")).fit().centerCrop().into(diningImage);
        }
    }
}
