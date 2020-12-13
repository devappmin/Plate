package com.petabyte.plate.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.petabyte.plate.R;
import com.petabyte.plate.adapter.BookmarkVerticalListAdapter;
import com.petabyte.plate.data.BookmarkCardViewData;
import com.petabyte.plate.data.DiningMasterData;
import com.petabyte.plate.data.UserData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;


public class BookmarkFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private BookmarkVerticalListAdapter recyclerAdapter;

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LottieAnimationView loadingSkeletonView;

    private DatabaseReference databaseReference, ref_g, ref_h;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String uid;

    private HashMap<String, UserData> userDataMap =  new HashMap<>();
    private HashMap<String, DiningMasterData> diningMasterDataMap = new HashMap<>();
    private ArrayList<BookmarkCardViewData> dataList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bookmark, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        collapsingToolbarLayout = (CollapsingToolbarLayout)v.findViewById(R.id.collapsing_toolbar_BookmarkFragment);
        swipeRefreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.refresh_BookmarkFragment);
        loadingSkeletonView = (LottieAnimationView)v.findViewById(R.id.loading_lottie_Bookmarkfragment);
        recyclerView = (RecyclerView)v.findViewById(R.id.recycler_BookmarkFragment);

        loadingSkeletonView.setVisibility(View.GONE);

        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);

        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimaryDark),
                getResources().getColor(R.color.colorAccent)
        );
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadingSkeletonView.setVisibility(View.VISIBLE);
                loadingSkeletonView.setSpeed(1f);
                removeAllData();
                initData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        recyclerInitial();
        initData();

        return v;
    }

    private void recyclerInitial() {
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerAdapter = new BookmarkVerticalListAdapter();
        recyclerView.setAdapter(recyclerAdapter);
    }

    public void initData() {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        uid = user.getUid();
        getUserData();
        getDiningMasterData();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList = new ArrayList<>();
                if(userDataMap.get(uid).getBookmark() != null && diningMasterDataMap.size() != 0) {
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
                loadingSkeletonView.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

    public void addData(BookmarkCardViewData data) {
        recyclerAdapter.addItem(data);
        recyclerAdapter.notifyDataSetChanged();
    }

    public void removeAllData() {
        recyclerAdapter.removeAllItem();
        recyclerAdapter.notifyDataSetChanged();
    }
}
