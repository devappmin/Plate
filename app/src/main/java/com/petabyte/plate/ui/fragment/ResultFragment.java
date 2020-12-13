package com.petabyte.plate.ui.fragment;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.petabyte.plate.R;
import com.petabyte.plate.adapter.ResultBottomSheetListAdapter;
import com.petabyte.plate.data.FoodStyle;
import com.petabyte.plate.data.ResultDetailData;
import com.petabyte.plate.ui.activity.SearchActivity;
import com.petabyte.plate.ui.view.ResultDetailBottomSheet;
import com.petabyte.plate.utils.ConnectionCodes;
import com.petabyte.plate.utils.LogTags;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ResultFragment extends Fragment implements OnMapReadyCallback,
                                                        GoogleMap.OnInfoWindowClickListener,
                                                        GoogleMap.InfoWindowAdapter,
                                                        GoogleMap.OnCameraChangeListener {

    private CardView searchButton;
    private TextView searchTextView;

    // Created but never called.
    private ConstraintLayout bottomSheet;
    private BottomSheetBehavior bottomSheetBehavior;

    private RecyclerView bottomSheetRecyclerView;
    private ResultBottomSheetListAdapter recyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private LottieAnimationView loadingAnimation;

    private DatabaseReference databaseReference;

    private String searchValue;
    private Long searchTimestamp;
    private String searchLocation;
    private int searchPeople;
    private List<FoodStyle> foodStyles;

    private StringBuilder searchTextBuilder;

    private MapView mapView = null;
    private GoogleMap googleMap = null;

    private List<Marker> markerList;

    String cvSearchTimestamp, cvStartTimestamp; // YY년 MM월 dd일 HH시 패턴으로 변환된 사용자 입력 Timestamp
    SimpleDateFormat formatter; // Timestamp 비교에 사용될 객체

    // marker의 개수에 따라 동적으로 지도의 move와 zoom level을 설정해 주기 위한 변수
    LatLngBounds bounds;
    CameraUpdate cu;
    boolean moveCameraFlag = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_result, container, false);

        // region GET VALUES FROM BUNDLE
        // 검색한 내용을 검색 버튼에 보여주기 위한 String Builder
        searchTextBuilder = new StringBuilder();

        // Timestamp 변환에 사용되는 SimpleDateFormat객체의 패턴을 정의
        //formatter = new SimpleDateFormat("YY년 MM월 dd일 HH시");

        formatter = new SimpleDateFormat("YY-MM-dd HH시", new Locale("ko", "KR"));

        // Bundle을 통해서 값을 불러온다.
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if (bundle.getString("search") != null) {
                searchValue = bundle.getString("search");
                searchTextBuilder.append(searchValue + "/");
            }

            if (bundle.getLong("timestamp") >= 0) {
                searchTimestamp = bundle.getLong(("timestamp"));
                cvSearchTimestamp = convertTimestamp(searchTimestamp);
                searchTextBuilder.append(convertTimestamp(searchTimestamp) + "/");
            }

            if (bundle.getInt("people") != 0) {
                searchPeople = bundle.getInt("people");
                searchTextBuilder.append(searchPeople + "/");
            }

            if (bundle.getString("location") != null) {
                searchLocation = bundle.getString("location");
                searchTextBuilder.append(searchLocation + "/");
            }

            if (bundle.getStringArrayList("foodStyles") != null)
                foodStyles = FoodStyle.getFoodStyles(bundle.getStringArrayList("foodStyles"));

            searchTextBuilder.deleteCharAt(searchTextBuilder.lastIndexOf("/"));
        } else
            searchValue = "";
        // endregion

        searchButton = (CardView)v.findViewById(R.id.search_card_fm_result);
        searchTextView = (TextView)v.findViewById(R.id.search_tv_fm_result);
        mapView = (MapView)v.findViewById(R.id.map_view_fm_result);
        loadingAnimation = (LottieAnimationView)v.findViewById(R.id.loading_lottie_fm_result);
        bottomSheet = (ConstraintLayout)v.findViewById(R.id.recommend_bottom_sheet_fm_result);
        bottomSheetRecyclerView = (RecyclerView)v.findViewById(R.id.recommend_rv_fm_result);

        // Bottom Sheet 내부에 있는 Recycler View를 연결시켜주는 부분
        layoutManager = new LinearLayoutManager(getContext(),RecyclerView.VERTICAL, false);
        bottomSheetRecyclerView.setLayoutManager(layoutManager);
        recyclerAdapter = new ResultBottomSheetListAdapter();
        bottomSheetRecyclerView.setAdapter(recyclerAdapter);

        // Bottom Sheet와 BottomSheetBehavior 간의 연결
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        // 지도를 시작하는 부분
        mapView.getMapAsync(this);

        searchTextView.setText(searchTextBuilder);

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

        databaseReference = FirebaseDatabase.getInstance().getReference();

        return v;
    }

    // region 맵 뷰를 위한 생명주기

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mapView != null)
            mapView.onCreate(savedInstanceState);
    }

    // endregion

    public String convertTimestamp(long time) {
        String date = (String) formatter.format(new Timestamp(time));
        return date;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (googleMap != null){
            this.googleMap = googleMap;
            this.googleMap.setOnInfoWindowClickListener(this);
            this.googleMap.setInfoWindowAdapter(this);
            this.googleMap.setOnCameraChangeListener(this);
            this.googleMap.getUiSettings().setRotateGesturesEnabled(false);
        }

        LatLng currentPost = null;

        Log.d(LogTags.IMPORTANT, searchLocation + " SEARCH");

        if (searchLocation != null && searchLocation != "") {
            currentPost = getLocationFromAddress(getContext(), searchLocation);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPost, 13));
            moveCameraFlag = true;
        } else {
            //currentPost = getLocationFromAddress(getContext(), "서울");
        }
        loadDatabaseWithException(null);
    }

    /**
     * 문자열을 통해서 위도 및 경도를 불러오는 함수
     * @param context 적용시키고자 하는 context
     * @param strAddress 주소 문자열
     * @return 위도 및 경도 값을 포함한 주소
     */
    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return p1;
    }

    /**
     * 지도에 마커를 찍어주는 함수
     * 하지만 해당 마커를 입력해도 아무런 창이 뜨지 않는다.
     *
     * @param context 적용시키고자 하는 context
     * @param strAddress 주소 문자열
     * @param title 마커에 표시할 타이틀
     */
    public void addMarker(Context context, String strAddress, String title) {
        LatLng location = getLocationFromAddress(context, strAddress);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(location);
        markerOptions.title(title);

        googleMap.addMarker(markerOptions);
    }

    /**
     * ResultDetailData를 통해 지도에 마커를 찍어주는 함수
     *
     * @param context 적용시키고자 하는 context
     * @param data 파이어베이스 데이터가 들어있는 ResultDetailData 변수
     */
    public void addMarker(Context context, ResultDetailData data) {
        LatLng location = getLocationFromAddress(context, data.getLocation().get("location"));

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(location);
        markerOptions.title(data.getTitle());
        markerOptions.snippet(String.format(Locale.KOREA, "%,d", data.getPrice()) + "원");
        Marker marker = googleMap.addMarker(markerOptions);
        marker.setTag(data);
    }


    public void loadDatabaseWithException(String... exceptions) {
        databaseReference.child("Dining").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshots) {
                List<ResultDetailData> datums = new ArrayList<>();
                boolean valueFlag = false; // 키워드 검색 관련 flag
                boolean timeFlag = false; // 시간 검색 관련 flag
                boolean peopleFlag = false; // 인원수 검색 관련 flag

                for (DataSnapshot snapshot : snapshots.getChildren()) {
                    if (snapshot.child("location").child("location").getValue(String.class) != null) {
                        ResultDetailData detailData = snapshot.getValue(ResultDetailData.class);
                        detailData.setDiningUID(snapshot.getKey());

                        // 키워드 입력으로 검색
                        if(!searchValue.equals("")){
                            if(detailData.getTitle().contains(searchValue)){
                                //Log.d("ji1dev", "키워드 포함된 다이닝 타이틀:"+detailData.getTitle());
                                datums.add(detailData);
                                valueFlag = true; // 매칭되는게 하나라도 있으면 true
                            }
                        // 기타 필터링 조건으로 검색
                        }else{
                            long maxPeople = detailData.getCount().get("max");
                            long currentPeople = detailData.getCount().get("current");

                            // 현재 예약 가능한 인원수와 입력된 인원수를 비교하여 남은 자리가 있는 다이닝인지 필터링
                            if(currentPeople + searchPeople <= maxPeople){
                                peopleFlag = true;
                                //Log.d("ji1dev", "다이닝 타이틀:"+detailData.getTitle()+"============");
                                //Log.d("ji1dev", "선택인원:"+searchPeople+", 현재인원:"+currentPeople+", 최대인원:"+maxPeople);

                                // 다이닝 스케쥴과 입력된 날짜 및 시간을 비교하여 가능한 시간대가 있는지 필터링 후 marker 추가함
                                if(searchTimestamp != null) {
                                    for(Map<String, Long> startTime : detailData.getSchedules().values()) {

                                        cvStartTimestamp = convertTimestamp(startTime.get("start"));

                                        // 사용자가 검색한 시간과 DB에 있는 다이닝 스케쥴 중 일치하는 다이닝이 있으면 플래그 활성화, marker추가
                                        if (cvSearchTimestamp.equals(cvStartTimestamp)) {
                                            //Log.d("ji1dev", "예약가능시간 : "+cvStartTimestamp);
                                            timeFlag = true;
                                            datums.add(detailData);
                                        }
                                        // timeFlag 활성화되면 해당 다이닝의 다른 시간대는 확인하지 않음
                                        if(timeFlag) break;
                                    }
                                }else{
                                    // 유저 입력 timestamp 가 존재하지 않으면 위치와 인원 정보만 필터링 뒤 marker 생성
                                    //addMarker(getContext(), detailData);
                                    datums.add(detailData);
                                }
                            }
                        }
                    }
                }
                // 키워드 검색, 시간, 인원 검색결과가 모두 없으면 모든 marker를 추가해준다
                if(!valueFlag && !timeFlag && !peopleFlag){
                    //Log.d("ji1dev", "모든 Flag 비활성화 됨. 전체 data에 대해 marker를 추가함");
                    for (DataSnapshot snapshot : snapshots.getChildren()) {
                        if (snapshot.child("location").child("location").getValue(String.class) != null) {
                            ResultDetailData detailData = snapshot.getValue(ResultDetailData.class);
                            detailData.setDiningUID(snapshot.getKey());
                            datums.add(detailData);
                        }
                    }
                }
                DrawMarkers drawMarkers = new DrawMarkers(getContext(), datums);
                drawMarkers.execute();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        ResultDetailData data = (ResultDetailData)marker.getTag();
        ResultDetailBottomSheet bottomSheet = new ResultDetailBottomSheet(data);

        bottomSheet.show(getFragmentManager(), bottomSheet.getTag());
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View v = View.inflate(getContext(), R.layout.view_result_marker_info, null);

        TextView title = (TextView)v.findViewById(R.id.title_tv_v_marker_info);
        TextView price = (TextView)v.findViewById(R.id.price_tv_v_marker_info);

        title.setText(marker.getTitle());
        price.setText(marker.getSnippet());
        return v;
    }

    @Override
    public View getInfoContents(Marker marker) {

        return null;
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

        boolean state = false;

        recyclerAdapter.removeAllData();
        for (Marker marker : markerList) {
            if (Math.abs(marker.getPosition().latitude - cameraPosition.target.latitude) * (Math.pow(cameraPosition.zoom, 2) / 169) < 0.04 &&
            Math.abs(marker.getPosition().longitude - cameraPosition.target.longitude) * (Math.pow(cameraPosition.zoom, 2) / 169) < 0.04) {
                // 현재 화면에 있는 그나마 가까운 marker 리스트
                recyclerAdapter.addData((ResultDetailData)marker.getTag());

                if (!state) state = true;
            }
        }

        // 만약에 리스트를 하나도 불러오지 못했으면 숨
        bottomSheetBehavior.setHideable(!state);

        // state의 상태에 따라 Bottom Sheet의 상태도 변환
        bottomSheetBehavior.setState(state ? BottomSheetBehavior.STATE_COLLAPSED : BottomSheetBehavior.STATE_HIDDEN);

        recyclerAdapter.notifyDataSetChanged();
    }

    /**
     * 지도에 마커를 찍는 AsyncTask
     * 클래스의 인자로 Context와 ResultDetailData의 리스트를 받는다.
     *
     * AsyncTask를 사용하여 지도를 로딩하는데 리소스를 많이 차지하는 부분을
     * 작업 스레드로 옮기고 그 동안에 LottieAnimation을 보여줘서 로딩하는 화면을 보여준다.
     */
    private class DrawMarkers extends AsyncTask<Void, Void, Boolean> {

        private Context context;
        private List<ResultDetailData> datum;
        private List<MarkerOptions> options;

        /**
         * DrawMarkers의 생성자
         * @param context 해당 Fragment의 Context
         * @param datum 마커에 관한 정보를 갖고 있는 ResultDetailData 리스트
         */
        public DrawMarkers(Context context, List<ResultDetailData> datum) {
            this.context = context;
            this.datum = datum;
        }

        /**
         * 맵을 다 불러오기 이전에 맵과 검색 창을 숨기고
         * Lottie Animation을 보여줘서 로딩하는 UI를 보여준다.
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            options = new ArrayList<>();
            markerList = new ArrayList<>();

            loadingAnimation.setVisibility(View.VISIBLE);
            searchButton.setVisibility(View.GONE);
            mapView.setVisibility(View.GONE);
            bottomSheet.setVisibility(View.GONE);
        }

        /**
         * 백그라운드에서 할 일
         */
        @Override
        protected Boolean doInBackground(Void... voids) {
            LatLngBounds.Builder latlngBounds = LatLngBounds.builder();
            LatLng location;
            int boundCnt = 0;

            // datum에서 데이터를 끌어모은다.
            for (ResultDetailData data : datum) {
                // data의 지역 값을 통해서 위도와 경도를 알아낸다.
                location = getLocationFromAddress(context, data.getLocation().get("location"));

                // MarkerOption을 통해서 마커에 관한 정보를 입력받는다.
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(location);
                markerOptions.title(data.getTitle());
                markerOptions.snippet(String.format(Locale.KOREA, "%,d", data.getPrice()) + "원");

                // options 리스트에 해당 MarkerOption을 추가한다.
                options.add(markerOptions);
                latlngBounds.include(location);
                boundCnt++;
            }

            //set default LatLng bound on 서울
            if(boundCnt == 0){
                //Log.d("ji1dev", "기본값으로 bound설정됨");
                latlngBounds.include(getLocationFromAddress(context, "서울"));
            }

            bounds = latlngBounds.build();
            return true;
        }

        /**
         * 백그라운드에서 처리한 것이 끝이 났을 경우 할 일
         */
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            // 백그라운드에서 갖고 온 options 리스트를 통해서 마커를 생성한다.
            for (int i = 0; i < options.size(); i++) {
                Marker marker = googleMap.addMarker(options.get(i));
                marker.setTag(datum.get(i));
                markerList.add(marker);
            }

            // moveCamera를 수행하지 않은 경우 (위치 기반으로 검색을 하지 않은 모든 경우) marker기반 동적으로 위치를 잡아준다
            if(moveCameraFlag == false){
                int width = getResources().getDisplayMetrics().widthPixels;
                int height = getResources().getDisplayMetrics().heightPixels;
                int padding = (int) (width * 0.2);
                cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                googleMap.moveCamera(cu);
            }

            // 전부 추가했으면 지도와 검색 버튼을 보여주고 Lottie 애니메이션을 숨긴다.
            mapView.setVisibility(View.VISIBLE);
            searchButton.setVisibility(View.VISIBLE);
            bottomSheet.setVisibility(View.VISIBLE);
            loadingAnimation.setVisibility(View.GONE);
        }
    }
}
