package com.petabyte.plate.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.petabyte.plate.R;
import com.petabyte.plate.data.DiningMasterData;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;

public class PurchaseActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CardView cardswitch1, cardswitch2;
    private TextView title, subtitle, timestamp, price, name;
    private Button submitbtn;

    private String reservDUID; // 다이닝uid
    private String reservTitle;
    private String reservSubtitle;
    private long reservTime; // 예약 시작 시간
    private int reservPrice;
    private String reservName; // 예약자 이름

    private DiningMasterData diningMasterData;
    private FirebaseUser user;
    private DatabaseReference ref_h, ref_g;
    private String UID, MEMBER_TYPE; // UID와 멤버 타입

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);

        // appbar 구현
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar_purchase);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);

        // toolbar back button 구현
        toolbar = (Toolbar)findViewById(R.id.toolbar_purchase);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get intent extra
        Intent intent = getIntent();
        reservDUID = intent.getStringExtra("diningUid");
        reservTitle = intent.getStringExtra("title");
        reservTime = intent.getLongExtra("start", 0);

        // get view
        cardswitch1 = (CardView) findViewById(R.id.card_v_purchase_sel1);
        cardswitch2 = (CardView) findViewById(R.id.card_v_purchase_sel2);
        cardswitch1.setBackgroundResource(R.drawable.button_left_radius);
        cardswitch2.setBackgroundResource(R.drawable.button_right_radius);
        title = (TextView) findViewById(R.id.text_v_purchase_title);
        subtitle = (TextView) findViewById(R.id.text_v_purchase_subtitle);
        timestamp = (TextView) findViewById(R.id.text_v_purchase_time);
        price = (TextView) findViewById(R.id.text_v_purchase_price);
        name = (TextView) findViewById(R.id.text_v_purchase_name);
        submitbtn = (Button) findViewById(R.id.button_purchase_submit);

        // set switch action
        cardswitch2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "개발자가 열심히 만들고 있는 기능이에요.", 3000).show();
            }
        });

        // get user info
        getUserInfo();

        // get datas not including in intent extra
        getDiningMasterData();

        // 각 필드에 setText
        title.setText(reservTitle);
        timestamp.setText(convertTimestamp(reservTime));

        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentTime = String.valueOf(System.currentTimeMillis());
                HashMap<Object,String> reservationMap = new HashMap<>();

                reservationMap.put("DiningUID", reservDUID);
                reservationMap.put("Reservation", String.valueOf(reservTime));
                reservationMap.put("Status", "입금대기중");

                DatabaseReference reserv_ref = FirebaseDatabase.getInstance().getReference("User");
                reserv_ref.child(MEMBER_TYPE).child(UID).child("Reservation").child(currentTime).setValue(reservationMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                makeDialog("성공적으로 예약되었어요!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                makeDialog("예약에 실패했어요.\n잠시 후 다시 시도해주세요.");
                                finish();
                            }
                        });

               // Log.d("Result", UID+" / "+MEMBER_TYPE+" / "+reservName);
            }
        });
    }

    // 유저 정보를 가져오고 reference를 세팅
    private void getUserInfo(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        ref_g = FirebaseDatabase.getInstance().getReference("User").child("Guest");
        ref_h = FirebaseDatabase.getInstance().getReference("User").child("Host");
        UID = user.getUid();

        ref_g.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Object g_uid = dataSnapshot.getKey();
                    if(g_uid.equals(UID)){
                        MEMBER_TYPE = "Guest";
                        reservName = (String) dataSnapshot.child("Profile/Name").getValue();
                        name.setText("입금자명 : "+reservName);

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        ref_h.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Object h_uid = dataSnapshot.getKey();
                    if(h_uid.equals(UID)){
                        MEMBER_TYPE = "Host";
                        reservName = (String) dataSnapshot.child("Profile/Name").getValue();
                        name.setText("입금자명 : "+reservName);
                        //Log.d("Result", UID+" / "+MEMBER_TYPE+" / "+reservName);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // 마스터 데이터 가져오는 함수
    private void getDiningMasterData(){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Dining");
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot datasnapshot: snapshot.getChildren()){
                    if(datasnapshot.getKey().equals(reservDUID)){
                        diningMasterData = datasnapshot.getValue(DiningMasterData.class);
                        reservSubtitle = diningMasterData.getSubtitle();
                        reservPrice = diningMasterData.getPrice();
                        subtitle.setText(reservSubtitle);
                        price.setText(String.format(Locale.KOREA, "%,d", reservPrice) + "원");
                        //Log.d("Result", reservSubtitle+" / "+reservPrice);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public void makeDialog(String message){
        final AlertDialog.Builder d = new AlertDialog.Builder(this);
        d.setMessage(message);
        d.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(PurchaseActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        // set btn color
        AlertDialog tmp = d.create();
        tmp.show();
        tmp.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public String convertTimestamp(long time) {
        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        String date = (String) formatter.format(new Timestamp(time));
        return date;
    }
}

