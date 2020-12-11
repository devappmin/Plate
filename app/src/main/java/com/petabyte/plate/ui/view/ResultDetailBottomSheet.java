package com.petabyte.plate.ui.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.petabyte.plate.R;
import com.petabyte.plate.data.ResultDetailData;
import com.petabyte.plate.ui.activity.DetailActivity;

import java.util.Locale;

public class ResultDetailBottomSheet extends BottomSheetDialogFragment {

    private TextView titleTextView;
    private TextView subtitleTextView;
    private TextView dateTextView;
    private TextView locationTextView;
    private TextView priceTextView;
    private Button detailButton;
    private ImageButton exitButton;


    private ResultDetailData data;

    public ResultDetailBottomSheet(ResultDetailData data) {
        this.data = data;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.view_result_detail_bottom_sheet, container, false);

        titleTextView = (TextView)v.findViewById(R.id.title_tv_result_detail_bs);
        subtitleTextView = (TextView)v.findViewById(R.id.subtitle_tv_result_detail_bs);
        dateTextView = (TextView)v.findViewById(R.id.date_tv_result_detail_bs);
        locationTextView = (TextView)v.findViewById(R.id.location_tv_result_detail_bs);
        priceTextView = (TextView)v.findViewById(R.id.price_tv_result_detail_bs);

        detailButton = (Button)v.findViewById(R.id.detail_bt_result_detail_bs);
        exitButton = (ImageButton)v.findViewById(R.id.exit_ib_result_detail_bs);

        return v;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        titleTextView.setText(data.getTitle());
        subtitleTextView.setText(data.getSubtitle());
        dateTextView.setText(data.getDate());
        locationTextView.setText(data.getLocation().get("location") + "\n" + data.getLocation().get("detail"));
        priceTextView.setText(String.format(Locale.KOREA, "%,d", data.getPrice()) + "원");

        detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra("title", data.getTitle());
                intent.putExtra("diningUid", data.getDiningUID());
                startActivity(intent);
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }


}
