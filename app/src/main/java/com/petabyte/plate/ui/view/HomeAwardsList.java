package com.petabyte.plate.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.petabyte.plate.R;
import com.petabyte.plate.adapter.HomeAwardsListAdapter;
import com.petabyte.plate.data.HomeAwardsData;

import java.util.ArrayList;
import java.util.List;

public class HomeAwardsList extends ConstraintLayout {

    private RecyclerView recyclerView;
    private HomeAwardsListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private TextView titleText;

    private StorageReference storage;

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

        storage = FirebaseStorage.getInstance("gs://plate-f5144.appspot.com/").getReference();

        titleText = (TextView)this.findViewById(R.id.title_tv_v_homeawards);
        recyclerView = (RecyclerView)this.findViewById(R.id.recycler_view_v_awards);

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new HomeAwardsListAdapter();
        adapter.setReference(storage);
        recyclerView.setAdapter(adapter);
    }

    public void setTitle(String title) {
        titleText.setText(title);
    }

    public void addData(HomeAwardsData data) {
        adapter.addItem(data);
        adapter.notifyDataSetChanged();
    }
}
