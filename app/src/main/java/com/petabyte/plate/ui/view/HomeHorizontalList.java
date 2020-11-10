package com.petabyte.plate.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.petabyte.plate.R;
import com.petabyte.plate.adapter.HomeHorizontalListAdapter;
import com.petabyte.plate.data.HomeCardData;

/**
 * Create custom horizontal recycler view
 * inside of the Nested Scroll View in Home Fragment
 */

public class HomeHorizontalList extends ConstraintLayout implements ValueEventListener{
    private RecyclerView recyclerView;
    private HomeHorizontalListAdapter recyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private DatabaseReference mDatabase;

    public HomeHorizontalList(@NonNull Context context) {
        super(context);
        init();
    }

    public HomeHorizontalList(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HomeHorizontalList(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public HomeHorizontalList(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_homehorizontallist, this);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        recyclerInitial();
        getItemDatas();
    }

    private void recyclerInitial() {
        recyclerView = (RecyclerView)this.findViewById(R.id.recycler_view_v_homehorizontal);

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerAdapter = new HomeHorizontalListAdapter();
        recyclerView.setAdapter(recyclerAdapter);
    }

    private void getItemDatas() {
        mDatabase.child("Dining").limitToLast(5).addListenerForSingleValueEvent(this);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshots) {
        for (DataSnapshot snapshot : snapshots.getChildren()) {
            String title = snapshot.child("Title").getValue(String.class);
            String description = snapshot.child("Description").getValue(String.class);

            // Image should be changed
            String imageUri = snapshot.child("Image").getValue(String.class);

            HomeCardData data = new HomeCardData(title, description, imageUri);
            recyclerAdapter.addItem(data);

            recyclerAdapter.notifyDataSetChanged();

            Log.d("[*]", snapshot + "");
            Log.d("[!]", title + " and " + description);
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
}
