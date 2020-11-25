package com.petabyte.plate.ui.view;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.text.format.DateFormat;
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

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Calendar;


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
        mDatabase.child("Dining").orderByChild("schedules/RANDOMKEY/start").addListenerForSingleValueEvent(this);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshots) {
        for (DataSnapshot snapshot : snapshots.getChildren()) {
            String diningTitle = snapshot.child("title").getValue().toString();
            String diningSubtitle = snapshot.child("subtitle").getValue().toString();
            String diningDate;
            long diningTimestamp = (long) snapshot.child("schedules").child("RANDOMKEY").child("start").getValue();
            double diningLatitude = (double) snapshot.child("location").child("x").getValue();
            double diningLongitude = (double) snapshot.child("location").child("y").getValue();
            String diningDetailLocation = snapshot.child("location").child("detail").getValue().toString();
            String imageUri = snapshot.child("images").child("2").getValue().toString();

            String diningLocation = getAddress(diningLatitude, diningLongitude).replace("대한민국 ", "");//좌표로 주소 얻기, String에서 대한민국 제거
            diningDate = getDate(diningTimestamp);//timstamp로 날짜 얻기

            BookmarkCardViewData data = new BookmarkCardViewData(diningTitle, diningSubtitle, diningDate, diningLocation, diningDetailLocation, imageUri);

            recyclerAdapter.addItem(data);
            recyclerAdapter.notifyDataSetChanged();

            Log.d("[*]", snapshot + "");
            Log.d("[!]", diningTitle + ", " + diningDate + "and" + diningLocation);
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }

    public String getAddress( double latitude, double longitude) {

        //좌표를 주소로 변환
        Geocoder geocoder = new Geocoder(this.getContext(), Locale.KOREA);

        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 7);
        } catch (IOException ioException) {
            //네트워크 문제
            return "네트워크 오류";
        } catch (IllegalArgumentException illegalArgumentException) {
            return "잘못된 주소";
        }

        if (addresses == null || addresses.size() == 0) {
            return "주소 미발견";
        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString();

    }

    private String getDate(long timeStamp) {
        Calendar cal = Calendar.getInstance(Locale.KOREA);
        cal.setTimeInMillis(timeStamp * 1000);
        String date = DateFormat.format("yyyy-MM-dd", cal).toString();
        return date;
    }

}