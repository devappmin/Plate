package com.petabyte.plate.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
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

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
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
import com.petabyte.plate.ui.view.HomeTodayFood;
import com.petabyte.plate.utils.ConnectionCodes;
import com.petabyte.plate.utils.KoreanUtil;
import com.petabyte.plate.utils.LogTags;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final int LIST_COUNT = 7;
    public static int current;
    private boolean[] completeLoaded;

    private CardView searchButton;
    private CardView applyCardView;
    private ImageView applyImage;
    private HomeHorizontalList recentList;
    private HomeHorizontalList foodTypeList;
    private HomeHorizontalList[] chipTypeList;
    private HomeAwardsList postList;
    private HomeAwardsList imageSlider;
    private LottieAnimationView loadingSkeletonView;
    private HomeTodayFood todayFood;

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

        // 모든 값이 불러왔는지 확인하는 배열 초기화
        completeLoaded = new boolean[LIST_COUNT];
        for (int i = 0; i < LIST_COUNT; i++)
            completeLoaded[i] = false;

        // 뷰가 생성되면 불러온 리스트의 수를 0으로 초기화
        current = 0;

        // 뷰를 불러온다.
        searchButton = (CardView)v.findViewById(R.id.search_card_fm_home);
        applyCardView = (CardView)v.findViewById(R.id.apply_cv_fm_home);
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
        loadingSkeletonView = (LottieAnimationView)v.findViewById(R.id.loading_lottie_fm_home);
        todayFood = (HomeTodayFood)v.findViewById(R.id.today_food_fm_home);

        // 처음에 SwipeRefreshLayout을 숨기고 로딩을 보여준다.
        scrollView.setVisibility(View.GONE);
        loadingSkeletonView.setVisibility(View.VISIBLE);
        searchButton.setVisibility(View.GONE);

        // 스와이프 시 Listener 설정
        swipeRefreshLayout.setOnRefreshListener(this);

        // 검색으로 이동하기 위한 검색창 클릭 리스너
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                Objects.requireNonNull(getActivity()).startActivityForResult(intent, ConnectionCodes.REQUEST_SEARCH_ACTIVITY);
            }
        });

        // PLATE 포스트 관련 설정
        postList.setTitle("PLATE 포스트");
        postList.setBackgroundColor(Color.BLACK, Color.WHITE);
        postList.setMarginTop(80);
        postList.setMarginBottom(80);
        postList.setType(HomeAwardsList.TYPE_MODE.POST_MODE);

        // 이미지 슬라이더 관련 설정
        imageSlider.hideTitle();
        imageSlider.setType(HomeAwardsList.TYPE_MODE.IMAGE_SLIDE_MODE);
        imageSlider.removePadding();

        // Firebase를 통한 데이터를 불러오는 함수
        loadRecentList(recentList);
        loadPlatePost(postList);
        loadImageSlider(imageSlider);
        loadChipList(FoodStyle.randomLetter(), chipTypeList[0]);
        loadChipList(FoodStyle.randomLetter(), chipTypeList[1]);
        loadFoodTypeList("한식 다이닝", foodTypeList);
        loadTodayFood(todayFood);

        // 유저가 게스트인지 호스트인지 알아온다.
        // 값은 userType String에 저장됨.
        getUserType();

        //region 지원하기 카드뷰 관련 코드
        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/plate-f5144.appspot.com/o/chef.png?alt=media&token=2e0e6f43-2523-482e-82ff-f5cd5ad54e19")
                .fit().centerCrop().into(applyImage);
        //endregion
        return v;
    }

    private void loadRecentList(final HomeHorizontalList mList) {
        mList.setTitle("최근에 올라온 음식이에요.");
        mDatabase.child("Dining").limitToLast(5).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshots) {
                for (DataSnapshot snapshot : snapshots.getChildren()) {
                    HomeCardData data = snapshot.getValue(HomeCardData.class);
                    data.setImageUri(snapshot.getKey() + "/" + snapshot.child("dishes/1").getValue(String.class) + ".jpg");
                    mList.addData(data);
                }

                completeLoaded[current++] = true;
                checkAllLoaded();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadPlatePost(final HomeAwardsList mList) {
        mDatabase.child("Home").child("Plate Post").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshots) {
                for (DataSnapshot snapshot : snapshots.getChildren()) {
                    HomeAwardsData data = snapshot.getValue(HomeAwardsData.class);
                    mList.addData(data);
                }

                completeLoaded[current++] = true;
                checkAllLoaded();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadImageSlider(final HomeAwardsList mList) {
        mDatabase.child("Home").child("Slider").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshots) {
                for (DataSnapshot snapshot : snapshots.getChildren()) {
                    ImageSlideData data = new ImageSlideData();
                    data.setImage(snapshot.getValue(String.class));
                    mList.addData(data);
                }

                completeLoaded[current++] = true;
                checkAllLoaded();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadChipList(final FoodStyle foodStyle, final HomeHorizontalList mList) {
        String title = "<font color=#4150b4>#" + KoreanUtil.includeGrammarCheck(foodStyle.label, "</font>은 ", "</font>는 ")
                + "어떤가요?";
        mList.setTitle(Html.fromHtml(title));
        mDatabase.child("Dining").limitToLast(5).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshots) {
                for (DataSnapshot snapshot : snapshots.getChildren()) {
                    if (snapshot.child("style").hasChild(foodStyle.toString())) {
                        HomeCardData data = snapshot.getValue(HomeCardData.class);
                        data.setImageUri(snapshot.getKey() + "/" + snapshot.child("dishes/1").getValue(String.class) + ".jpg");
                        mList.addData(data);
                    }
                }

                completeLoaded[current++] = true;
                checkAllLoaded();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadFoodTypeList(final String type, final HomeHorizontalList mList) {
        mList.setTitle(type + " 모음");
        mDatabase.child("Dining").limitToLast(5).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshots) {
                for (DataSnapshot snapshot : snapshots.getChildren()) {
                    if (snapshot.child("subtitle").getValue(String.class).equals(type)) {
                        HomeCardData data = snapshot.getValue(HomeCardData.class);
                        data.setImageUri(snapshot.getKey() + "/" + snapshot.child("dishes/1").getValue(String.class) + ".jpg");
                        mList.addData(data);
                    }
                }

                completeLoaded[current++] = true;
                checkAllLoaded();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadTodayFood(final HomeTodayFood view) {

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String path = snapshot.child("Home").child("Todays Food").getValue(String.class);
                String title = snapshot.child("Dining").child(path).child("title").getValue(String.class);
                String image = path + "/" + snapshot.child("Dining").child(path).child("dishes/1").getValue(String.class) + ".jpg";
                view.launch(title, image);

                completeLoaded[current++] = true;
                checkAllLoaded();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkAllLoaded() {
        if (completeLoaded[LIST_COUNT - 1]) {
            // 로딩화면을 감춘 뒤 루프 종료
            loadingSkeletonView.setVisibility(View.GONE);
            loadingSkeletonView.setSpeed(0);

            // 데이터 화면 보여줌.
            scrollView.setVisibility(View.VISIBLE);

            // 검색창을 보여줌.
            searchButton.setVisibility(View.VISIBLE);

            // 만약에 Swipe를 통해서 Refreshing을 하고 있던거였으면 false로 바꿈.
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    private void getUserType() {
        final String userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDatabase.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userType = snapshot.child("Guest").hasChild(userUID) ? "Guest" : "Host";

                if (userType.equals("Host"))
                    applyCardView.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onRefresh() {
        // 로딩화면 표시
        loadingSkeletonView.setVisibility(View.VISIBLE);
        loadingSkeletonView.setSpeed(1f);
        scrollView.setVisibility(View.GONE);
        searchButton.setVisibility(View.GONE);

        // 로딩확인 초기화
        for(int i = 0; i < LIST_COUNT; i++)
            completeLoaded[i] = false;
        current = 0;

        // 모든 리스트 데이터 삭제
        recentList.removeAllData();
        postList.removeAllData();
        imageSlider.removeAllData();
        chipTypeList[0].removeAllData();
        chipTypeList[1].removeAllData();
        foodTypeList.removeAllData();

        // 리스트에 값 다시 입력
        loadRecentList(recentList);
        loadPlatePost(postList);
        loadImageSlider(imageSlider);
        loadChipList(FoodStyle.randomLetter(), chipTypeList[0]);
        loadChipList(FoodStyle.randomLetter(), chipTypeList[1]);
        loadFoodTypeList("한식 다이닝", foodTypeList);
        loadTodayFood(todayFood);
    }
}
