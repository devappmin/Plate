package com.petabyte.plate.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.petabyte.plate.R;
import com.petabyte.plate.data.FoodStyle;
import com.petabyte.plate.data.HomeAwardsData;
import com.petabyte.plate.data.HomeCardData;
import com.petabyte.plate.data.ImageSlideData;
import com.petabyte.plate.ui.activity.SearchActivity;
import com.petabyte.plate.ui.view.HomeAwardsList;
import com.petabyte.plate.ui.view.HomeHorizontalList;
import com.petabyte.plate.utils.ConnectionCodes;
import com.petabyte.plate.utils.LogTags;
import com.squareup.picasso.Picasso;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private CardView searchButton;
    private ImageView applyImage;
    private HomeHorizontalList recentList;
    private HomeHorizontalList foodTypeList;
    private HomeHorizontalList[] chipTypeList;
    private HomeAwardsList postList;
    private HomeAwardsList imageSlider;

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

        // chipTypeList 배열 초기화
        chipTypeList = new HomeHorizontalList[2];

        // 뷰를 불러온다.
        searchButton = (CardView) v.findViewById(R.id.search_card_fm_home);
        applyImage = (ImageView)v.findViewById(R.id.apply_iv_fm_home);
        recentList = (HomeHorizontalList)v.findViewById(R.id.recent_hh_fm_home);
        postList = (HomeAwardsList)v.findViewById(R.id.awards_ha_fm_home);
        imageSlider = (HomeAwardsList)v.findViewById(R.id.image_slide_fm_home);
        swipeRefreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.swipe_layout_fm_home);
        mainLayout = (LinearLayout) v.findViewById(R.id.main_layout_fm_home);
        scrollView = (NestedScrollView)v.findViewById(R.id.scroll_v_fm_home);
        foodTypeList = (HomeHorizontalList)v.findViewById(R.id.food_type_hh_fm_home);
        chipTypeList[0] = (HomeHorizontalList)v.findViewById(R.id.chip_type_hh_fm_home);
        chipTypeList[1] = (HomeHorizontalList)v.findViewById(R.id.chip_type_2_hh_fm_home);

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

        postList.setTitle("PLATE 포스트");
        postList.setType(HomeAwardsList.TYPE_MODE.POST_MODE);

        imageSlider.hideTitle();
        imageSlider.setType(HomeAwardsList.TYPE_MODE.IMAGE_SLIDE_MODE);
        imageSlider.removePadding();

        loadRecentList();
        loadPlatePost();
        loadImageSlider();
        loadChipList(FoodStyle.SWEET, chipTypeList[0]);
        loadChipList(FoodStyle.SALTY, chipTypeList[1]);
        loadFoodTypeList("한식 다이닝", foodTypeList);

        //region 지원하기 카드뷰 관련 코드
        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/plate-f5144.appspot.com/o/chef.png?alt=media&token=2e0e6f43-2523-482e-82ff-f5cd5ad54e19")
                .fit().centerCrop().into(applyImage);
        //endregion
        return v;
    }

    private void loadRecentList() {
        recentList.setTitle("최근에 올라온 음식이에요.");
        mDatabase.child("Dining").limitToLast(5).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshots) {
                for (DataSnapshot snapshot : snapshots.getChildren()) {
                    HomeCardData data = snapshot.getValue(HomeCardData.class);
                    data.setImageUri(snapshot.getKey() + "/" + snapshot.child("dishes/1").getValue(String.class) + ".jpg");
                    recentList.addData(data);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadPlatePost() {
        mDatabase.child("Home").child("Plate Post").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshots) {
                for (DataSnapshot snapshot : snapshots.getChildren()) {
                    HomeAwardsData data = snapshot.getValue(HomeAwardsData.class);
                    postList.addData(data);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadImageSlider() {
        mDatabase.child("Home").child("Slider").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshots) {
                for (DataSnapshot snapshot : snapshots.getChildren()) {
                    ImageSlideData data = new ImageSlideData();
                    data.setImage(snapshot.getValue(String.class));
                    imageSlider.addData(data);
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadChipList(final FoodStyle foodStyle, final HomeHorizontalList list) {
        list.setTitle("#" + foodStyle.label + "은 어떤가요?");
        mDatabase.child("Dining").limitToLast(5).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshots) {
                for (DataSnapshot snapshot : snapshots.getChildren()) {
                    if (snapshot.child("style").hasChild(foodStyle.toString())) {
                        HomeCardData data = snapshot.getValue(HomeCardData.class);
                        data.setImageUri(snapshot.getKey() + "/" + snapshot.child("dishes/1").getValue(String.class) + ".jpg");
                        list.addData(data);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadFoodTypeList(final String type, final HomeHorizontalList list) {
        list.setTitle(type + " 모음");
        mDatabase.child("Dining").limitToLast(5).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshots) {
                for (DataSnapshot snapshot : snapshots.getChildren()) {
                    if (snapshot.child("subtitle").getValue(String.class).equals(type)) {
                        HomeCardData data = snapshot.getValue(HomeCardData.class);
                        data.setImageUri(snapshot.getKey() + "/" + snapshot.child("dishes/1").getValue(String.class) + ".jpg");
                        list.addData(data);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onRefresh() {
        recentList.removeAllData();
        postList.removeAllData();
        imageSlider.removeAllData();
        chipTypeList[0].removeAllData();
        chipTypeList[1].removeAllData();
        foodTypeList.removeAllData();
        loadRecentList();
        loadPlatePost();
        loadImageSlider();
        loadChipList(FoodStyle.SWEET, chipTypeList[0]);
        loadChipList(FoodStyle.SALTY, chipTypeList[1]);
        loadFoodTypeList("한식 다이닝", foodTypeList);
    }
}
