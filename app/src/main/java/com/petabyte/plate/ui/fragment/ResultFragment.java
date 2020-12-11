package com.petabyte.plate.ui.fragment;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.petabyte.plate.R;
import com.petabyte.plate.data.DiningMasterData;
import com.petabyte.plate.data.FoodStyle;
import com.petabyte.plate.data.ResultDetailData;
import com.petabyte.plate.ui.activity.DetailActivity;
import com.petabyte.plate.ui.activity.SearchActivity;
import com.petabyte.plate.ui.view.ResultDetailBottomSheet;
import com.petabyte.plate.utils.ConnectionCodes;
import com.petabyte.plate.utils.LogTags;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ResultFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private CardView searchButton;
    private TextView searchTextView;

    private DatabaseReference databaseReference;

    private String searchValue;
    private Long searchTimestamp;
    private String searchLocation;
    private int searchPeople;
    private List<FoodStyle> foodStyles;

    private StringBuilder searchTextBuilder;

    private MapView mapView = null;
    private GoogleMap googleMap = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_result, container, false);

        // region GET VALUES FROM BUNDLE
        // 검색한 내용을 검색 버튼에 보여주기 위한 String Builder
        searchTextBuilder = new StringBuilder();

        // Bundle을 통해서 값을 불러온다.
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if (bundle.getString("search") != null) {
                searchValue = bundle.getString("search");
                searchTextBuilder.append(searchValue + "/");
            }

            if (bundle.getLong("timestamp") >= 0) {
                searchTimestamp = bundle.getLong(("timestamp"));
                searchTextBuilder.append(getDate(searchTimestamp) + "/");
            }

            if (bundle.getInt("people") != 0) {
                searchPeople = bundle.getInt("people");
                searchTextBuilder.append(searchPeople + "/");
            }

            if (bundle.getString("location") != null) {
                searchLocation = bundle.getString("location");
                searchTextBuilder.append(searchLocation + "/");
            }

            if (bundle.getStringArrayList("foodStyles") != null) foodStyles = FoodStyle.getFoodStyles(bundle.getStringArrayList("foodStyles"));

            searchTextBuilder.deleteCharAt(searchTextBuilder.lastIndexOf("/"));
        }
        // endregion

        searchButton = (CardView)v.findViewById(R.id.search_card_fm_result);
        searchTextView = (TextView)v.findViewById(R.id.search_tv_fm_result);
        mapView = (MapView)v.findViewById(R.id.map_view_fm_result);

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

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.KOREA);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("MM월 dd일", cal).toString();
        return date;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (googleMap != null){
            this.googleMap = googleMap;
            this.googleMap.setOnMarkerClickListener(this);
        }


        LatLng currentPost = null;

        Log.d(LogTags.IMPORTANT, searchLocation + " SEARCH");

        if (searchLocation != null && searchLocation != "") {
            currentPost = getLocationFromAddress(getContext(), searchLocation);
        } else {
            currentPost = getLocationFromAddress(getContext(), "서울");
        }

        loadDatabaseWithException(null);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPost, 13));
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

        Marker marker = googleMap.addMarker(markerOptions);
        marker.setTag(data);
    }


    public void loadDatabaseWithException(String... exceptions) {
        databaseReference.child("Dining").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshots) {
                for (DataSnapshot snapshot : snapshots.getChildren()) {
                    if (snapshot.child("location").child("location").getValue(String.class) != null) {
                        ResultDetailData detailData = snapshot.getValue(ResultDetailData.class);
                        detailData.setDiningUID(snapshot.getKey());
                        addMarker(getContext(), detailData);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        ResultDetailData data = (ResultDetailData)marker.getTag();
        ResultDetailBottomSheet bottomSheet = new ResultDetailBottomSheet(data);

        bottomSheet.show(getFragmentManager(), bottomSheet.getTag());

        return false;
    }
}
