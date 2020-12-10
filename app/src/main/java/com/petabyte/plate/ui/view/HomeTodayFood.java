package com.petabyte.plate.ui.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.petabyte.plate.R;
import com.petabyte.plate.ui.activity.DetailActivity;
import com.petabyte.plate.utils.GlideApp;
import com.petabyte.plate.utils.LogTags;

public class HomeTodayFood extends CardView {

    private ImageView imageView;
    private TextView titleTextView;
    private View opacityView;

    private StorageReference mStorage;

    public HomeTodayFood(@NonNull Context context) {
        super(context);
        initView();
    }

    public HomeTodayFood(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public HomeTodayFood(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.view_home_today_food, this);

        // 뷰를 불러온다.
        imageView = (ImageView)findViewById(R.id.image_v_today_food);
        titleTextView = (TextView)findViewById(R.id.title_v_today_food);
        opacityView = (View)findViewById(R.id.background_v_today_food);

        // 데이터베이스를 불러온다.
        mStorage = FirebaseStorage.getInstance("gs://plate-f5144.appspot.com/").getReference();
    }

    public void setBackground(String color) {
        opacityView.setBackgroundColor(Color.parseColor(color));
    }

    public void launch(final String title, final String image, final String diningUid) {
        titleTextView.setText(title);
        Log.i(LogTags.IMPORTANT, title +" , " + image);
        GlideApp.with(this).load(mStorage.child("dining/" + image)).centerCrop().into(imageView);

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("diningUid", diningUid);
                getContext().startActivity(intent);
            }
        });
    }
}
