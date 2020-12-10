package com.petabyte.plate.ui.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
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

import com.petabyte.plate.R;
import com.petabyte.plate.data.FoodStyle;
import com.petabyte.plate.ui.activity.SearchActivity;
import com.petabyte.plate.utils.ConnectionCodes;
import com.petabyte.plate.utils.LogTags;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ResultFragment extends Fragment {

    private CardView searchButton;
    private TextView searchTextView;

    private String searchValue;
    private Long searchTimestamp;
    private String searchLocation;
    private int searchPeople;
    private List<FoodStyle> foodStyles;

    private StringBuilder searchTextBuilder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_result, container, false);

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

        searchButton = (CardView)v.findViewById(R.id.search_card_fm_result);
        searchTextView = (TextView)v.findViewById(R.id.search_tv_fm_result);

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

        return v;
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.KOREA);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("MM월 dd일", cal).toString();
        return date;
    }
}
