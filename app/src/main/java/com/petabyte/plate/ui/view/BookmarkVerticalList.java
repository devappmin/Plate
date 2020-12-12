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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.petabyte.plate.R;
import com.petabyte.plate.adapter.BookmarkVerticalListAdapter;
import com.petabyte.plate.data.BookmarkCardViewData;
import com.petabyte.plate.data.DiningMasterData;
import com.petabyte.plate.data.HomeCardData;
import com.petabyte.plate.data.UserData;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;


public class BookmarkVerticalList extends ConstraintLayout {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private BookmarkVerticalListAdapter recyclerAdapter;
    private DatabaseReference databaseReference, ref_g, ref_h;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String uid;
    private HashMap<String, UserData> userDataMap =  new HashMap<>();
    private HashMap<String, DiningMasterData> diningMasterDataMap = new HashMap<>();
    private ArrayList<BookmarkCardViewData> dataList = new ArrayList<>();

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

    public void init() {
        inflate(getContext(), R.layout.view_bookmarkverticallist, this);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        recyclerInitial();
        getDiningData();
    }

    private void recyclerInitial() {
        recyclerView = (RecyclerView)this.findViewById(R.id.recycler_view_v_bookmarkvertical);

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerAdapter = new BookmarkVerticalListAdapter();
        recyclerView.setAdapter(recyclerAdapter);
    }

    private void getUserData(){
        ref_g = FirebaseDatabase.getInstance().getReference("User").child("Guest");
        ref_h = FirebaseDatabase.getInstance().getReference("User").child("Host");

        ref_g.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    userDataMap.put(dataSnapshot.getKey(), dataSnapshot.getValue(UserData.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ref_h.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    userDataMap.put(dataSnapshot.getKey(), dataSnapshot.getValue(UserData.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getDiningMasterData(){
        diningMasterDataMap.clear();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Dining");
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot datasnapshot: snapshot.getChildren()){
                    // key, value 쌍을 diningUID, DiningMasterData 클래스로 지정하여 hashmap에 저장함
                    diningMasterDataMap.put(datasnapshot.getKey(), datasnapshot.getValue(DiningMasterData.class));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    //get dining Data
    public void getDiningData() {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        uid = user.getUid();
        getUserData();
        getDiningMasterData();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList = new ArrayList<>();
                if(userDataMap.get(uid).getBookmark() != null) {
                    for (String uid : userDataMap.get(uid).getBookmark().values()) {
                        String diningTitle = diningMasterDataMap.get(uid).getTitle();
                        String diningSubTitle = diningMasterDataMap.get(uid).getSubtitle();
                        String diningDate = diningMasterDataMap.get(uid).getDate();
                        String diningLocation = diningMasterDataMap.get(uid).getLocation().get("location");
                        String diningDetailLocation = diningMasterDataMap.get(uid).getLocation().get("detail");
                        String imageName = diningMasterDataMap.get(uid).getImages().get(1);
                        BookmarkCardViewData data = new BookmarkCardViewData(diningTitle, diningSubTitle, diningDate, diningLocation, diningDetailLocation, imageName, uid);
                        dataList.add(data);
                    }
                }
                Collections.sort(dataList);
                for (BookmarkCardViewData bookmarkCardViewData : dataList) {
                    String dayOfWeek = "";
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = new Date();
                    try {
                        date = dateFormat.parse(bookmarkCardViewData.getDiningDate());
                    } catch (ParseException e) {
                    }
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    switch (calendar.get(Calendar.DAY_OF_WEEK)) {
                        case 1:
                            dayOfWeek = " 일요일";
                            break;
                        case 2:
                            dayOfWeek = " 월요일";
                            break;
                        case 3:
                            dayOfWeek = " 화요일";
                            break;
                        case 4:
                            dayOfWeek = " 수요일";
                            break;
                        case 5:
                            dayOfWeek = " 목요일";
                            break;
                        case 6:
                            dayOfWeek = " 금요일";
                            break;
                        case 7:
                            dayOfWeek = " 토요일";
                            break;
                    }
                    bookmarkCardViewData.setDiningDate(bookmarkCardViewData.getDiningDate() + dayOfWeek);
                    recyclerAdapter.addItem(bookmarkCardViewData);
                    recyclerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addData(BookmarkCardViewData data) {
        recyclerAdapter.addItem(data);
        recyclerAdapter.notifyDataSetChanged();
    }

    public void removeAllData() {
        recyclerAdapter.removeAllItem();
        recyclerAdapter.notifyDataSetChanged();
    }
}