package com.petabyte.plate.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.L;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.petabyte.plate.R;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener{

    private Context context;
    private Intent intent;
    private String diningName, diningSubName;

    private LinearLayout linearLayout;
    private ImageButton cancelButton;
    private TextView diningTitle;
    private TextView diningSubtitle;

    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        context = this;
        intent = getIntent();
        diningName = intent.getStringExtra("title");

        linearLayout = (LinearLayout)findViewById(R.id.linear_layout_DetailActivity);
        diningTitle = (TextView)findViewById(R.id.dining_title_DetailActivity);
        diningSubtitle = (TextView)findViewById(R.id.dining_subtitle_DetailActivity);
        cancelButton = (ImageButton)findViewById(R.id.cancel_button_DetailActivity);

        cancelButton.setOnClickListener(this);

        databaseReference = FirebaseDatabase.getInstance().getReference("Dining");
        databaseReference.orderByChild("title").equalTo(diningName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshots) {
                for(DataSnapshot dataSnapshot : snapshots.getChildren()){
                    diningSubtitle.setText(dataSnapshot.child("subtitle").getValue().toString());//set subtitle from database
                    for(int i = 1; i <= dataSnapshot.child("dishes").getChildrenCount(); i++) {//get dishes from database
                        TextView textView = new TextView(context);
                        textView.setText(dataSnapshot.child("dishes").child(Integer.toString(i)).getValue().toString());
                        textView.setGravity(Gravity.CENTER);
                        float scale = getResources().getDisplayMetrics().density;
                        if(i != dataSnapshot.child("dishes").getChildrenCount())
                            textView.setPadding(0,0,0, (int)(8 * scale + 0.5f));
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                        textView.setTextColor(getResources().getColor(R.color.textDarkPrimary));
                        linearLayout.addView(textView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        diningTitle.setText(diningName);
    }

    @Override
    public void onClick(View v) {
        if(v == cancelButton)
            finish();
    }
}