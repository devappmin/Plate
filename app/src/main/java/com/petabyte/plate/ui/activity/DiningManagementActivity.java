package com.petabyte.plate.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

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
import com.petabyte.plate.adapter.DiningManagementAdapter;
import com.petabyte.plate.data.DiningManagementCardData;
import com.petabyte.plate.data.DiningMasterData;
import com.petabyte.plate.data.UserData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class DiningManagementActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LottieAnimationView loadingSkeletonView;

    private RecyclerView recyclerView;
    private DiningManagementAdapter recyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private DatabaseReference databaseReference, ref_g, ref_h;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String uid;
    private HashMap<String, UserData> userDataMap =  new HashMap<>();
    private HashMap<String, DiningMasterData> diningMasterDataMap = new HashMap<>();
    private ArrayList<DiningManagementCardData> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dining_management);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh_diningManage);
        loadingSkeletonView = (LottieAnimationView)findViewById(R.id.loading_lottie_diningManage);
        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar_diningManage);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);

        toolbar = (Toolbar)findViewById(R.id.toolbar_diningManage);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

    }

    @Override
    protected void onResume() {
        super.onResume();

        initRecycler();
        initData();
    }

    private void initRecycler(){
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view_diningManage);
        recyclerAdapter = new DiningManagementAdapter(this.getSupportFragmentManager(), this);
        recyclerAdapter.removeAllItem();
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,true);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    //get dining Data
    public void initData() {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        uid = user.getUid();
        getUserData();
        getDiningMasterData();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList = new ArrayList<>();
                if(userDataMap.get(uid).getMyDining() != null) {
                    for (String uid : userDataMap.get(uid).getMyDining().values()) {
                        String diningTitle = diningMasterDataMap.get(uid).getTitle();
                        String diningDate = diningMasterDataMap.get(uid).getDate();
                        String diningLocation = diningMasterDataMap.get(uid).getLocation().get("location");
                        int currentReservationCount = diningMasterDataMap.get(uid).getCount().get("current");
                        DiningManagementCardData data = new DiningManagementCardData(diningTitle, diningDate, diningLocation, uid, currentReservationCount);
                        dataList.add(data);
                    }
                }
                Collections.sort(dataList);
                for (DiningManagementCardData diningManagementCardData : dataList) {
                    String dayOfWeek = "";
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = new Date();
                    try {
                        date = dateFormat.parse(diningManagementCardData.getDiningDate());
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
                    diningManagementCardData.setDiningDate(diningManagementCardData.getDiningDate() + dayOfWeek);
                    recyclerAdapter.addItem(diningManagementCardData);
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
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Dining");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot datasnapshot: snapshot.getChildren()){
                    diningMasterDataMap.put(datasnapshot.getKey(), datasnapshot.getValue(DiningMasterData.class));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public void addData(DiningManagementCardData data) {
        recyclerAdapter.addItem(data);
        recyclerAdapter.notifyDataSetChanged();
    }

    public void removeAllData() {
        recyclerAdapter.removeAllItem();
        recyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}