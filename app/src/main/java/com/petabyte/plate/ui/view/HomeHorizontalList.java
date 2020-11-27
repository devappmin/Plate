package com.petabyte.plate.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.petabyte.plate.R;
import com.petabyte.plate.adapter.HomeHorizontalListAdapter;
import com.petabyte.plate.data.HomeCardData;

/**
 * Create custom horizontal recycler view
 * inside of the Nested Scroll View in Home Fragment
 */

public class HomeHorizontalList extends ConstraintLayout {

    // RecyclerView 관련 변수
    private RecyclerView recyclerView;
    private HomeHorizontalListAdapter recyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;

    // View 변수
    private TextView titleView;

    // Firebase 관련 변수
    private StorageReference mStorage;

    public HomeHorizontalList(@NonNull Context context) {
        super(context);
        initViews();
    }

    public HomeHorizontalList(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public HomeHorizontalList(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    public HomeHorizontalList(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initViews();
    }

    private void initViews() {
        inflate(getContext(), R.layout.view_homehorizontallist, this);

        mStorage = FirebaseStorage.getInstance("gs://plate-f5144.appspot.com/").getReference();

        recyclerView = (RecyclerView)this.findViewById(R.id.recycler_view_v_homehorizontal);
        titleView = (TextView)this.findViewById(R.id.title_tv_v_homehorizontal);

        // 부드러운 스크롤이 아닌 item 단위로 스크롤을 하기 위해 적용
        SnapHelper helper = new PagerSnapHelper();
        helper.attachToRecyclerView(recyclerView);

        // RecyclerView에 LayoutManager 연결
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // RecyclerView에 Adapter연결
        recyclerAdapter = new HomeHorizontalListAdapter();
        recyclerAdapter.setReference(mStorage);
        recyclerView.setAdapter(recyclerAdapter);
    }

    public void setTitle(String title) {
        titleView.setText(title);
    }

    public void addData(HomeCardData data) {
        recyclerAdapter.addItem(data);
        recyclerAdapter.notifyDataSetChanged();
    }
}
