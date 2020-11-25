package com.petabyte.plate.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.petabyte.plate.R;
import com.squareup.picasso.Picasso;

import java.util.Locale;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context;
    private Intent intent;
    private String diningName;

    private LinearLayout linearLayout;
    private ImageButton cancelButton;
    private TextView diningTitle;
    private TextView diningSubtitle;
    private ImageView chefImage;
    private TextView chefName;
    private TextView description;
    private TextView rating;
    private SimpleRatingBar ratingBar;
    private CardView bottomCardview;
    private TextView diningPrice;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        context = this;

        intent = getIntent();
        diningName = intent.getStringExtra("title");

        linearLayout = (LinearLayout)findViewById(R.id.linear_layout_DetailActivity);
        cancelButton = (ImageButton)findViewById(R.id.cancel_button_DetailActivity);
        diningTitle = (TextView)findViewById(R.id.dining_title_DetailActivity);
        diningSubtitle = (TextView)findViewById(R.id.dining_subtitle_DetailActivity);
        chefImage = (ImageView)findViewById(R.id.chef_image_DetailActivity);
        chefName = (TextView)findViewById(R.id.chef_name_DetailActivity);
        description = (TextView)findViewById(R.id.description_DetailActivity);
        rating = (TextView)findViewById(R.id.rating_text_view_DetailActivity);
        ratingBar = (SimpleRatingBar)findViewById(R.id.rating_bar_DetailActivity);
        bottomCardview = (CardView)findViewById(R.id.card_view_bottom_DetailActivity);
        diningPrice = (TextView)findViewById(R.id.price_DetailActivity);

        cancelButton.setOnClickListener(this);

        diningTitle.setText(diningName);

        getDiningInformation();
        getChefInformation();

    }

    @Override
    public void onClick(View v) {
        if(v == cancelButton)
            finish();

    }

    public void getDiningInformation() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Dining");
        databaseReference.orderByChild("title").equalTo(diningName).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
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
                    diningPrice.setText(String.format(Locale.KOREA, "%,d", dataSnapshot.child("price").getValue()) + "원");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getChefInformation() {

        storageReference = FirebaseStorage.getInstance().getReference();

        storageReference.child("chef.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                chefImage.setBackground(new ShapeDrawable((new OvalShape())));
                chefImage.setClipToOutline(true);
                Picasso.get().load(uri).fit().centerCrop().into(chefImage);
            }
        });

        readData(new MyCallback() {//get Chef UID
            @Override
            public void onCallback(String uid) {
                databaseReference = FirebaseDatabase.getInstance().getReference();
                //get Host user Information
                databaseReference.child("User").child("Host").orderByKey().equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshots) {
                        for(DataSnapshot dataSnapshot : snapshots.getChildren()) {
                            double rate = (double) dataSnapshot.child("Profile").child("Rating").getValue();
                            chefName.setText(dataSnapshot.child("Profile").child("Name").getValue().toString());
                            description.setText("\"" + dataSnapshot.child("Profile").child("Description").getValue().toString() + "\"");
                            rating.setText("유저 평점\n" + rate + " / 5.0");
                            ratingBar.setRating(((float) rate));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    public void readData(final MyCallback myCallback) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Dining");
        databaseReference.orderByChild("title").equalTo(diningName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshots) {
                for(DataSnapshot dataSnapshot : snapshots.getChildren()){
                    String uid = dataSnapshot.getKey();
                    myCallback.onCallback(uid.substring(0, 28));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public interface MyCallback {
        void onCallback(String uid);
    }
}