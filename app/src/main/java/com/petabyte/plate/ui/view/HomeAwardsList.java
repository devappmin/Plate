package com.petabyte.plate.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
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
    private LinearLayoutManager layoutManager;

    private TextView titleText;
    private ConstraintLayout mainLayout;

    private StorageReference mStorage;

    private Handler handler;
    private Runnable runnable;

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

        // 해당 RecyclerView를 터치한 뒤 위/아래로 스크롤하면 Collapsing Toolbar가 제대로 닫히지 않는 문제 해결
        recyclerView.setNestedScrollingEnabled(false);

        // RecyclerView에 LayoutManager 연결
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

            // Runnable을 통해서 이미지 슬라이더를 자동으로 스크롤
            if(handler == null) {
                // Handler와 Runnable을 초기화
                handler = new Handler();
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        // 만약에 현재 위치의 아이템이 마지막 아이템이 아니라면
                        if (layoutManager.findFirstVisibleItemPosition() != slideAdapter.getItemCount() - 1) {
                            // 다음 아이템으로 이동
                            recyclerView.smoothScrollToPosition(layoutManager.findFirstVisibleItemPosition() + 1);
                        } else {
                            // 마지막 아이템이라면 첫 아이템으로 이동
                            recyclerView.smoothScrollToPosition(0);
                        }
                        // 7초 뒤에 이동
                        handler.postDelayed(this, 7000);
                    }
                };
            }
            handler.postDelayed(runnable, 7000);
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
