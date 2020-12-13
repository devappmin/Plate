package com.petabyte.plate.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.petabyte.plate.R;
import com.petabyte.plate.ui.view.LocationPickerBottomSheet;
import com.petabyte.plate.ui.view.NumberPickerBottomSheet;
import com.petabyte.plate.ui.view.RecommendChipGroup;
import com.petabyte.plate.utils.LogTags;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener, NumberPickerBottomSheet.NumberPickerSelectedListener, LocationPickerBottomSheet.LocationPickerSelectedListener {

    private RecommendChipGroup chipGroup;

    private ImageButton cancelButton;
    private CardView    peopleCardView;
    private CardView    dateCardView;
    private CardView    locationCardView;
    private EditText    searchEditText;
    private TextView    peopleTextView;
    private TextView    dateTextView;
    private TextView    locationTextView;
    private Button      submitButton;

    private String      location;
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
        searchEditText = (EditText)findViewById(R.id.search_et_av_search);
        peopleTextView = (TextView)findViewById(R.id.people_tv_av_search);
        dateTextView = (TextView)findViewById(R.id.date_tv_av_search);
        locationTextView = (TextView)findViewById(R.id.location_tv_av_search);

        chipGroup = (RecommendChipGroup)findViewById(R.id.recommend_chip_av_search);

        cancelButton.setOnClickListener(this);
        peopleCardView.setOnClickListener(this);
        dateCardView.setOnClickListener(this);
        locationCardView.setOnClickListener(this);
        submitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == cancelButton)
            finishAfterTransition();
        else if (view == peopleCardView) {
            NumberPickerBottomSheet bottomSheet = new NumberPickerBottomSheet(1, 50);
            bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
        }
        else if (view == dateCardView) {
            datePicker();
        }
        else if (view == locationCardView) {
            LocationPickerBottomSheet bottomSheet = new LocationPickerBottomSheet();
            bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
        }
        else if (view == submitButton) {
            Intent intent = new Intent();

            // 검색을 하더라도 분 단위가 맞지 않을 경우에는 무시하는 경향이 있기 때문에
            // 분 단위는 삭제를 하고 그 값을 보낸다.
            // 하지만 mMinute가 안 쓰이는 것이 아닌 CardView에서 선택한 값을 보여주기 위해서와
            // 이후에 사용할 수도 있기 때문에 이 변수는 놔두지 않고 남긴다.
            intent.putExtra("timestamp", convertDateToTimestamp(mYear, mMonth, mDay, mHour, 0));

            // 인원 수를 보낸다.
            intent.putExtra("people", people);

            // 음식스타일을 보내는데 String Array 형식으로 보내고
            // ResultFragment에서 그 값을 String Array로 받아 FoodStyle의 리스트로 변환한다.
            intent.putStringArrayListExtra("foodStyles", chipGroup.getSelectedChipsLabel());

            // 위치 정보를 보낸다.
            intent.putExtra("location", location);

            // 검색 값을 보낸다.
            intent.putExtra("search", searchEditText.getText().toString());

            // 해당 인텐트를 입력하여 ResultFragment에 보내고 현재 엑티비티를 끝낸다.
            setResult(RESULT_OK, intent);
            finish();
        }
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
                        dateTextView.setTypeface(null, Typeface.BOLD);
                        dateTextView.setTextColor(getResources().getColor(R.color.colorPrimary));
                    }
                }, 18, 0, false);
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
    private long convertDateToTimestamp(int year, int month, int day, int hour, int minute) {
        // 년도 ~ 분까지 전부 저장하는 문자열 생성
        String dateStr = year + "-" + month + "-" + day + " " + hour + ":" + minute;

        // Dateformat을 만들고 dateStr의 값을 삽입
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = null;
        try {
            date = (Date)dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Timestamp 값을 리턴하는데 date 값에 문제가 있으면 0 리턴
        return date != null ? date.getTime() : -1;
    }

    @Override
    public void onLocationPickerSelected(String address) {
        location = address;

        locationTextView.setText(address);
        locationTextView.setTypeface(null, Typeface.BOLD);
        locationTextView.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    @Override
    public void onNumberPickerSelected(int selectedValue) {
        // numberPicker에서 선택한 값을 people 변수에 저장하고
        // peopleTextView에 해당 값을 입력한다.
        people = selectedValue;
        peopleTextView.setText(people + "명");
        peopleTextView.setTypeface(null, Typeface.BOLD);
        peopleTextView.setTextColor(getResources().getColor(R.color.colorPrimary));
    }
}