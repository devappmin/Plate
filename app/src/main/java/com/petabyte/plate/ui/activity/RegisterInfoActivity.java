package com.petabyte.plate.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.petabyte.plate.R;

public class RegisterInfoActivity extends AppCompatActivity {

    private Intent intent;
    private String member_type;
    private String name;

    private LinearLayout host_welcome;
    private TextView welcome_text;
    private Button btn_start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_info);

        intent = getIntent();
        Bundle bundleData = intent.getBundleExtra("REGISTER_DATA");

        member_type = bundleData.getString("MEMBER_TYPE");
        name = bundleData.getString("NAME");

        host_welcome = findViewById(R.id.layout_register_host_welcome);
        welcome_text = findViewById(R.id.text_v_register_welcome);
        btn_start = findViewById(R.id.btn_start);

        if(member_type.equals("GUEST")){
            host_welcome.setVisibility(View.GONE);
        }

        String welcome_msg = "수많은 다이닝이 "+name+"님을 기다리고 있어요!";
        welcome_text.setText(welcome_msg);

        btn_start.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}