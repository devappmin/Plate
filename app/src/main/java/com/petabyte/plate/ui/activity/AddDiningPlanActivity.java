package com.petabyte.plate.ui.activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.petabyte.plate.R;
import com.petabyte.plate.data.FoodStyle;
import com.petabyte.plate.ui.view.AddDishImageHorizontalList;
import com.petabyte.plate.utils.ConnectionCodes;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddDiningPlanActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context;

    private ImageButton cancelButton;
    private EditText title_editText;
    private EditText subtitle_editText;
    private EditText description_editText;
    private EditText style_editText;
    private EditText location_editText;
    private EditText detailLocation_editText;
    private ChipGroup styles_chipGroup;
    private EditText date_editText;
    private EditText startTime_editText;
    private EditText endTime_editText;
    private ImageView add_time;
    private ChipGroup times_chipGroup;
    private EditText maxPerson_editText;
    private EditText price_editText;
    private EditText dish_editText;
    private ChipGroup dishes_chipGroup;
    private LinearLayout dishImageList;
    private ImageView addDishImage;
    private Button submitButton;

    private ArrayList<String> selectedStyles = new ArrayList<>();
    private ArrayList<String> styles = new ArrayList<>();
    private ArrayList<String> times = new ArrayList<>();
    private ArrayList<String> dishes = new ArrayList<>();
    private ArrayList<Bitmap> dishImageBitmaps = new ArrayList<>();
    private ArrayList<String> imageNames = new ArrayList<>();

    private int mYear, mMonth, mDay, mHour, mMinute;
    private String timestamp, uid;
    private Timestamp ts;

    private DatabaseReference diningReference, userReference;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dining_plan);
        context = this;

        cancelButton = (ImageButton)findViewById(R.id.cancel_button_addPlan);
        title_editText = (EditText)findViewById(R.id.editText_title_addPlan);
        subtitle_editText = (EditText)findViewById(R.id.editText_subtitle_addPlan);
        description_editText = (EditText)findViewById(R.id.editText_description_addPlan);
        style_editText = (EditText)findViewById(R.id.editText_style_addPlan);
        location_editText = (EditText)findViewById(R.id.editText_location_addPlan);
        detailLocation_editText = (EditText)findViewById(R.id.editText_detail_location_addPlan);
        styles_chipGroup = (ChipGroup)findViewById(R.id.chip_group_styles_addPlan);
        date_editText = (EditText)findViewById(R.id.editText_date_addPlan);
        startTime_editText = (EditText)findViewById(R.id.editText_start_time_addPlan);
        endTime_editText = (EditText)findViewById(R.id.editText_end_time_addPlan);
        add_time = (ImageView)findViewById(R.id.add_time_addPlan);
        times_chipGroup = (ChipGroup)findViewById(R.id.chip_group_times_addPlan);
        maxPerson_editText = (EditText)findViewById(R.id.editText_max_person_addPlan);
        price_editText = (EditText)findViewById(R.id.editText_price_addPlan);
        dish_editText = (EditText)findViewById(R.id.editText_dish_addPlan);
        dishes_chipGroup = (ChipGroup)findViewById(R.id.chip_group_dishes_addPlan);
        dishImageList = (LinearLayout)findViewById(R.id.linear_layout_dishImage_addPlan);
        addDishImage = (ImageView)findViewById(R.id.add_dish_image_view_addPlan);
        submitButton = (Button)findViewById(R.id.submit_button_addPlan);

        cancelButton.setOnClickListener(this);
        style_editText.setOnClickListener(this);
        location_editText.setOnClickListener(this);
        date_editText.setOnClickListener(this);
        startTime_editText.setOnClickListener(this);
        endTime_editText.setOnClickListener(this);
        add_time.setOnClickListener(this);
        addDishImage.setOnClickListener(this);
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
                        dish_editText.getText().clear();
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
        } else if (v == location_editText) {
            Intent i = new Intent(AddDiningPlanActivity.this, SearchAddressActivity.class);
            startActivityForResult(i, ConnectionCodes.REQUEST_SEARCH_ADDRESS);
        } else if (v == style_editText) {
            final String[] items = styles.toArray(new String[styles.size()]);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("스타일을 선택해 주세요.");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final String selectedItem = items[which];
                    final String foodStyle = FoodStyle.getFoodStyle(selectedItem.replace("#", "")).name();
                    //chip의 중복 추가를 방지하기 위해 selectedStyles로 관리
                    boolean containsItem = false;
                    for (String item : selectedStyles) {
                        if (item.contains(foodStyle))
                            containsItem = true;
                    }
                    if (!containsItem) {
                        selectedStyles.add(foodStyle);
                        style_editText.setText(selectedItem);
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
                                selectedStyles.remove(foodStyle);
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
                    //times 배열에 item을 16자리 숫자로 저장(앞 8자리 날짜, 중간 4자리 시작 시간, 끝 4자리 종료 시간)
                    final String timeNumber = Integer.parseInt(date.replace("/", "")) + 20000000//2자릿수 연도를 4자리로 바꿈 ex)20 -> 2020
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
                                times.remove(timeNumber);
                                if(times.size() == 0)
                                    date_editText.setEnabled(true);
                            }
                        });
                        times_chipGroup.addView(chip);
                        date_editText.setEnabled(false);
                    } else {
                        Snackbar.make(v, "이미 입력한 시간이에요.", 3000).show();
                    }
                }
            }
        } else if (v == addDishImage) {
            PermissionListener permissionlistener = new PermissionListener(){
                @Override
                public void onPermissionGranted() {
                    startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI).setType("image/*"), ConnectionCodes.GET_FROM_GALLERY);
                }
                @Override
                public void onPermissionDenied(List<String> deniedPermissions) {
                    Toast.makeText(context, "권한을 거부하였습니다.\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                }
            };
            TedPermission.with(context)
                    .setPermissionListener(permissionlistener)
                    .setRationaleMessage("사진을 업로드하기 위해 권한이 필요합니다.")
                    .setDeniedMessage("사진을 업로드하기 위해 이 권한이 필요합니다.\n[설정] > [권한] 에서 권한을 허용할 수 있어요.")
                    .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .check();
        } else if (v == submitButton) {
            //upload to realtime database
            //항목들이 모두 입력되었는지 검사, 공백이 입력되어도 입력되지 않은 것으로 판단
            if (title_editText.getText().toString().replace(" ", "").equals("")
                    || subtitle_editText.getText().toString().replace(" ", "").equals("")
                    || description_editText.getText().toString().replace(" ", "").equals("")
                    || (styles_chipGroup.getChildCount() == 0)
                    || location_editText.getText().toString().replace(" ", "").equals("")
                    || detailLocation_editText.getText().toString().replace(" ", "").equals("")
                    || (times_chipGroup.getChildCount() == 0)
                    || maxPerson_editText.getText().toString().replace(" ", "").equals("")
                    || price_editText.getText().toString().replace(" ", "").equals("")
                    || (dishes_chipGroup.getChildCount() == 0)
                    || (dishImageBitmaps.size() == 0)) {
                Snackbar.make(v, "모든 항목들을 채워 주세요.", 3000).show();
            } else {
                uploadDiningInformation();
                finish();
            }
        }
    }

    public void uploadDiningInformation () {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        uid = user.getUid();
        ts = new Timestamp(System.currentTimeMillis());
        timestamp = Long.toString(ts.getTime());
        String diningUid = uid + "-" + timestamp;
        diningReference = FirebaseDatabase.getInstance().getReference("Dining").child(diningUid);
        userReference = FirebaseDatabase.getInstance().getReference("User").child("Host").child(uid);
        storageReference = FirebaseStorage.getInstance().getReference();

        userReference.child("MyDining").push().setValue(diningUid);//add MyDining

        //add Dining information
        diningReference.child("bookmark").setValue(0);
        diningReference.child("count").child("current").setValue(0);
        diningReference.child("count").child("max").setValue(Long.parseLong(maxPerson_editText.getText().toString()));
        diningReference.child("description").setValue(description_editText.getText().toString());

        //add dish list to dining database
        for (int i = 0; i < dishes.size(); i++) {
            diningReference.child("dishes").child(Integer.toString(i + 1)).setValue(dishes.get(i));
        }

        //add images to storage and add image names to dining database
        for(int i = 0; i < dishImageBitmaps.size(); i++) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            dishImageBitmaps.get(i).compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = storageReference.child("dining").child(diningUid).child(imageNames.get(i)).putBytes(data);
            diningReference.child("images").child(Integer.toString(i + 1)).setValue(imageNames.get(i));
        }

        diningReference.child("coordinate").child("latitude").setValue(getLatitudeFromAddress(location_editText.getText().toString()));
        diningReference.child("coordinate").child("longitude").setValue(getLongitudeFromAddress(location_editText.getText().toString()));
        diningReference.child("location").child("location").setValue(location_editText.getText().toString());
        diningReference.child("location").child("detail").setValue(detailLocation_editText.getText().toString());
        diningReference.child("price").setValue(Long.parseLong(price_editText.getText().toString()));
        ArrayList<String> plans = timeNumberToDateFormatString(times);
        DatabaseReference scheduleReference = diningReference.child("schedules").push();
        for (int i = 0; i < plans.size(); i++) {
            Long timestamp = Timestamp.valueOf(plans.get(i)).getTime();
            if((i % 2) == 0) {
                scheduleReference = diningReference.child("schedules").push();
                scheduleReference.child("start").setValue(timestamp);
            }
            else
                scheduleReference.child("end").setValue(timestamp);
        }
        for(int i = 0; i < selectedStyles.size(); i++) {
            diningReference.child("style").child(Integer.toString(i + 1)).setValue(selectedStyles.get(i).replace("#", ""));
        }
        diningReference.child("subtitle").setValue(subtitle_editText.getText().toString());
        diningReference.child("title").setValue(title_editText.getText().toString());
        diningReference.child("date").setValue(plans.get(0).substring(0, 10));
    }

    //16자리 숫자의 시간 정보를 yyyy-MM-dd HH:mm:ss 형식으로 변환, 순서대로 두 개씩 짝지어 시작 시간, 종료 시간을 나타냄
    public ArrayList<String> timeNumberToDateFormatString (ArrayList<String> times) {
        ArrayList<String> dateFormat = new ArrayList<>();
        for(int i = 0; i < times.size(); i++) {
            String startString = times.get(i).substring(0, 4)
                    + "-" + times.get(i).substring(4, 6)
                    + "-" + times.get(i).substring(6, 8)
                    + " " + times.get(i).substring(8, 10)
                    + ":" + times.get(i).substring(10, 12)
                    + ":00";
            dateFormat.add(startString);
            String endString = times.get(i).substring(0, 4)
                    + "-" + times.get(i).substring(4, 6)
                    + "-" + times.get(i).substring(6, 8)
                    + " " + times.get(i).substring(12, 14)
                    + ":" + times.get(i).substring(14)
                    + ":00";
            dateFormat.add(endString);
        }
        return dateFormat;
    }

    public double getLatitudeFromAddress(String address) {
        Geocoder geocoder = new Geocoder(this);
        List<Address> list = null;
        try {
            list = geocoder.getFromLocationName(address, 10); // 읽을 개수
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (list != null) {
            // 해당되는 주소로 인텐트 날리기
            return list.get(0).getLatitude();
        } else return 0;
    }

    public double getLongitudeFromAddress(String address) {
        Geocoder geocoder = new Geocoder(this);
        List<Address> list = null;
        try {
            list = geocoder.getFromLocationName(address, 10); // 읽을 개수
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (list != null) {
            // 해당되는 주소로 인텐트 날리기
            return list.get(0).getLongitude();
        } else return 0;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case ConnectionCodes.REQUEST_SEARCH_ADDRESS:
                if (resultCode == RESULT_OK) {
                    String data = intent.getExtras().getString("data");
                    if (data != null) {
                        location_editText.setText(data);
                    }
                }
                break;

            case ConnectionCodes.GET_FROM_GALLERY:
                if(resultCode == RESULT_OK) {
                    Uri selectedImage = intent.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };

                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    final String filename = picturePath.substring(picturePath.lastIndexOf("/") + 1);
                    cursor.close();

                    try {
                        final InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                        final Bitmap selectedImageBitmap = BitmapFactory.decodeStream(imageStream);
                        final AddDishImageHorizontalList addDishImageHorizontalList = new AddDishImageHorizontalList(context);
                        addDishImageHorizontalList.setImage(selectedImageBitmap);
                        dishImageBitmaps.add(selectedImageBitmap);
                        imageNames.add(filename);
                        addDishImageHorizontalList.removeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dishImageList.removeView(addDishImageHorizontalList);
                                dishImageBitmaps.remove(selectedImageBitmap);
                                imageNames.remove(filename);
                            }
                        });
                        dishImageList.addView(addDishImageHorizontalList);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }
}