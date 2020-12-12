package com.petabyte.plate.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.petabyte.plate.R;
import com.petabyte.plate.data.DiningManagementCardData;
import com.petabyte.plate.data.UserData;
import com.petabyte.plate.ui.activity.DetailActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DiningManagementAdapter extends RecyclerView.Adapter<DiningManagementAdapter.ViewHolder> {

    private List<DiningManagementCardData> datas = new ArrayList<>();

    private Activity activity;
    private FragmentManager fragmentManager;

    private  int selectedPosition = -1;

    public DiningManagementAdapter(FragmentManager fm, Activity ac) {
        fragmentManager = fm;
        activity = ac;
    }

    @NonNull
    @Override
    public DiningManagementAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_dining_management_card, parent, false);
        return new DiningManagementAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiningManagementAdapter.ViewHolder holder, int position) {
        if(selectedPosition == position) {
            holder.detailText.setVisibility(View.VISIBLE);
            holder.deleteText.setVisibility(View.VISIBLE);
        } else {
            holder.detailText.setVisibility(View.GONE);
            holder.deleteText.setVisibility(View.GONE);
        }
        holder.onBind(datas.get(position));
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public void addItem(DiningManagementCardData data) {
        datas.add(data);
    }

    public void removeAllItem() {
        datas = new ArrayList<>();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private TextView diningStatus;
        private TextView diningTitle;
        private TextView diningDate;
        private TextView diningLocation;
        private TextView reservedCount;
        private TextView detailText;
        private TextView deleteText;

        private DatabaseReference databaseReference, ref_h;
        private FirebaseAuth mAuth;
        private FirebaseUser user;

        private HashMap<String, UserData> userDataMap =  new HashMap<>();
        private int currentReservationCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = (CardView)itemView.findViewById(R.id.card_diningManageCard);
            diningStatus = (TextView)itemView.findViewById(R.id.status_diningMangeCard);
            diningTitle = (TextView)itemView.findViewById(R.id.title_diningManageCard);
            diningDate = (TextView)itemView.findViewById(R.id.date_diningManageCard);
            diningLocation = (TextView)itemView.findViewById(R.id.location_diningManageCard);
            reservedCount = (TextView)itemView.findViewById(R.id.reserved_count_diningManageCard);
            detailText = (TextView)itemView.findViewById(R.id.detail_text_diningManageCard);
            deleteText = (TextView)itemView.findViewById(R.id.delete_text_diningManageCard);
        }

        private void onBind(final DiningManagementCardData data) {
            diningTitle.setText(data.getDiningTitle());
            diningDate.setText(data.getDiningDate());
            diningLocation.setText(data.getDiningLocation());
            reservedCount.setText(data.getCurrentReservationCount() + "명 예약 완료");

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date time = new Date();
            String currentDate = format.format(time);
            Drawable bg = activity.getResources().getDrawable(R.drawable.textview_status_background);

            if(data.getDiningDate().substring(0, 10).compareTo(currentDate) < 0) {
                bg.setColorFilter(activity.getResources().getColor(R.color.colorDanger), PorterDuff.Mode.SRC_ATOP);
                diningStatus.setBackground(bg);
                diningStatus.setText("종료된 다이닝");
                deleteText.setTextColor(Color.GRAY);
                deleteText.setEnabled(false);
            } else if (data.getDiningDate().substring(0, 10).compareTo(currentDate) > 0) {
                bg.setColorFilter(activity.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                diningStatus.setBackground(bg);
                diningStatus.setText("예정된 다이닝");
            } else {
                bg.setColorFilter(activity.getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
                diningStatus.setBackground(bg);
                diningStatus.setText("오늘의 일정");
            }

            databaseReference = FirebaseDatabase.getInstance().getReference("Dining");

            diningLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//주소 textview를 누르면 지도 어플리케이션으로 연결
                    String address = diningLocation.getText().toString().replace(' ', '+');
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q="+address));
                    v.getContext().startActivity(intent);
                }
            });

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedPosition = getAdapterPosition();
                    notifyDataSetChanged();
                }
            });

            detailText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), DetailActivity.class);
                    intent.putExtra("diningUid", data.getDiningUid());
                    v.getContext().startActivity(intent);
                }
            });

            deleteText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(data.getCurrentReservationCount() != 0){
                        Snackbar.make(v, "예약되어 있는 사람이 있어 삭제할 수 없어요.", Snackbar.LENGTH_LONG).show();
                    } else {
                        deleteDining(data);
                    }
                }
            });
        }

        private void deleteDining(final DiningManagementCardData data) {
            databaseReference = FirebaseDatabase.getInstance().getReference("Dining");
            ref_h = FirebaseDatabase.getInstance().getReference("User").child("Host");
            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();
            getUserData();

            databaseReference.orderByKey().equalTo(data.getDiningUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        dataSnapshot.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(userDataMap.get(user.getUid()).getMyDining() != null) {
                        if(userDataMap.get(user.getUid()).getMyDining().values().contains(data.getDiningUid())) {
                            userDataMap.get(user.getUid()).getMyDining().values().remove(data.getDiningUid());
                            ref_h.child(user.getUid()).child("MyDining").setValue(userDataMap.get(user.getUid()).getMyDining());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        private void getUserData(){
            ref_h = FirebaseDatabase.getInstance().getReference("User").child("Host");
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
