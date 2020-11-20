package com.petabyte.plate.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.petabyte.plate.R;
import com.petabyte.plate.data.DiningMasterData;
import com.petabyte.plate.ui.fragment.NumberPickerDialog;
import com.petabyte.plate.utils.ConnectionCodes;
import com.petabyte.plate.utils.LogTags;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener, NumberPicker.OnValueChangeListener {

    private ImageButton cancelButton;
    private CardView    peopleCardView;
    private CardView    dateCardView;
    private CardView    locationCardView;
    private TextView    peopleTextView;
    private TextView    dateTextView;
    private Button      submitButton;

    private String      date_time;
    private int         mYear;
    private int         mMonth;
    private int         mDay;
    private int         mHour;
    private int         mMinute;
    private int         people;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        cancelButton = (ImageButton)findViewById(R.id.cancel_ib_av_search);
        peopleCardView = (CardView)findViewById(R.id.people_cv_av_search);
        dateCardView = (CardView)findViewById(R.id.date_cv_av_search);
        locationCardView = (CardView)findViewById(R.id.location_cv_av_search);
        submitButton = (Button)findViewById(R.id.search_btn_av_search);

        peopleTextView = (TextView)findViewById(R.id.people_tv_av_search);
        dateTextView = (TextView)findViewById(R.id.date_tv_av_search);

        cancelButton.setOnClickListener(this);
        peopleCardView.setOnClickListener(this);
        dateCardView.setOnClickListener(this);
        locationCardView.setOnClickListener(this);
        submitButton.setOnClickListener(this);
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
        }
        else if (view == dateCardView) {
            datePicker();
        }
        else if (view == locationCardView) {
            Intent intent = new Intent(SearchActivity.this, SearchLocationActivity.class);
            startActivityForResult(intent, ConnectionCodes.REQUEST_SEARCH_LOCATION);
        }
        else if (view == submitButton) {
            Intent intent = new Intent();
            intent.putExtra("year", mYear);
            intent.putExtra("month", mMonth);
            intent.putExtra("day", mDay);
            intent.putExtra("hour", mHour);
            intent.putExtra("minute", mMinute);
            intent.putExtra("people", people);

            setResult(RESULT_OK, intent);
            finish();
        }
    }

    // NumberPicker를 통해서 값을 입력 받았을 때 호출되는 함수
    @Override
    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
        // numberPicker에서 선택한 값을 people 변수에 저장하고
        // peopleTextView에 해당 값을 입력한다.
        people = numberPicker.getValue() + 1;
        peopleTextView.setText(people + "명");
    }

    /**
     * Datepicker 다이얼로그를 호출하는 함수
     * 날짜를 정한 뒤에는 시간도 정해야한다,
     */
    private void datePicker(){
        // 현재 날짜를 기준으로 년 월 일 변수 할당
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        // DatePickerDialog를 통해 다이얼로그 생성
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // 입력된 년도, 월, 일을 각 변수에 삽입
                        mYear = year;
                        mMonth = monthOfYear + 1;
                        mDay = dayOfMonth;

                        // date_time 문자열에 월과 일을 삽입
                        date_time = mMonth + "월 " + dayOfMonth + "일";

                        // Time Picker 다이얼로그를 호출
                        timePicker();
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    /**
     * Timepicker 다이얼로그를 호출하는 함수
     */
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

                        // date_time 문자열에 시간 포함
                        date_time += " " + mHour + "시";

                        // 0분이 아닐 경우에 date_time 문자열에 분 포함
                        if (minute != 0)
                            date_time += " " + mMinute + "분";

                        // dateTextView에 date_time 값을 삽입
                        dateTextView.setText(date_time);

                    }
                }, mHour, 0, false);
        timePickerDialog.show();
    }

    /**
     * 년, 월, 일, 시간, 분을 입력하면 Timestamp로 변환해주는 함수
     * @param year      년
     * @param month     월
     * @param day       일
     * @param hour      시간
     * @param minute    분
     * @return Timestamp로 변환한 날짜 값
     * @throws ParseException
     */
    private long convertDateToTimestamp(int year, int month, int day, int hour, int minute) throws ParseException {
        // 년도 ~ 분까지 전부 저장하는 문자열 생성
        String dateStr = year + "-" + month + "-" + day + " " + hour + ":" + minute;

        // Dateformat을 만들고 dateStr의 값을 삽입
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date date = (Date)dateFormat.parse(dateStr);

        // Timestamp 값을 리턴하는데 date 값에 문제가 있으면 0 리턴
        return date != null ? date.getTime() : 0;
    }
}