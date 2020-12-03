package com.petabyte.plate.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Constraints;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.petabyte.plate.R;
import com.petabyte.plate.adapter.HomePostListAdapter;
import com.petabyte.plate.adapter.ImageSlideAdapter;
import com.petabyte.plate.data.HomeAwardsData;
import com.petabyte.plate.data.ImageSlideData;
import com.petabyte.plate.utils.LogTags;

import java.util.ArrayList;
import java.util.List;

public class HomeAwardsList extends ConstraintLayout {

    private RecyclerView recyclerView;
    private HomePostListAdapter awardAdapter;
    private ImageSlideAdapter slideAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private TextView titleText;
    private ConstraintLayout mainLayout;

    private StorageReference mStorage;

    private TYPE_MODE type;

    public enum TYPE_MODE {
        POST_MODE,
        IMAGE_SLIDE_MODE
    }

    public HomeAwardsList(@NonNull Context context) {
        super(context);
        initViews();
    }

    public HomeAwardsList(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public HomeAwardsList(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    public HomeAwardsList(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initViews();
    }

    public void initViews() {
        inflate(getContext(), R.layout.view_homeawardslist, this);

        mStorage = FirebaseStorage.getInstance("gs://plate-f5144.appspot.com/").getReference();

        titleText = (TextView)this.findViewById(R.id.title_tv_v_homeawards);
        recyclerView = (RecyclerView)this.findViewById(R.id.recycler_view_v_awards);
        mainLayout = (ConstraintLayout)this.findViewById(R.id.layout_v_awards);

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
    }

    public void setType(TYPE_MODE type) {
        // 타입을 설정
        this.type = type;

        if (type == TYPE_MODE.POST_MODE) {

            // POST_MODE면
            // HomePostListAdapter를 Adapter로 설정
            awardAdapter = new HomePostListAdapter();
            awardAdapter.setReference(mStorage);
            recyclerView.setAdapter(awardAdapter);
        } else if (type == TYPE_MODE.IMAGE_SLIDE_MODE){

            // IMAGES_SLIDE_MODE면,
            // Slider로 만들기 위해 PageSnapHelper 사용
            SnapHelper snapHelper = new PagerSnapHelper();
            snapHelper.attachToRecyclerView(recyclerView);

            // ImageSliderAdapter를 Adapter로 사용
            slideAdapter = new ImageSlideAdapter();
            slideAdapter.setReference(mStorage);
            recyclerView.setAdapter(slideAdapter);
        }
    }

    public void removePadding() {
        recyclerView.setPadding(0, 0, 0, 0);
    }

    public void setTitle(String title) {
        titleText.setText(title);
    }

    public void hideTitle() {
        titleText.setVisibility(GONE);
    }

    public void setBackgroundColor(final int backgroundColor, final int textColor) {
        mainLayout.setBackgroundColor(backgroundColor);
        titleText.setTextColor(textColor);
    }

    public void setMarginTop(final int top) {
        titleText.setPadding(0, top, 0, 5);
    }

    public void setMarginBottom(final int bottom) {
        recyclerView.setPadding(15, 0, 15, bottom);
    }

    public void addData(HomeAwardsData data) {
        awardAdapter.addItem(data);
        awardAdapter.notifyDataSetChanged();
    }

    public void addData(ImageSlideData data) {
        slideAdapter.addData(data);
        slideAdapter.notifyDataSetChanged();
    }

    public void removeAllData() {
        if (type == TYPE_MODE.POST_MODE) {
            awardAdapter.removeAllItem();
            awardAdapter.notifyDataSetChanged();
        } else if (type == TYPE_MODE.IMAGE_SLIDE_MODE){
            slideAdapter.removeAllData();
            slideAdapter.notifyDataSetChanged();
        }
    }


}
