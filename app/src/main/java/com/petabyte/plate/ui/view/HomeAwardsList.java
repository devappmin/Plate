package com.petabyte.plate.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.petabyte.plate.R;
import com.petabyte.plate.adapter.HomeAwardsListAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeAwardsList extends ConstraintLayout {

    private RecyclerView recyclerView;
    private HomeAwardsListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private TextView titleText;

    private List<String> imageUris;

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

        imageUris = new ArrayList<>();

        titleText = (TextView)this.findViewById(R.id.title_tv_v_homeawards);
        recyclerView = (RecyclerView)this.findViewById(R.id.recycler_view_v_awards);

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new HomeAwardsListAdapter();
        recyclerView.setAdapter(adapter);
    }

    public void setTitle(String title) {
        titleText.setText(title);
    }

    public void addImages(String uri) {
        imageUris.add(uri);
        adapter.addItem(uri);
        adapter.notifyDataSetChanged();
    }
}
