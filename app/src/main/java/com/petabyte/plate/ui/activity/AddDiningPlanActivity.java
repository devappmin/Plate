package com.petabyte.plate.ui.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.petabyte.plate.R;
import com.petabyte.plate.data.FoodStyle;
import com.petabyte.plate.utils.ConnectionCodes;

import java.util.ArrayList;
import java.util.Calendar;

public class AddDiningPlanActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context;

    private ImageButton cancelButton;
    private EditText style;
    private EditText location;
    private ChipGroup styles_chipGroup;
    private EditText date_editText;
    private EditText startTime_editText;
    private EditText endTime_editText;
    private ImageView add_time;
    private ChipGroup times_chipGroup;
    private EditText dish_editText;
    private ChipGroup dishes_chipGroup;
    private Button submitButton;

    private ArrayList<String> selectedStyles = new ArrayList<>();
    private ArrayList<String> styles = new ArrayList<>();
    private ArrayList<String> times = new ArrayList<>();
    private ArrayList<String> dishes = new ArrayList<>();

    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dining_plan);
        context = this;

        cancelButton = (ImageButton)findViewById(R.id.cancel_button_addPlan);
        style = (EditText)findViewById(R.id.editText_style_addplan);
        location = (EditText)findViewById(R.id.editText_location_addplan);
        styles_chipGroup = (ChipGroup)findViewById(R.id.chip_group_styles_addPlan);
        date_editText = (EditText)findViewById(R.id.editText_date_addPlan);
        startTime_editText = (EditText)findViewById(R.id.editText_start_time_addPlan);
        endTime_editText = (EditText)findViewById(R.id.editText_end_time_addPlan);
        add_time = (ImageView)findViewById(R.id.add_time_addPlan);
        times_chipGroup = (ChipGroup)findViewById(R.id.chip_group_times_addPlan);
        dish_editText = (EditText)findViewById(R.id.editText_dish_addPlan);
        dishes_chipGroup = (ChipGroup)findViewById(R.id.chip_group_dishes_addPlan);
        submitButton = (Button)findViewById(R.id.submit_button_addPlan);

        cancelButton.setOnClickListener(this);
        style.setOnClickListener(this);
        location.setOnClickListener(this);
        date_editText.setOnClickListener(this);
        startTime_editText.setOnClickListener(this);
        endTime_editText.setOnClickListener(this);
        add_time.setOnClickListener(this);
        submitButton.setOnClickListener(this);
        dish_editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    final String dish = dish_editText.getText().toString();
                    boolean containsItem = false;
                    for (String item : dishes) {
                        if (item.contains(dish))
                            containsItem = true;
                    }
                    if(!containsItem) {
                        dishes.add(dish);
                        Chip chip = new Chip(dishes_chipGroup.getContext());
                        chip.setText(dish);
                        chip.setCloseIcon(context.getDrawable(R.drawable.ic_baseline_cancel_24));
                        chip.setCloseIconVisible(true);
                        chip.setTextColor(Color.parseColor("#FFFFFF"));
                        chip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorPrimary)));
                        chip.setOnCloseIconClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dishes_chipGroup.removeView(v);
                                dishes.remove(dish);
                            }
                        });
                        dishes_chipGroup.addView(chip);
                        dish_editText.getText().clear();
                        return true;
                    } else {
                        Snackbar.make(v, "이미 추가한 메뉴에요.", 3000).show();
                    }
                }
                return true;
            }
        });


        //get FoodStyle labels
        for(FoodStyle style : FoodStyle.values()) {
            styles.add("#" + style.label);
        }
    }

    @Override
    public void onClick(final View v) {
        if (v == cancelButton) {
            finish();
        } else if (v == location) {
            Intent i = new Intent(AddDiningPlanActivity.this, SearchAddressActivity.class);
            startActivityForResult(i, ConnectionCodes.REQUEST_SEARCH_ADDRESS);
        } else if (v == style) {
            final String[] items = styles.toArray(new String[styles.size()]);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("스타일을 선택해 주세요.");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final String selectedItem = items[which];
                    //chip의 중복 추가를 방지하기 위해 selectedStyles로 관리
                    boolean containsItem = false;
                    for (String item : selectedStyles) {
                        if (item.contains(selectedItem))
                            containsItem = true;
                    }
                    if (!containsItem) {
                        selectedStyles.add(selectedItem);
                        style.setText(selectedItem);
                        //add chip to chipgroup
                        Chip chip = new Chip(styles_chipGroup.getContext());
                        chip.setText(selectedItem);
                        chip.setCloseIcon(context.getDrawable(R.drawable.ic_baseline_cancel_24));
                        chip.setCloseIconVisible(true);
                        chip.setTextColor(Color.parseColor("#FFFFFF"));
                        chip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorPrimary)));
                        chip.setOnCloseIconClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                styles_chipGroup.removeView(v);
                                selectedStyles.remove(selectedItem);
                            }
                        });
                        styles_chipGroup.addView(chip);
                    } else {
                        Snackbar.make(v, "이미 선택한 스타일이에요.", 3000).show();
                    }
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else if (v == date_editText) {
            // 현재 날짜를 기준으로 년 월 일 변수 할당
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            // DatePickerDialog를 통해 다이얼로그 생성
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.CustomDatePicker,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            // 입력된 년도, 월, 일을 각 변수에 삽입
                            String date;
                            mYear = year;
                            mMonth = monthOfYear + 1;
                            mDay = dayOfMonth;

                            // date_time 문자열에 YYMMDD를 삽입
                            date = mYear - 2000 + "/" + mMonth + "/" + mDay;

                            date_editText.setText(date);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();

        } else if (v == startTime_editText) {
            // 현재 시간을 기준으로 시간 및 분 변수 할당
            final Calendar c = Calendar.getInstance();

            // Time Picker 다이어로그 호출
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            String time;
                            // 각 변수에 시간 및 분을 저장
                            mHour = hourOfDay;
                            mMinute = minute;

                            // date_time 문자열에 시간 포함, 오전 10시보다 이를 경우 앞에 0 추가하여 표시
                            if(mHour < 10)
                                time = "0" + mHour + ":";
                            else
                                time = mHour + ":";

                            // 10분보다 작을 경우 앞에 0 추가하여 표시
                            if (minute < 10)
                                time += "0" + mMinute;
                            else
                                time += minute;

                            // dateTextView에 date_time 값을 삽입
                            startTime_editText.setText(time);
                        }
                    }, 18, 0, false);
            timePickerDialog.setTitle("시작 시간");
            timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            timePickerDialog.show();
        } else if (v == endTime_editText) {
            // 현재 시간을 기준으로 시간 및 분 변수 할당
            final Calendar c = Calendar.getInstance();

            // Time Picker 다이어로그 호출
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            String time;
                            // 각 변수에 시간 및 분을 저장
                            mHour = hourOfDay;
                            mMinute = minute;

                            // date_time 문자열에 시간 포함, 오전 10시보다 이를 경우 앞에 0 추가하여 표시
                            if(mHour < 10)
                                time = "0" + mHour + ":";
                            else
                                time = mHour + ":";

                            // 10분보다 작을 경우 앞에 0 추가하여 표시
                            if (minute < 10)
                                time += "0" + mMinute;
                            else
                                time += minute;
                            // dateTextView에 date_time 값을 삽입
                            endTime_editText.setText(time);
                        }
                    }, 18, 0, false);
            timePickerDialog.setTitle("종료 시간");
            timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            timePickerDialog.show();
        } else if (v == add_time) {
            //날짜, 시작 시간, 종료 시간이 입력되지 않았다면
            if (date_editText.getText().toString().replace(" ", "").equals("")
                    || startTime_editText.getText().toString().replace(" ", "").equals("")
                    || endTime_editText.getText().toString().replace(" ", "").equals("")) {
                Snackbar.make(v, "빈 칸이 있어요.", 3000).show();
            } else {
                String date = date_editText.getText().toString();
                String startTime = startTime_editText.getText().toString();
                String endTime = endTime_editText.getText().toString();

                //시작 시간이 종료 시간보다 늦다면
                if (startTime.compareTo(endTime) > 0) {
                    Snackbar.make(v, "시작 시간이 종료 시간보다 늦어요.", 3000).show();
                } else {
                    //times 배열에 item을 14자리 숫자로 저장(앞 8자리 날짜, 중간 4자리 시작 시간, 끝 4자리 종료 시간)
                    String timeNumber = Integer.parseInt(date.replace("/", "")) + 20000000//2자릿수 연도를 4자리로 바꿈 ex)20 -> 2020
                            + startTime.replace(":", "")
                            + endTime.replace(":", "");

                    date = "20" + date.replaceFirst("/", "년 ")
                            .replaceFirst("/", "월 ")
                            + "일";
                    final String time = date + "   " + startTime + " ~ " + endTime;

                    boolean containsItem = false;
                    for (String item : times) {
                        if (item.contains(timeNumber))
                            containsItem = true;
                    }
                    if(!containsItem) {
                        times.add(timeNumber);
                        Chip chip = new Chip(times_chipGroup.getContext());
                        chip.setText(time);
                        chip.setCloseIcon(context.getDrawable(R.drawable.ic_baseline_cancel_24));
                        chip.setCloseIconVisible(true);
                        chip.setTextColor(Color.parseColor("#FFFFFF"));
                        chip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorPrimary)));
                        chip.setOnCloseIconClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                times_chipGroup.removeView(v);
                                times.remove(time);
                            }
                        });
                        times_chipGroup.addView(chip);
                    } else {
                        Snackbar.make(v, "이미 입력한 시간이에요.", 3000).show();
                    }
                }
            }
        } else if (v == submitButton) {
            //upload to realtime database
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case ConnectionCodes.REQUEST_SEARCH_ADDRESS:
                if (resultCode == RESULT_OK) {
                    String data = intent.getExtras().getString("data");
                    if (data != null) {
                        location.setText(data);
                    }
                }
                break;
        }
    }
}