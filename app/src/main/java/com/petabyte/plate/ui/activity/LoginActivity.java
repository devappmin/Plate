package com.petabyte.plate.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.petabyte.plate.R;
import com.petabyte.plate.SplashActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText input_email, input_password;
    private Button btn_login;
    private TextView btn_signup;
    private Intent intent;
    private FirebaseAuth firebaseAuth;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // get firebase access permission
        firebaseAuth = FirebaseAuth.getInstance();

        input_email = findViewById(R.id.input_email);
        input_password = findViewById(R.id.input_password);
        btn_login = findViewById(R.id.btn_login);
        btn_signup = findViewById(R.id.btn_signup);

        // force hide soft keyboard
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        // login btn onclick event
        btn_login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View view) {

                String email = input_email.getText().toString().trim();
                String pw = input_password.getText().toString().trim();

                imm.hideSoftInputFromWindow(input_email.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(input_password.getWindowToken(), 0);

                if(email.getBytes().length <= 0 || pw.getBytes().length <= 0) {
                    Snackbar.make(view, "빈 칸이 있어요!", 2000).show();
                }else{
                    firebaseAuth.signInWithEmailAndPassword(email, pw)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();
                                    }else{
                                        Snackbar.make(view, "이메일이나 비밀번호가 틀렸어요!", 2000).show();
                                    }
                                }
                            });
                }
            }
        });

        // register btn onclick event
        btn_signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                intent = new Intent(LoginActivity.this, RegisterSelectActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}