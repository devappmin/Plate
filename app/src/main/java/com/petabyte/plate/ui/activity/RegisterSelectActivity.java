package com.petabyte.plate.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.petabyte.plate.R;

public class RegisterSelectActivity extends AppCompatActivity {

    private CardView btn_guest, btn_host;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_select);

        btn_guest = findViewById(R.id.card_v_register_guest);
        btn_host = findViewById(R.id.card_v_register_host);

        btn_guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INFO", "guest card view clicked");
                intent = new Intent(RegisterSelectActivity.this, RegisterActivity.class);
                intent.putExtra("member_type", "guest");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        btn_host.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INFO", "host card view clicked");
                intent = new Intent(RegisterSelectActivity.this, RegisterActivity.class);
                intent.putExtra("member_type", "host");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }
}