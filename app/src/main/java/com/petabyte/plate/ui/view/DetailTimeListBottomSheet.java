package com.petabyte.plate.ui.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.petabyte.plate.R;
import com.petabyte.plate.data.DiningMasterData;
import com.petabyte.plate.ui.activity.PurchaseActivity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;


public class DetailTimeListBottomSheet extends BottomSheetDialogFragment {

    private RadioGroup radioGroup;
    private TextView title;
    private TextView date;
    private Button purchaseButton;

    private DatabaseReference databaseReference;
    private DiningMasterData diningMasterData;
    private ArrayList<Map<String, Double>> schedules = new ArrayList<>();
    private Context context;

    private String title_String, diningUid_String;
    private long startTimestamp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.view_detail_time_list_bottom_sheet, container, false);
        context = v.getContext();

        title = (TextView)v.findViewById(R.id.title_DetailBottomSheet);
        date = (TextView)v.findViewById(R.id.date_DetailBottomSheet);
        radioGroup = (RadioGroup)v.findViewById(R.id.radioGroup_DetailBottomSheet);
        purchaseButton = (Button)v.findViewById(R.id.purchase_button_DetailBottomSheet);

        title_String = this.getArguments().getString("title");
        diningUid_String = this.getArguments().getString("diningUid");

        title.setText(this.getArguments().getString("title"));
        date.setText(this.getArguments().getString("date"));

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                purchaseButton.setEnabled(true);
            }
        });

        purchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent purchase = new Intent(context, PurchaseActivity.class);
                purchase.putExtra("title", title_String);
                purchase.putExtra("diningUid", diningUid_String);
                purchase.putExtra("start", startTimestamp);
                startActivity(purchase);
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("Dining");
        databaseReference.orderByKey().equalTo(diningUid_String).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    diningMasterData = dataSnapshot.getValue(DiningMasterData.class);
                    for (Map<String, Double> schedule : diningMasterData.getSchedules().values()) {
                        schedules.add(schedule);
                    }
                    Collections.sort(schedules, new Comparator<Map<String, Double>>() {
                        @Override
                        public int compare(Map<String, Double> o1, Map<String, Double> o2) {
                            if (o1.get("start") < o2.get("start")) {
                                return -1;
                            } else if (o1.get("start") > o2.get("start")) {
                                return 1;
                            }
                            return 0;
                        }
                    });
                    Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
                    Log.d("cu", Long.toString(currentTimestamp.getTime()));
                    for (int i = 0; i < schedules.size(); i++) {
                        final long startTime = schedules.get(i).get("start").longValue();
                        long endTime = schedules.get(i).get("end").longValue();
                        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, toDp(50));
                        params.setMarginStart(toDp(20));
                        String date = getTime(startTime) + " ~ " + getTime(endTime);
                        RadioButton radioButton = new RadioButton(context);
                        ColorStateList colorStateList = new ColorStateList(
                                new int[][]{
                                        new int[]{android.R.attr.state_enabled} //enabled
                                },
                                new int[]{getResources().getColor(R.color.colorPrimary)}
                        );
                        radioButton.setButtonTintList(colorStateList);
                        radioButton.setText(date);
                        radioButton.setTextSize(17);
                        if(startTime < currentTimestamp.getTime()) { //현재 시간보다 과거의 다이닝이라면
                            radioButton.setEnabled(false);
                        }
                        radioButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startTimestamp = startTime;
                            }
                        });
                        radioButton.setLayoutParams(params);
                        radioGroup.addView(radioButton);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return v;
    }

    private String getTime(long timeStamp) {
        Calendar cal = Calendar.getInstance(Locale.KOREA);
        cal.setTimeInMillis(timeStamp);
        String date = DateFormat.format("aa hh:mm", cal).toString();
        return date;
    }

    public int toDp(int size) {
        float scale = getResources().getDisplayMetrics().density;
        return (int)(size * scale + 0.5f);
    }
}