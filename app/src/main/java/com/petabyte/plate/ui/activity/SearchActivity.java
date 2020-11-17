package com.petabyte.plate.ui.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.petabyte.plate.R;
import com.petabyte.plate.ui.fragment.NumberPickerDialog;
import com.petabyte.plate.utils.ConnectionCodes;
import com.petabyte.plate.utils.LogTags;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener, NumberPicker.OnValueChangeListener {

    private ImageButton cancelButton;
    private CardView    peopleCardView;
    private CardView    dateCardView;
    private CardView    locationCardView;
    private TextView    peopleTextView;
    private TextView    dateTextView;

    private String      date_time;
    private int         mYear;
    private int         mMonth;
    private int         mDay;
    private int         mHour;
    private int         mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        cancelButton = (ImageButton)findViewById(R.id.cancel_ib_av_search);
        peopleCardView = (CardView)findViewById(R.id.people_cv_av_search);
        dateCardView = (CardView)findViewById(R.id.date_cv_av_search);
        locationCardView = (CardView)findViewById(R.id.location_cv_av_search);

        peopleTextView = (TextView)findViewById(R.id.people_tv_av_search);
        dateTextView = (TextView)findViewById(R.id.date_tv_av_search);

        cancelButton.setOnClickListener(this);
        peopleCardView.setOnClickListener(this);
        dateCardView.setOnClickListener(this);
        locationCardView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == cancelButton)
            finish();
        else if (view == peopleCardView) {
            NumberPickerDialog dialog = new NumberPickerDialog();

            Bundle bundle = new Bundle(6); // 파라미터는 전달할 데이터 개수
            bundle.putString("title", "인원 수"); // key , value
            bundle.putString("subtitle", "예약하려는 인원을 입력해주세요."); // key , value
            bundle.putInt("maxvalue", 30); // key , value
            bundle.putInt("minvalue", 1); // key , value
            bundle.putInt("step", 1); // key , value
            bundle.putInt("defvalue", 1); // key , value
            dialog.setArguments(bundle);
            //class 자신을 Listener로 설정한다
            dialog.setValueChangeListener(this);
            dialog.show(this.getFragmentManager(), "number picker");

            Log.d("[*]", "fuck");
        }
        else if (view == dateCardView) {
            datePicker();
        }
        else if (view == locationCardView) {
            Intent intent = new Intent(SearchActivity.this, SearchLocationActivity.class);
            startActivityForResult(intent, ConnectionCodes.REQUEST_SEARCH_LOCATION);
        }
    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
        peopleTextView.setText((numberPicker.getValue() + 1)+ "명");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConnectionCodes.REQUEST_SEARCH_ACTIVITY) {

        }
    }

    private void datePicker(){
        // 현재 날짜를 기준으로 년 월 일 변수 할당
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mYear = year;
                        mMonth = monthOfYear + 1;
                        mDay = dayOfMonth;

                        date_time = mMonth + "월 " + dayOfMonth + "일";

                        // Time Picker 다이어로그 호출
                        timePicker();
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void timePicker(){
        // 현재 시간을 기준으로 시간 및 분 변수 할당
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Time Picker 다이어로그 호출
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        // 각 변수에 시간 및 분을 저장
                        mHour = hourOfDay;
                        mMinute = minute;

                        // dateTextView에 텍스트 지정
                        dateTextView.setText(date_time+" "+hourOfDay + "시 " + minute + "분");

                    }
                }, mHour, 0, false);
        timePickerDialog.show();
    }

    private long convertDateToTimestamp(int year, int month, int day, int hour, int minute) throws ParseException {
        String dateStr = year + "-" + month + "-" + day + " " + hour + ":" + minute;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date date = (Date)dateFormat.parse(dateStr);

        return date != null ? date.getTime() : 0;
    }
}