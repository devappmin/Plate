package com.petabyte.plate.ui.view;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.petabyte.plate.R;
import com.petabyte.plate.adapter.BookmarkVerticalListAdapter;
import com.petabyte.plate.data.BookmarkCardViewData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class BookmarkVerticalList extends ConstraintLayout {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private BookmarkVerticalListAdapter recyclerAdapter;
    private DatabaseReference databaseReference, ref_g, ref_h;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String uid;

    public BookmarkVerticalList(@NonNull Context context) {
        super(context);
        init();
    }

    public BookmarkVerticalList(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BookmarkVerticalList(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public BookmarkVerticalList(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_bookmarkverticallist, this);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        recyclerInitial();
        getDiningList();

    }

    private void recyclerInitial() {
        recyclerView = (RecyclerView)this.findViewById(R.id.recycler_view_v_bookmarkvertical);

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerAdapter = new BookmarkVerticalListAdapter();
        recyclerView.setAdapter(recyclerAdapter);
    }

    //get dining list
    public void getDiningList() {
        getDiningUid(new MyCallback() {
            @Override
            public void onCallback(final ArrayList<String> diningUid) {
                databaseReference = FirebaseDatabase.getInstance().getReference("Dining");
                databaseReference.orderByChild("start").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            for(int i = 0; i < diningUid.size(); i++) {
                                if(diningUid.get(i).equals(dataSnapshot.getKey())) {
                                    getDiningData(dataSnapshot);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });
    }

    //get Dining UIDs with myCallback
    private void getDiningUid(final MyCallback myCallback) {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        ref_g = FirebaseDatabase.getInstance().getReference("User").child("Guest");
        ref_h = FirebaseDatabase.getInstance().getReference("User").child("Host");
        uid = user.getUid();
        Log.d("uid", uid);
        ref_g.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    String g_uid = dataSnapshot.getKey();
                    ArrayList<String> diningUid = new ArrayList<String>();
                    if(g_uid.equals(uid)){
                        for (int i = 1; i <= dataSnapshot.child("Bookmark").getChildrenCount(); i++){
                            try {
                                diningUid.add(dataSnapshot.child("Bookmark").child(Integer.toString(i)).getValue().toString());
                            } catch (NullPointerException e) {
                                String newDiningUid;
                                newDiningUid = dataSnapshot.child("Bookmark").child(Integer.toString(i + 1)).getValue().toString();
                                ref_g.child(g_uid).child("Bookmark").child(Integer.toString(i)).setValue(newDiningUid);
                                diningUid.add(newDiningUid);
                                dataSnapshot.child("Bookmark").child(Integer.toString(i + 1)).getRef().removeValue();
                            }
                        }
                        myCallback.onCallback(diningUid);
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
                    String h_uid = dataSnapshot.getKey();
                    ArrayList<String> diningUid = new ArrayList<String>();
                    if(h_uid.equals(uid)){
                        for (int i = 1; i <= dataSnapshot.child("Bookmark").getChildrenCount(); i++){
                            try {
                                diningUid.add(dataSnapshot.child("Bookmark").child(Integer.toString(i)).getValue().toString());
                            } catch (NullPointerException e) {
                                String newDiningUid;
                                newDiningUid = dataSnapshot.child("Bookmark").child(Integer.toString(i + 1)).getValue().toString();
                                ref_h.child(h_uid).child("Bookmark").child(Integer.toString(i)).setValue(newDiningUid);
                                diningUid.add(newDiningUid);
                                dataSnapshot.child("Bookmark").child(Integer.toString(i + 1)).getRef().removeValue();
                            }
                        }
                        myCallback.onCallback(diningUid);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    //get dining Data
    public void getDiningData (DataSnapshot snapshot) {
        String diningTitle = snapshot.child("title").getValue().toString();
        String diningSubtitle = snapshot.child("subtitle").getValue().toString();
        long diningTimestamp = (long) snapshot.child("schedules").child("RANDOMKEY").child("start").getValue();
        String  diningDate = getDate(diningTimestamp);//get Date by timestamp
        String diningLocation = snapshot.child("location").child("location").getValue().toString();
        String diningDetailLocation = snapshot.child("location").child("detail").getValue().toString();
        String imageUri = snapshot.child("images").child("2").getValue().toString();

        BookmarkCardViewData data = new BookmarkCardViewData(diningTitle, diningSubtitle, diningDate, diningLocation, diningDetailLocation, imageUri);

        recyclerAdapter.addItem(data);
        recyclerAdapter.notifyDataSetChanged();
    }

    private String getDate(long timeStamp) {
        Calendar cal = Calendar.getInstance(Locale.KOREA);
        cal.setTimeInMillis(timeStamp);
        String date = DateFormat.format("yyyy-MM-dd EEEE", cal).toString();
        return date;
    }

    public interface MyCallback {
        //diningUIDs
        void onCallback(ArrayList<String> diningUid);
    }

}