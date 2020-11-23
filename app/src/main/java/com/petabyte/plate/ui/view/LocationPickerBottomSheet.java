package com.petabyte.plate.ui.view;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.petabyte.plate.R;

import java.util.HashMap;
import java.util.Map;

public class LocationPickerBottomSheet extends BottomSheetDialogFragment {

    private Button submitButton;
    private GridLayout gridLayout;
    private TextView titleTextView;
    private TextSwitcher textSwitcher;

    private String head = "init";
    private Map<String, Integer> addresses = new HashMap<>();

    private enum PROGRESS {
        LARGE,
        DETAIL
    };
    private PROGRESS current = PROGRESS.LARGE;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.view_location_picker_bottom_sheet, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initAddress();

        submitButton = (Button)getView().findViewById(R.id.button_bottom_sheet);
        gridLayout = (GridLayout)getView().findViewById(R.id.grid_v_location_bottom_sheet);
        titleTextView = (TextView)getView().findViewById(R.id.title_v_location_bottom_sheet);
        textSwitcher = (TextSwitcher)getView().findViewById(R.id.switcher_v_location_bottom_sheet);

        textSwitcher.setInAnimation(getContext(), R.anim.slide_in_right);
        textSwitcher.setOutAnimation(getContext(), R.anim.slide_out_left);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (current == PROGRESS.LARGE && !head.equals("init")) {
                    gridLayout.removeAllViews();

                    final String temp = head;

                    Button location;
                    for(final String loc : getResources().getStringArray(addresses.get(head))) {
                        location = addView(loc);

                        location.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (head != loc) {
                                    head = temp + " " + loc;
                                    textSwitcher.setText(head);

                                    if(!submitButton.getText().equals("확인"))
                                        submitButton.setText("확인");
                                }
                            }
                        });

                        gridLayout.addView(location);
                    }

                    current = PROGRESS.DETAIL;
                }
                else if(current == PROGRESS.DETAIL) {
                    ItemClickListener listener = (ItemClickListener) getContext();
                    listener.onItemClick(head);
                    dismiss();
                }
            }
        });

        Button location;
        for (final String loc : getResources().getStringArray(R.array.spinner_region)) {
            location = addView(loc);

            location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(head != loc) {
                        head = loc;
                        textSwitcher.setText(head);
                    }
                }
            });

            // 버튼 추가
            gridLayout.addView(location);
        }
    }

    private Button addView(String name) {
        Button button = new Button(getContext());
        button.setText(name);
        button.setPadding(40, 10, 40, 10);

        // 투명한 버튼이지만 눌렀을 때 Ripple 효과를 내기 위해 사용
        TypedValue outValue = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        button.setBackgroundResource(outValue.resourceId);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setGravity(Gravity.CENTER);

        button.setLayoutParams(params);

        return button;
    }

    private void initAddress() {

        addresses.put("서울특별시", R.array.spinner_region_seoul);
        addresses.put("부산광역시", R.array.spinner_region_busan);
        addresses.put("대구광역시", R.array.spinner_region_daegu);
        addresses.put("인천광역시", R.array.spinner_region_incheon);
        addresses.put("광주광역시", R.array.spinner_region_gwangju);
        addresses.put("대전광역시", R.array.spinner_region_daejeon);
        addresses.put("울산광역시", R.array.spinner_region_ulsan);
        addresses.put("세종특별자치시", R.array.spinner_region_sejong);
        addresses.put("경기도", R.array.spinner_region_gyeonggi);
        addresses.put("강원도", R.array.spinner_region_gangwon);
        addresses.put("충청북도", R.array.spinner_region_chung_buk);
        addresses.put("충청남도", R.array.spinner_region_chung_nam);
        addresses.put("전라북도", R.array.spinner_region_jeon_buk);
        addresses.put("전라남도", R.array.spinner_region_jeon_nam);
        addresses.put("경상북도", R.array.spinner_region_gyeong_buk);
        addresses.put("경상남도", R.array.spinner_region_gyeong_nam);
        addresses.put("제주특별자치도", R.array.spinner_region_jeju);
    }

    public interface ItemClickListener {
        void onItemClick(String item);
    }
}
