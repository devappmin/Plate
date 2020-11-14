package com.petabyte.plate.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

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
import com.petabyte.plate.adapter.BookmarkVerticalListAdapter;
import com.petabyte.plate.data.BookmarkCardViewData;


public class BookmarkVerticalList extends ConstraintLayout implements ValueEventListener{
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private BookmarkVerticalListAdapter recyclerAdapter;
    private DatabaseReference mDatabase;

    public BookmarkVerticalList(@NonNull Context context) {
        super(context);
        init();
    }

    public BookmarkVerticalList(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BookmarkVerticalList(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public BookmarkVerticalList(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_bookmarkverticallist, this);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        recyclerInitial();
        getItemDatas();
    }

    private void recyclerInitial() {
        recyclerView = (RecyclerView)this.findViewById(R.id.recycler_view_v_bookmarkvertical);

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerAdapter = new BookmarkVerticalListAdapter();
        recyclerView.setAdapter(recyclerAdapter);
    }

    private void getItemDatas() {
        mDatabase.child("Dining").limitToLast(5).addListenerForSingleValueEvent(this);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshots) {
        for (DataSnapshot snapshot : snapshots.getChildren()) {
            String diningName = snapshot.child("Title").getValue().toString();
            String diningDate = snapshot.child("Schedules").child("RANDOMKEY").child("start").getValue().toString();
            String diningLocation = snapshot.child("Location").getValue().toString();
            String imageUri = snapshot.child("Image").getValue().toString();


            BookmarkCardViewData data = new BookmarkCardViewData(diningName, diningDate, diningLocation, imageUri);

            recyclerAdapter.addItem(data);
            recyclerAdapter.notifyDataSetChanged();

            Log.d("[*]", snapshot + "");
            Log.d("[!]", diningName + ", " + diningDate + "and" + diningLocation);
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
}