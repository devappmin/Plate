package com.petabyte.plate.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.petabyte.plate.R;

import java.util.HashMap;

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
        firebaseAuth = FirebaseAuth.getInstance();

        input_name = findViewById(R.id.input_name);
        input_email = findViewById(R.id.input_email);
        input_pw = findViewById(R.id.input_pw);
        input_pw_v = findViewById(R.id.input_pw_verify);
        //input_intro = findViewById(R.id.input_host_intro);
        //label_intro = findViewById(R.id.label_host_intro);

        btn_submit = findViewById(R.id.btn_submit);

        if(member_type.equals("HOST")){
            btn_submit.setText("Host "+btn_submit.getText().toString());
        }else{
            btn_submit.setText("Guest "+btn_submit.getText().toString());
        }

        /*
        if(member_type.equals("GUEST")){
            label_intro.setVisibility(View.GONE);
            input_intro.setVisibility(View.GONE);
            Log.d("ji1dev", "intro view gone");
        }*/

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                final String name = input_name.getText().toString().trim();
                final String email = input_email.getText().toString().trim();
                final String pw = input_pw.getText().toString().trim();
                final String pw_v = input_pw_v.getText().toString().trim();

                // if there are blank field exist
                if(name.getBytes().length <= 0 || email.getBytes().length <= 0
                        || pw.getBytes().length <= 0 || pw_v.getBytes().length <= 0){
                    Snackbar.make(v, "빈 칸이 있어요!", 3000).show();
                    return;
                }else{
                    if(pw.equals(pw_v)){
                        if(pw.getBytes().length < 6){
                            Snackbar.make(v, "비밀번호는 최소 여섯자리로 정해주세요!", 3000).show();
                            return;
                        }
                        Snackbar.make(v, "회원정보를 등록하는 중이에요", 3000).show();

                        firebaseAuth.createUserWithEmailAndPassword(email, pw)
                                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        // Firebase Auth register success
                                        if(task.isSuccessful()){
                                            FirebaseUser user = firebaseAuth.getCurrentUser();
                                            String email = user.getEmail();
                                            String uid = user.getUid();
                                            String type;

                                            // insert new record(hash map type) in Firebase DB
                                            HashMap<Object,String> map = new HashMap<>();

                                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                                            DatabaseReference reference = database.getReference("User");

                                            //intent = new Intent(RegisterActivity.this, RegisterInfoActivity.class);

                                            if(member_type.equals("HOST")){
                                                type = "Host";
                                                //intent.putExtra("MEMBER_TYPE", "HOST");
                                                reference.child(type).child(uid).child("Status").setValue("심사중");
                                            }else{
                                                type = "Guest";
                                                //intent.putExtra("MEMBER_TYPE", "GUEST");
                                            }

                                            map.put("Email", email);
                                            map.put("Name", name);
                                            //map.put("Description", intro);

                                            reference.child(type).child(uid).child("Profile").setValue(map);

                                            // Firebase DB insert success
                                            // go to login activity
                                            Snackbar.make(v, "회원가입이 완료되었어요!", 3000).show();

                                            /*
                                            startActivity(intent);
                                            finish();
                                            */

                                            finish();

                                        }else{
                                           Log.d("ji1dev", String.valueOf(task.getException()));
                                            Snackbar.make(v, "다른 이메일로 시도해주세요", 3000).show();
                                            return;
                                        }
                                    }
                                });
                    }else{
                        // if pw and pw_verify is different
                        Snackbar.make(v, "비밀번호를 다시 확인해주세요!", 3000).show();
                        return;
                    }
                }
            }
        });
    }
}