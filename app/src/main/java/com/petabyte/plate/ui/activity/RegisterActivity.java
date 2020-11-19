package com.petabyte.plate.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.petabyte.plate.R;

public class RegisterActivity extends AppCompatActivity {

    private TextView label_intro;
    private EditText input_name, input_email, input_pw, input_pw_v, input_intro;
    private Button btn_submit;
    private Intent intent;
    private FirebaseAuth firebaseAuth;
    private String member_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        intent = getIntent();
        member_type = intent.getStringExtra("MEMBER_TYPE");

        // get firebase access permission
        //firebaseAuth = FirebaseAuth.getInstance();

        input_name = findViewById(R.id.input_name);
        input_email = findViewById(R.id.input_email);
        input_pw = findViewById(R.id.input_pw);
        input_pw_v = findViewById(R.id.input_pw_verify);
        input_intro = findViewById(R.id.input_host_intro);
        label_intro = findViewById(R.id.label_host_intro);
        btn_submit = findViewById(R.id.btn_submit);

        if(member_type.equals("HOST")){
            Log.d("ji1dev", "HOST");
        }else{
            Log.d("ji1dev", "GUEST");
            label_intro.setVisibility(View.GONE);
            input_intro.setVisibility(View.GONE);

        }


    }
}