package com.petabyte.plate.ui.fragment;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.petabyte.plate.R;
import com.petabyte.plate.data.DiningStyle;
import com.petabyte.plate.data.FoodStyle;
import com.petabyte.plate.data.HomeAwardsData;
import com.petabyte.plate.data.HomeCardData;
import com.petabyte.plate.data.ImageSlideData;
import com.petabyte.plate.ui.activity.AddDiningPlanActivity;
import com.petabyte.plate.ui.activity.RegisterInfoActivity;
import com.petabyte.plate.ui.activity.SearchActivity;
import com.petabyte.plate.ui.view.HomeAwardsList;
import com.petabyte.plate.ui.view.HomeHorizontalList;
import com.petabyte.plate.ui.view.HomeTodayFood;
import com.petabyte.plate.utils.ConnectionCodes;
import com.petabyte.plate.utils.KoreanUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    // HomeHorizontalList에서 아이템을 불러올 때 최대 10개(0~9개)를 불러오게 한다.
    private static final int MAX_ITEM_COUNT = 9;

    // 모든 커스텀뷰가 로딩이 됐는지 확인하기 위한 변수
    public static final int LIST_COUNT = 7;
    public static int current;
    private boolean[] completeLoaded;

    private TextView addDiningPlanTextView;
    private ConstraintLayout addDiningPlanBackground;
    private CardView searchButton;
    private CardView applyCardView;
    private CardView addDiningPlanCardView;
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

    private String userUID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        // Firebase 초기화
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // chipTypeList 배열 초기화
        chipTypeList = new HomeHorizontalList[2];

        initCompleteLoadedArray();
        
        // 뷰를 불러온다.
        addDiningPlanBackground = (ConstraintLayout)v.findViewById(R.id.add_dining_layout_fm_home);
        addDiningPlanTextView = (TextView) v.findViewById(R.id.add_dining_tv_fm_home);
        searchButton = (CardView)v.findViewById(R.id.search_card_fm_home);
        applyCardView = (CardView)v.findViewById(R.id.apply_cv_fm_home);
        addDiningPlanCardView = (CardView)v.findViewById(R.id.add_dining_cv_fm_home);
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

                // 지금 돌리는 프로그램이 안드로이드 롤리팝(5.0)인지 확인
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), searchButton, "Search");
                    Objects.requireNonNull(getActivity()).startActivityForResult(intent, ConnectionCodes.REQUEST_SEARCH_ACTIVITY, options.toBundle());
                } else
                    // 롤리팝 이하면 애니메이션 없이 액티비티 호출
                    Objects.requireNonNull(getActivity()).startActivityForResult(intent, ConnectionCodes.REQUEST_SEARCH_ACTIVITY);
            }
        });

        // 다이닝 추가와 관련된 클릭 리스너
        addDiningPlanCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start AddDiningPlanActivity here..
                Intent intent = new Intent(getContext(), AddDiningPlanActivity.class);
                startActivity(intent);
            }
        });

        // 호스트 지원 관련 클릭 리스너
        applyCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("팝업다이닝 셰프를 지원할까요?").setMessage("셰프가 되어 언제 어디서나 다이닝을 선보이고 수익을 창출할 수 있습니다.");

                builder.setPositiveButton("지원하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        changeGuestToHost();
                    }
                });

                builder.setNegativeButton("아니요", null);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

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

        // ChipList를 불러올 때 겹치지 않고 불러오기 위해서 미리 배열 선언
        FoodStyle[] foodStyles = FoodStyle.randomFoodStyles(2);

        // Firebase를 통한 데이터를 불러오는 함수
        loadRecentList(recentList);
        loadPlatePost(postList);
        loadImageSlider(imageSlider);
        loadChipList(foodStyles[0], chipTypeList[0]);
        loadChipList(foodStyles[1], chipTypeList[1]);
        loadFoodTypeList(DiningStyle.randomDining().label, foodTypeList);
        loadTodayFood(todayFood);

        // 유저가 게스트인지 호스트인지 알아온다.
        // 값은 userType String에 저장됨.
        getUserType();

        // 지원하기 카드뷰 관련 코드
        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/plate-f5144.appspot.com/o/chef.png?alt=media&token=2e0e6f43-2523-482e-82ff-f5cd5ad54e19")
                .fit().centerCrop().into(applyImage);

        return v;
    }

    private void initCompleteLoadedArray() {
        // 모든 값이 불러왔는지 확인하는 배열 초기화
        completeLoaded = new boolean[LIST_COUNT];
        for (int i = 0; i < LIST_COUNT; i++)
            completeLoaded[i] = false;
        // 뷰가 생성되면 불러온 리스트의 수를 0으로 초기화
        current = 0;
    }

    private void changeGuestToHost() {
        mDatabase.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot guestSnapshot = snapshot.child("Guest");
                DatabaseReference hostReference = mDatabase.child("User").child("Host");

                Map<String, String> bookmark = (Map<String, String>)guestSnapshot.child(userUID).child("Bookmark").getValue();
                Map<String, String> profile = (Map<String, String>)guestSnapshot.child(userUID).child("Profile").getValue();
                Map<String, Map<String, String>> reservation = (Map<String, Map<String, String>>)guestSnapshot.child(userUID).child("Reservation").getValue();

                // User에 있는 모든 값을 지운다.
                guestSnapshot.child(userUID).getRef().removeValue();

                // Host에 추가한다.
                hostReference.child(userUID).child("Profile").setValue(profile);
                hostReference.child(userUID).child("Profile").child("Rating").setValue(0);
                hostReference.child(userUID).child("Profile").child("RatingCount").setValue(0);
                hostReference.child(userUID).child("Profile").child("Description").setValue("셰프가 되신 것을 환영합니다 :D");
                hostReference.child(userUID).child("Bookmark").setValue(bookmark);
                hostReference.child(userUID).child("Reservation").setValue(reservation);
                hostReference.child(userUID).child("Status").setValue("WAITING");

                // 다이닝 지원 엑티비티 로드
                Intent intent = new Intent(getActivity(), RegisterInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("MEMBER_TYPE", "HOST");
                bundle.putString("NAME", "셰프");
                bundle.putString("APPLY", "TRUE");
                intent.putExtra("REGISTER_DATA", bundle);

                getUserType();

                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * 다이닝 Key 값에서 Timestamp의 값을 추출하는 함수
     * @param dataSnapshot 추출하고자하는 다이닝의 Datasnapshot
     * @return 다이닝 Key 값에 적혀있는 Timestamp 값
     */
    private Long getTimestamp(DataSnapshot dataSnapshot) {
        String key = dataSnapshot.getKey();
        return Long.parseLong(key.substring(key.lastIndexOf("-") + 1));
    }

    /**
     * DataSnapshot의 루트를 불러와 자식 노드를 TimeStamp에 맞춰 최신 순으로 정렬하는 메소드
     * 다이닝 Key 값이 HostUID-Timestamp로 이루어진 것을 이용하여 정렬하는 것이다.
     * @param root 다이닝 루트 노드
     * @return 루트 노드의 자식을 최신순으로 정렬한 배열
     */
    private DataSnapshot[] sortSnapshot(DataSnapshot root) {
        // root의 자식을 불러와 ArrayList에 저장
        List<DataSnapshot> temp = new ArrayList<>();
        for (DataSnapshot snapshot : root.getChildren()) {
            temp.add(snapshot);
        }

        // ArrayList를 배열로 변환
        DataSnapshot[] array = temp.toArray(new DataSnapshot[temp.size()]);

        // Sorting을 하기 위한 임시 데이터 변수
        DataSnapshot tmp;

        // Selection Sort를 이용하여 DataSnapshot을 정렬
        for (int i = 0; i < array.length - 1; i++) {
            for (int j = i + 1; j < array.length; j++) {
                if (getTimestamp(array[i]) < getTimestamp(array[j])) {
                    tmp = array[i];
                    array[i] = array[j];
                    array[j] = tmp;
                }
            }
        }

        return array;
    }

    /**
     * Datasnapshot child를 뒤집기 위한 메소드
     * @param root Root DataSnapshot
     * @return Reversed DataSnapshot
     */
    private List<DataSnapshot> reverseSnapshot(DataSnapshot root) {
        List<DataSnapshot> temp = new ArrayList<>();
        for (DataSnapshot snapshot : root.getChildren()) {
            temp.add(snapshot);
        }
        Collections.reverse(temp);

        return temp;
    }


    private void loadRecentList(final HomeHorizontalList mList) {
        mList.setTitle("최근에 올라온 음식이에요.");
        mDatabase.child("Dining").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshots) {
                int loadCount = 0;

                for (DataSnapshot snapshot : sortSnapshot(snapshots)) {
                    HomeCardData data = snapshot.getValue(HomeCardData.class);
                    data.setDiningUID(snapshot.getKey());
                    data.setImageUri(snapshot.getKey() + "/" + snapshot.child("images/1").getValue(String.class));
                    mList.addData(data);

                    // 최대 10개만 불러온다.
                    if (loadCount++ >= MAX_ITEM_COUNT) break;
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
                for (DataSnapshot snapshot : reverseSnapshot(snapshots)) {
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
        mDatabase.child("Dining").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshots) {
                int loadCount = 0;

                for (DataSnapshot snapshot : snapshots.getChildren()) {
                    if (((List<String>)snapshot.child("style").getValue()).contains(foodStyle.toString())) {
                        HomeCardData data = snapshot.getValue(HomeCardData.class);
                        data.setDiningUID(snapshot.getKey());
                        data.setImageUri(snapshot.getKey() + "/" + snapshot.child("images/1").getValue(String.class));
                        mList.addData(data);

                        // 최대 10개만 불러온다.
                        if(loadCount++ >= MAX_ITEM_COUNT) break;
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
        mDatabase.child("Dining").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshots) {
                int loadCount = 0;

                for (DataSnapshot snapshot : snapshots.getChildren()) {
                    if (snapshot.child("subtitle").getValue(String.class).equals(type)) {
                        HomeCardData data = snapshot.getValue(HomeCardData.class);
                        data.setDiningUID(snapshot.getKey());
                        data.setImageUri(snapshot.getKey() + "/" + snapshot.child("images/1").getValue(String.class));
                        mList.addData(data);

                        // 최대 10개만 불러온다.
                        if (loadCount++ >= MAX_ITEM_COUNT) break;
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
                String image = path + "/" + snapshot.child("Dining").child(path).child("images/1").getValue(String.class);
                view.launch(title, image, path);

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

            // Loaded Array를 다시 전부 초기화
            initCompleteLoadedArray();

            // 만약에 Swipe를 통해서 Refreshing을 하고 있던거였으면 false로 바꿈.
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    /**
     * 유저가 Guest인지 Host인지를 구분하여
     * 유저가 Guest인 경우 지원하기 CardView를 보여주고
     * 유저가 Host인 경우 다이닝 추가하기 CardView를 보여준다.
     */
    private void getUserType() {

        mDatabase.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userType = snapshot.child("Guest").hasChild(userUID) ? "Guest" : "Host";

                if (userType.equals("Host")) {
                    applyCardView.setVisibility(View.GONE);
                    addDiningPlanCardView.setVisibility(View.VISIBLE);

                    if (snapshot.child(userType).child(userUID).child("Status").getValue(String.class).equals("WAITING")) {

                        addDiningPlanCardView.setEnabled(false);
                        addDiningPlanBackground.setBackgroundColor(Color.DKGRAY);
                        addDiningPlanTextView.setText(addDiningPlanTextView.getText().toString() + "\n[심사 진행 중]");
                    }
                }

                if (userType.equals("Guest"))
                    addDiningPlanCardView.setVisibility(View.GONE);
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

        // 모든 리스트 데이터 삭제
        recentList.removeAllData();
        postList.removeAllData();
        imageSlider.removeAllData();
        chipTypeList[0].removeAllData();
        chipTypeList[1].removeAllData();
        foodTypeList.removeAllData();

        FoodStyle[] styles = FoodStyle.randomFoodStyles(2);

        // 리스트에 값 다시 입력
        loadRecentList(recentList);
        loadPlatePost(postList);
        loadImageSlider(imageSlider);
        loadChipList(styles[0], chipTypeList[0]);
        loadChipList(styles[1], chipTypeList[1]);
        loadFoodTypeList(DiningStyle.randomDining().label, foodTypeList);
        loadTodayFood(todayFood);
    }
}
