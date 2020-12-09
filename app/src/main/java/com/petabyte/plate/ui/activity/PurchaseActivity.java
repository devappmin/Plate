package com.petabyte.plate.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.petabyte.plate.R;

public class PurchaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);

        Intent intent = getIntent();
        TextView textView = (TextView)findViewById(R.id.price_purchase);
        textView.setText(Long.toString(intent.getLongExtra("start", 0)));
    }
}