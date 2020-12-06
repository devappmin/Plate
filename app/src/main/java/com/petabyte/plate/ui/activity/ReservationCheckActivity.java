package com.petabyte.plate.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.petabyte.plate.R;
import com.petabyte.plate.adapter.BookmarkVerticalListAdapter;
import com.petabyte.plate.adapter.HomeHorizontalListAdapter;
import com.petabyte.plate.adapter.ReservationListAdapter;
import com.petabyte.plate.data.DiningMasterData;
import com.petabyte.plate.data.ReservationCardData;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReservationCheckActivity extends AppCompatActivity implements ValueEventListener {

    private Toolbar toolbar;
    private String UID;
    private String MEMBER_TYPE;
    private Intent intent;

    private RecyclerView recyclerView;
    private ReservationListAdapter recyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference mDatabase;
    HashMap<String, DiningMasterData> diningMasterDataMap = new HashMap<String, DiningMasterData>();

    private String diningUID;
    private String reservationTime;
    private String reservationStatus;
    private String diningTitle;
    private String diningLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_check);

        // mypage에서 회원정보 인텐트로 수신
        intent = getIntent();
        UID = intent.getStringExtra("UID");
        MEMBER_TYPE = intent.getStringExtra("MEMBER_TYPE");
        Log.d("ji1dev", UID+" / "+MEMBER_TYPE);

        // appbar 구현
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar_reservation_check);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);

        // toolbar back button 구현
        toolbar = (Toolbar)findViewById(R.id.toolbar_reservation_check);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // recycler view에 adapter과 layout manager 등록
        initRecycler();

        // data를 가져와서 세팅
        initData();

    }

    // recycler view 초기 설정
    private void initRecycler(){
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view_reservation);
        recyclerAdapter = new ReservationListAdapter(this); // adapter 생성
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,true);
        recyclerView.setAdapter(recyclerAdapter); // layout manager 세팅
        recyclerView.setLayoutManager(layoutManager); // adapter 세팅
    }
    // firebase db 에서 data를 가져와서 item을 세팅
    private void initData() {
        String currentTime = String.valueOf(System.currentTimeMillis());
        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("User").child(MEMBER_TYPE).child(UID).child("Reservation");

        // 마스터 데이터 세팅
        getDiningMasterData();
        // make dummy data on DB
        //ReservationCardData data = new ReservationCardData("입금대기중", "맥도날드", currentTime, "숭실대학교 정보과학관 201호");
        //mDatabase.child(currentTime).setValue(data);
        mDatabase.orderByKey().addListenerForSingleValueEvent(this);
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

    // 마스터 데이터 가져오는 함수
    private void getDiningMasterData(){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Dining");
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot datasnapshot: snapshot.getChildren()){

                    // key, value 쌍을 diningUID, DiningMasterData 클래스로 지정하여 hashmap에 저장함
                    diningMasterDataMap.put(datasnapshot.getKey(), datasnapshot.getValue(DiningMasterData.class));
                    Log.d("ji1dev", datasnapshot.getKey());
                    Log.d("ji1dev", diningMasterDataMap.get(datasnapshot.getKey()).getTitle());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        for(DataSnapshot datasnapshot: snapshot.getChildren()){
            diningUID = datasnapshot.child("DiningUID").getValue().toString();
            reservationTime = convertTimestamp((long) datasnapshot.child("Reservation").getValue());
            reservationStatus = datasnapshot.child("Status").getValue().toString();
            diningTitle = diningMasterDataMap.get(diningUID).getTitle();
            diningLocation = (String) diningMasterDataMap.get(diningUID).getLocation().get("detail");

            Log.d("ji1dev", diningUID+" / "+diningTitle+" / "+reservationTime+" / "+reservationStatus+" / "+diningLocation);

            // recycler adapter에 item 추가
            ReservationCardData data = new ReservationCardData(reservationStatus, diningTitle, reservationTime, diningLocation);
            recyclerAdapter.addItem(data);
            recyclerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {}

    public String convertTimestamp(long time) {
        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        String date = (String) formatter.format(new Timestamp(time));
        return date;
    }
}