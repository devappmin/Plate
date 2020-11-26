package com.petabyte.plate.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.petabyte.plate.R;
import com.petabyte.plate.data.HomeAwardsData;
import com.petabyte.plate.ui.activity.SearchActivity;
import com.petabyte.plate.ui.view.HomeAwardsList;
import com.petabyte.plate.ui.view.HomeHorizontalList;
import com.petabyte.plate.utils.ConnectionCodes;
import com.petabyte.plate.utils.LogTags;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private CardView searchButton;
    private ImageView applyImage;
    private HomeHorizontalList specialList;
    private HomeAwardsList awardsList;

    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout mainLayout;
    private NestedScrollView scrollView;

    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        // Firebase 초기화
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // 뷰를 불러온다.
        searchButton = (CardView) v.findViewById(R.id.search_card_fm_home);
        applyImage = (ImageView)v.findViewById(R.id.apply_iv_fm_home);
        specialList = (HomeHorizontalList)v.findViewById(R.id.special_hh_fm_home);
        awardsList = (HomeAwardsList)v.findViewById(R.id.awards_ha_fm_home);
        swipeRefreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.swipe_layout_fm_home);
        mainLayout = (LinearLayout) v.findViewById(R.id.main_layout_fm_home);
        scrollView = (NestedScrollView)v.findViewById(R.id.scroll_v_fm_home);

        // 스와이프 시 Listener 설정
        swipeRefreshLayout.setOnRefreshListener(this);

        // 검색으로 이동하기 위한 검색창 클릭 리스너
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                getActivity().startActivityForResult(intent, ConnectionCodes.REQUEST_SEARCH_ACTIVITY);
            }
        });

        //region PLATE 포스트 관련 코드
        awardsList.setTitle("PLATE 포스트");
        mDatabase.child("Home").child("Plate Post").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshots) {
                for (DataSnapshot snapshot : snapshots.getChildren()) {
                    HomeAwardsData data = snapshot.getValue(HomeAwardsData.class);
                    awardsList.addData(data);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //endregion


        //region 지원하기 카드뷰 관련 코드
        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/plate-f5144.appspot.com/o/chef.png?alt=media&token=2e0e6f43-2523-482e-82ff-f5cd5ad54e19")
                .fit().centerCrop().into(applyImage);
        //endregion
        return v;
    }

    @Override
    public void onRefresh() {
        scrollView.removeView(mainLayout);
        mainLayout.invalidate();
        scrollView.addView(mainLayout);
        swipeRefreshLayout.setRefreshing(false);
    }
}
