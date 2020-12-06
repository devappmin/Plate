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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.petabyte.plate.R;
import com.petabyte.plate.data.BookmarkCardViewData;
import com.petabyte.plate.ui.activity.DetailActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
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

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView diningTitle;
        private TextView diningSubtitle;
        private TextView diningDate;
        private TextView diningLocation;
        private TextView diningDetailLocation;
        private ImageView diningImage;
        private CheckBox checkBox;
        private DatabaseReference databaseReference, ref_g, ref_h;
        private FirebaseAuth mAuth;
        private FirebaseUser user;
        private Context context;

        private String uid;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            context = itemView.getContext();

            databaseReference = FirebaseDatabase.getInstance().getReference();
            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();
            diningTitle = (TextView) itemView.findViewById(R.id.dining_title_bookmarkcard);
            diningSubtitle = (TextView) itemView.findViewById(R.id.dining_subtitle_bookmarkcard);
            diningDate = (TextView) itemView.findViewById(R.id.dining_date_bookmarkcard);
            diningLocation = (TextView) itemView.findViewById(R.id.dining_location_bookmarkcard);
            diningDetailLocation = (TextView) itemView.findViewById(R.id.dining_detail_location_bookmarkcard);
            diningImage = (ImageView) itemView.findViewById(R.id.dining_image_bookmarkcard);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox_bookmarkcard);
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
            Picasso.get().load(("https://firebasestorage.googleapis.com/v0/b/plate-f5144.appspot.com/o/chef.png?alt=media&token=8e789939-497e-4b18-95f4-e526f50e7917")).fit().centerCrop().into(diningImage);

            diningTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), DetailActivity.class);
                    intent.putExtra("title", diningTitle.getText().toString());
                    intent.putExtra("checked", checkBox.isChecked());
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

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                    getDiningUid(data.getDiningTitle(), new MyCallback() {
                        @Override
                        public void onCallback(String diningUid) {
                            reviseBookmarkStatus(diningUid, isChecked);
                        }
                    });
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

        public void getDiningUid(String diningTitle, final MyCallback myCallback){
            databaseReference = FirebaseDatabase.getInstance().getReference("Dining");
            databaseReference.orderByChild("title").equalTo(diningTitle).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        myCallback.onCallback(dataSnapshot.getKey());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }

        public interface MyCallback {
            void onCallback(String diningUid);
        }
    }
}
