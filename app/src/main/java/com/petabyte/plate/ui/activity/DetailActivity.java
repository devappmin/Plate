package com.petabyte.plate.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.L;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import java.util.ArrayList;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context;
    private Intent intent;
    private String diningName;

    private LinearLayout dishImageList;
    private LinearLayout dishList;
    private ImageButton cancelButton;
    private TextView diningTitle;
    private TextView diningSubtitle;
    private TextView diningDescription;
    private ImageView chefImage;
    private TextView chefName;
    private TextView chefIntroduction;
    private TextView rating;
    private SimpleRatingBar ratingBar;
    private TextView bookmarked;
    private TextView diningPrice;
    private Button purchaseButton;
    private CheckBox bookmarkBox;
    private boolean isChecked;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        context = this;

        intent = getIntent();
        diningName = intent.getStringExtra("title");
        isChecked = intent.getBooleanExtra("checked", false);

        mAuth = FirebaseAuth.getInstance();

        dishImageList = (LinearLayout)findViewById(R.id.linear_layout_dishImage_DetailActivity);
        dishList = (LinearLayout)findViewById(R.id.linear_layout_dishList_DetailActivity);
        cancelButton = (ImageButton)findViewById(R.id.cancel_button_DetailActivity);
        diningTitle = (TextView)findViewById(R.id.dining_title_DetailActivity);
        diningSubtitle = (TextView)findViewById(R.id.dining_subtitle_DetailActivity);
        diningDescription = (TextView)findViewById(R.id.dining_description_DetailActivity);
        chefImage = (ImageView)findViewById(R.id.chef_image_DetailActivity);
        chefName = (TextView)findViewById(R.id.chef_name_DetailActivity);
        chefIntroduction = (TextView)findViewById(R.id.chef_introduction_DetailActivity);
        rating = (TextView)findViewById(R.id.rating_text_view_DetailActivity);
        ratingBar = (SimpleRatingBar)findViewById(R.id.rating_bar_DetailActivity);
        bookmarked = (TextView)findViewById(R.id.number_of_bookmark_DetailActivity);
        diningPrice = (TextView)findViewById(R.id.price_DetailActivity);
        purchaseButton = (Button)findViewById(R.id.purchase_button_DetailActivity);
        bookmarkBox = (CheckBox)findViewById(R.id.checkbox_DetailActivity);

        diningTitle.setText(diningName);
        bookmarkBox.setChecked(isChecked);

        cancelButton.setOnClickListener(this);
        purchaseButton.setOnClickListener(this);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            getDiningImage();
            getDiningInformation();
            getChefInformation();
        } else {
            signInAnonymously();
        }


    }

    @Override
    public void onClick(View v) {
        if(v == cancelButton)
            finish();
        else if(v == purchaseButton) {
            Intent purchase = new Intent(this, PurchaseActivity.class);
            purchase.putExtra("title", diningTitle.getText().toString());
            startActivity(purchase);
        }

    }

    private void signInAnonymously() {
        mAuth.signInAnonymously().addOnSuccessListener(this, new  OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                getDiningImage();
                getDiningInformation();
                getChefInformation();
            }
        });
    }

    public void getDiningImage() {
        getDishImageUri(new MyCallback() {
            @Override
            public void onCallback(ArrayList<String> keyList) {
                String diningUID = keyList.get(0);
                String dish = keyList.get(1);
                Log.d("음식", dish);
                storageReference = FirebaseStorage.getInstance().getReference();
                storageReference.child("dining").child(diningUID).child(dish + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        ImageView dishImage = new ImageView(context);
                        GradientDrawable drawable = (GradientDrawable)context.getDrawable(R.drawable.image_radius);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, toDp(250));
                        layoutParams.gravity = Gravity.CENTER;
                        layoutParams.bottomMargin = toDp(15);
                        dishImage.setBackground(drawable);
                        dishImage.setClipToOutline(true);
                        dishImage.setLayoutParams(layoutParams);
                        Picasso.get().load(uri).fit().centerCrop().into(dishImage);
                        dishImageList.addView(dishImage);
                    }
                });
            }
        });
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
                        String dish = dataSnapshot.child("dishes").child(Integer.toString(i)).getValue().toString();
                        TextView textView = new TextView(context);
                        textView.setText("- " + dish);
                        if(i != dataSnapshot.child("dishes").getChildrenCount())
                            textView.setPadding(0,0,0, toDp(3));
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                        textView.setTextColor(getResources().getColor(R.color.textDarkPrimary));
                        dishList.addView(textView);
                    }
                    bookmarked.setText(dataSnapshot.child("bookmark").getValue() + "명이 좋아합니다.");
                    diningDescription.setText("\"" + dataSnapshot.child("description").getValue().toString() + "\"");
                    diningPrice.setText(String.format(Locale.KOREA, "%,d", dataSnapshot.child("price").getValue()) + "원");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getChefInformation() {
        readChefData(new MyCallback() {//get Chef UID
            @Override
            public void onCallback(ArrayList<String> keyList) {
                String uid = keyList.get(0).substring(0, 28);
                storageReference = FirebaseStorage.getInstance().getReference();
                storageReference.child("user").child("host").child(uid + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        chefImage.setBackground(new ShapeDrawable((new OvalShape())));
                        chefImage.setClipToOutline(true);
                        Picasso.get().load(uri).fit().centerCrop().into(chefImage);
                    }
                });

                databaseReference = FirebaseDatabase.getInstance().getReference();
                //get Host user Information
                databaseReference.child("User").child("Host").orderByKey().equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshots) {
                        for(DataSnapshot dataSnapshot : snapshots.getChildren()) {
                            double rate = (double) dataSnapshot.child("Profile").child("Rating").getValue();
                            chefName.setText(dataSnapshot.child("Profile").child("Name").getValue().toString());
                            chefIntroduction.setText("\"" + dataSnapshot.child("Profile").child("Description").getValue().toString() + "\"");
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

    //get chef UID
    public void readChefData(final MyCallback myCallback) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Dining");
        databaseReference.orderByChild("title").equalTo(diningName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshots) {
                for(DataSnapshot dataSnapshot : snapshots.getChildren()){
                    ArrayList<String> keyList = new ArrayList<String>();
                    keyList.add(dataSnapshot.getKey());
                    myCallback.onCallback(keyList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //get Dish Uri
    public void getDishImageUri(final MyCallback myCallback) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Dining");
        databaseReference.orderByChild("title").equalTo(diningName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshots) {
                for(DataSnapshot dataSnapshot : snapshots.getChildren()){
                    for(int i = 1; i <= dataSnapshot.child("dishes").getChildrenCount(); i++) {//get dishes from database
                        ArrayList<String> keyList = new ArrayList<String>();
                        keyList.add(dataSnapshot.getKey());
                        keyList.add(dataSnapshot.child("dishes").child(Integer.toString(i)).getValue().toString());
                        myCallback.onCallback(keyList);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public int toDp(int size) {
        float scale = getResources().getDisplayMetrics().density;
        return (int)(size * scale + 0.5f);
    }

    public interface MyCallback {
        //keyList[0] = uid
        //keyList[1] = dish
        void onCallback(ArrayList<String> keyList);
    }
}