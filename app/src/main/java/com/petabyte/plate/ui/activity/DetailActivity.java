package com.petabyte.plate.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.petabyte.plate.R;
import com.petabyte.plate.data.FoodStyle;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context;
    private Intent intent;
    private String diningName, uid;

    private LinearLayout parentLayout;
    private LinearLayout dishImageList;
    private LinearLayout dishList;
    private ImageButton cancelButton;
    private TextView diningTitle;
    private TextView diningSubtitle;
    private ChipGroup styleChipGroup;
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

    private DatabaseReference databaseReference, ref_g, ref_h;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        context = this;

        intent = getIntent();
        diningName = intent.getStringExtra("title");
        isChecked = intent.getBooleanExtra("checked", false);

        parentLayout = (LinearLayout)findViewById(R.id.parent_layout_DetailActivity);
        dishImageList = (LinearLayout)findViewById(R.id.linear_layout_dishImage_DetailActivity);
        dishList = (LinearLayout)findViewById(R.id.linear_layout_dishList_DetailActivity);
        cancelButton = (ImageButton)findViewById(R.id.cancel_button_DetailActivity);
        diningTitle = (TextView)findViewById(R.id.dining_title_DetailActivity);
        diningSubtitle = (TextView)findViewById(R.id.dining_subtitle_DetailActivity);
        styleChipGroup = (ChipGroup)findViewById(R.id.chip_group_DetailActivity);
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

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        getDiningImage();
        getDiningInformation();
        getChefInformation();

        cancelButton.setOnClickListener(this);
        purchaseButton.setOnClickListener(this);
        bookmarkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                getDishImageUri(new MyCallback() {//for getting dining UID
                    @Override
                    public void onCallback(ArrayList<String> keyList) {
                        if(isChecked)
                            Snackbar.make(buttonView, "찜 목록에 추가하였습니다.", 3000).show();
                        else
                            Snackbar.make(buttonView, "찜 목록에서 삭제하였습니다.", 3000).show();
                        reviseBookmarkStatus(keyList.get(0), isChecked);//index 0 is dining UID
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v == cancelButton) {
            finish();
        }
        else if(v == purchaseButton) {
            Intent purchase = new Intent(this, PurchaseActivity.class);
            purchase.putExtra("title", diningTitle.getText().toString());
            startActivity(purchase);
        }

    }

    public void getDiningImage() {
        getDishImageUri(new MyCallback() {
            @Override
            public void onCallback(ArrayList<String> keyList) {
                String diningUid = keyList.get(0);
                for(int i = 1; i < keyList.size(); i++) {
                    final String imageName = keyList.get(i);
                    storageReference = FirebaseStorage.getInstance().getReference();
                    storageReference.child("dining").child(diningUid).child(imageName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            ImageView dishImage = new ImageView(context);
                            GradientDrawable drawable = (GradientDrawable) context.getDrawable(R.drawable.image_radius);
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
                    for(int i = 0; i < dataSnapshot.child("style").getChildrenCount(); i++) {
                        FoodStyle style = FoodStyle.valueOf((dataSnapshot.child("style").child(Integer.toString(i + 1)).getValue().toString()));
                        String styleLabel = "#" + style.label;
                        Chip chip = new Chip(styleChipGroup.getContext());
                        chip.setText(styleLabel);
                        chip.setTextColor(Color.parseColor("#FFFFFF"));
                        chip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorPrimary)));
                        styleChipGroup.addView(chip);
                    }
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
                            String rateString = dataSnapshot.child("Profile").child("Rating").getValue().toString();
                            double rate = Double.parseDouble(rateString);
                            Long ratingCount = (Long) dataSnapshot.child("Profile").child("RatingCount").getValue();
                            chefName.setText(dataSnapshot.child("Profile").child("Name").getValue().toString());
                            chefIntroduction.setText("\"" + dataSnapshot.child("Profile").child("Description").getValue().toString() + "\"");
                            rating.setText(String.format("유저 평점\n" + "%.1f" + " / 5.0 (" + ratingCount + "명 참여)", rate));
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
                    ArrayList<String> keyList = new ArrayList<String>();
                    keyList.add(dataSnapshot.getKey());
                    for(int i = 1; i <= dataSnapshot.child("images").getChildrenCount(); i++) {//get dishes from database
                        keyList.add(dataSnapshot.child("images").child(Integer.toString(i)).getValue().toString());
                    }
                    myCallback.onCallback(keyList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void reviseBookmarkStatus(final String diningUid, final boolean isChecked) {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        ref_g = FirebaseDatabase.getInstance().getReference("User").child("Guest");
        ref_h = FirebaseDatabase.getInstance().getReference("User").child("Host");
        uid = user.getUid();
        ref_g.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    String userUid = dataSnapshot.getKey();
                    boolean isRemoved = false;
                    if(userUid.equals(uid)){
                        if(isChecked) {//if not checked before listener runs, add to bookmark
                            Long size = dataSnapshot.child("Bookmark").getChildrenCount();
                            ref_g.child(userUid).child("Bookmark").child(Long.toString(size + 1)).setValue(diningUid);
                        } else {//if checked before listener runs, remove from bookmark
                            for (int i = 1; i <= dataSnapshot.child("Bookmark").getChildrenCount(); i++) {
                                try {
                                    if (dataSnapshot.child("Bookmark").child(Integer.toString(i)).getValue().toString().equals(diningUid)) {
                                        dataSnapshot.child("Bookmark").child(Integer.toString(i)).getRef().removeValue();
                                        isRemoved = true;
                                    }
                                } catch (NullPointerException e) {
                                    if (dataSnapshot.child("Bookmark").child(Integer.toString(i + 1)).getValue().toString().equals(diningUid)) {
                                        dataSnapshot.child("Bookmark").child(Integer.toString(i + 1)).getRef().removeValue();
                                        isRemoved = true;
                                    }
                                }
                                if(isRemoved) {
                                    //update afterward value's index
                                    if(i != dataSnapshot.child("Bookmark").getChildrenCount()){
                                        String newDiningUid;
                                        newDiningUid = dataSnapshot.child("Bookmark").child(Integer.toString(i + 1)).getValue().toString();
                                        ref_g.child(userUid).child("Bookmark").child(Integer.toString(i)).setValue(newDiningUid);
                                    } else {//remove last value
                                        try {
                                            dataSnapshot.child("Bookmark").child(Integer.toString(i)).getRef().removeValue();
                                        } catch (NullPointerException e) {
                                            dataSnapshot.child("Bookmark").child(Integer.toString(i + 1)).getRef().removeValue();
                                        }
                                    }
                                }
                            }
                        }
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
                    String userUid = dataSnapshot.getKey();
                    boolean isRemoved = false;
                    if(userUid.equals(uid)){
                        if(isChecked) {//if not checked before listener runs, add to bookmark
                            Long size = dataSnapshot.child("Bookmark").getChildrenCount();
                            ref_h.child(userUid).child("Bookmark").child(Long.toString(size + 1)).setValue(diningUid);
                        } else {//if checked before listener runs, remove from bookmark
                            for (int i = 1; i <= dataSnapshot.child("Bookmark").getChildrenCount(); i++) {
                                try {
                                    if (dataSnapshot.child("Bookmark").child(Integer.toString(i)).getValue().toString().equals(diningUid)) {
                                        dataSnapshot.child("Bookmark").child(Integer.toString(i)).getRef().removeValue();
                                        isRemoved = true;
                                    }
                                } catch (NullPointerException e) {
                                    if (dataSnapshot.child("Bookmark").child(Integer.toString(i + 1)).getValue().toString().equals(diningUid)) {
                                        dataSnapshot.child("Bookmark").child(Integer.toString(i + 1)).getRef().removeValue();
                                        isRemoved = true;
                                    }
                                }
                                if(isRemoved) {
                                    //update afterward value's index
                                    if(i != dataSnapshot.child("Bookmark").getChildrenCount()){
                                        String newDiningUid;
                                        newDiningUid = dataSnapshot.child("Bookmark").child(Integer.toString(i + 1)).getValue().toString();
                                        ref_h.child(userUid).child("Bookmark").child(Integer.toString(i)).setValue(newDiningUid);
                                    } else {//remove last value
                                        try {
                                            dataSnapshot.child("Bookmark").child(Integer.toString(i)).getRef().removeValue();
                                        } catch (NullPointerException e) {
                                            dataSnapshot.child("Bookmark").child(Integer.toString(i + 1)).getRef().removeValue();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
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