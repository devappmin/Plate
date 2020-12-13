package com.petabyte.plate.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.petabyte.plate.R;
import com.petabyte.plate.data.BookmarkCardViewData;
import com.petabyte.plate.data.DiningMasterData;
import com.petabyte.plate.data.UserData;
import com.petabyte.plate.ui.activity.DetailActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BookmarkVerticalListAdapter extends RecyclerView.Adapter<BookmarkVerticalListAdapter.ViewHolder>{
    private List<BookmarkCardViewData> datas = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_bookmarkcard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBind(datas.get(position));
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public void addItem(BookmarkCardViewData data) {
        datas.add(data);
    }

    public void removeItem(BookmarkCardViewData data) { datas.remove(data); }

    public void removeAllItem() { datas = new ArrayList<>(); }

    class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private TextView diningTitle;
        private TextView diningSubtitle;
        private TextView diningDate;
        private TextView diningLocation;
        private TextView diningDetailLocation;
        private ImageView diningImage;
        private CheckBox checkBox;

        private DatabaseReference databaseReference, ref_g, ref_h;
        private StorageReference storageReference;
        private FirebaseAuth mAuth;
        private FirebaseUser user;

        private Context context;

        private HashMap<String, UserData> userDataMap =  new HashMap<>();

        private String uid;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            context = itemView.getContext();

            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();
            cardView = (CardView) itemView.findViewById(R.id.card_view_bookmarkcard);
            diningTitle = (TextView) itemView.findViewById(R.id.dining_title_bookmarkcard);
            diningSubtitle = (TextView) itemView.findViewById(R.id.dining_subtitle_bookmarkcard);
            diningDate = (TextView) itemView.findViewById(R.id.dining_date_bookmarkcard);
            diningLocation = (TextView) itemView.findViewById(R.id.dining_location_bookmarkcard);
            diningDetailLocation = (TextView) itemView.findViewById(R.id.dining_detail_location_bookmarkcard);
            diningImage = (ImageView) itemView.findViewById(R.id.dining_image_bookmarkcard);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox_bookmarkcard);

            diningTitle.setSelected(true);
        }

        private void onBind(final BookmarkCardViewData data) {
            diningTitle.setText(data.getDiningTitle());
            diningSubtitle.setText(data.getDiningSubtitle());
            diningDate.setText(data.getDiningDate());
            diningLocation.setText(data.getDiningLocation());
            diningDetailLocation.setText(data.getDiningDetailLocation());
            GradientDrawable drawable = (GradientDrawable)context.getDrawable(R.drawable.image_radius);
            diningImage.setBackground(drawable);
            diningImage.setClipToOutline(true);
            checkBox.setChecked(true);
            storageReference = FirebaseStorage.getInstance().getReference();
            storageReference.child("dining").child(data.getDiningUID()).child(data.getImageName()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).fit().centerCrop().into(diningImage);
                }
            });

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), DetailActivity.class);
                    intent.putExtra("diningUid", data.getDiningUID());
                    v.getContext().startActivity(intent);
                }
            });

            diningLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//주소 textview를 누르면 지도 어플리케이션으로 연결
                    String address = diningLocation.getText().toString().replace(' ', '+');
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q="+address));
                    v.getContext().startActivity(intent);
                }
            });

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "찜 목록에서 삭제하였습니다.", Toast.LENGTH_SHORT).show();
                    databaseReference = FirebaseDatabase.getInstance().getReference("Dining").child(data.getDiningUID());
                    databaseReference.runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                            DiningMasterData data = currentData.getValue(DiningMasterData.class);
                            if(data == null)
                                return Transaction.success(currentData);
                            data.setBookmark(data.getBookmark() - 1);
                            currentData.setValue(data);
                            return Transaction.success(currentData);
                        }

                        @Override
                        public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

                        }
                    });
                    reviseBookmarkStatus(data.getDiningUID(), false);
                    removeItem(data);
                    notifyItemRemoved(getAdapterPosition());
                }
            });
        }

        public void reviseBookmarkStatus (final String diningUid, final boolean isChecked) {
            getUserData();
            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();
            ref_g = FirebaseDatabase.getInstance().getReference("User").child("Guest");
            ref_h = FirebaseDatabase.getInstance().getReference("User").child("Host");
            uid = user.getUid();

            ref_g.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String userUid = dataSnapshot.getKey();
                        if(userUid.equals(uid)){
                            if(isChecked) {//if not checked before listener runs, add to bookmark
                                if(userDataMap.get(uid).getBookmark() != null) {
                                    if (!(userDataMap.get(uid).getBookmark().values().contains(diningUid))) {
                                        ref_g.child(uid).child("Bookmark").push().setValue(diningUid);
                                    }
                                } else
                                    ref_g.child(uid).child("Bookmark").push().setValue(diningUid);
                            } else {//if checked before listener runs, remove from bookmark
                                if((userDataMap.get(uid).getBookmark().values().contains(diningUid))) {
                                    userDataMap.get(uid).getBookmark().remove(diningUid);
                                    ref_g.child(uid).child("Bookmark").setValue(userDataMap.get(uid).getBookmark());
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            ref_h.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String userUid = dataSnapshot.getKey();
                        if(userUid.equals(uid)){
                            if(isChecked) {//if not checked before listener runs, add to bookmark
                                if(userDataMap.get(uid).getBookmark() != null) {
                                    if (!(userDataMap.get(uid).getBookmark().values().contains(diningUid))) {
                                        ref_h.child(uid).child("Bookmark").push().setValue(diningUid);
                                    }
                                } else
                                    ref_h.child(uid).child("Bookmark").push().setValue(diningUid);
                            } else {//if checked before listener runs, remove from bookmark
                                if((userDataMap.get(uid).getBookmark().values().contains(diningUid))) {
                                    userDataMap.get(uid).getBookmark().values().remove(diningUid);
                                    ref_h.child(uid).child("Bookmark").setValue(userDataMap.get(uid).getBookmark());
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        private void getUserData(){
            ref_g = FirebaseDatabase.getInstance().getReference("User").child("Guest");
            ref_h = FirebaseDatabase.getInstance().getReference("User").child("Host");

            ref_g.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        userDataMap.put(dataSnapshot.getKey(), dataSnapshot.getValue(UserData.class));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            ref_h.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        userDataMap.put(dataSnapshot.getKey(), dataSnapshot.getValue(UserData.class));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}
