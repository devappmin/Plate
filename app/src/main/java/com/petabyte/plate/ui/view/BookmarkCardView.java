package com.petabyte.plate.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.petabyte.plate.R;


public class BookmarkCardView extends ConstraintLayout implements ValueEventListener{

    private DatabaseReference mDatabase;

    public BookmarkCardView(@NonNull Context context) {
        super(context);
        init();
    }

    public BookmarkCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BookmarkCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public BookmarkCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_bookmarkcard, this);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        getItemDatas();
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


        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
}