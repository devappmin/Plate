package com.petabyte.plate.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.petabyte.plate.R;
import com.petabyte.plate.data.ReservationCardData;
import com.petabyte.plate.ui.view.ReservationDetailBottomSheet;
import com.petabyte.plate.ui.view.ReviewBottomSheet;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ReservationListAdapter extends RecyclerView.Adapter<ReservationListAdapter.ViewHolder> {

    private static List<ReservationCardData> datas = new ArrayList<>();
    private static Activity mActivity;
    private static FragmentManager mFragmentManager;

    public ReservationListAdapter(FragmentManager fm, Activity activity) {
        mFragmentManager = fm;
        mActivity = activity;
    }

    // item view를 관리하는 ViewHolder 객체를 생성한다.
    @NonNull
    @Override
    public ReservationListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_reservationcard, parent, false);
        return new ReservationListAdapter.ViewHolder(view);
    }

    // position에 해당되는 데이터를 ViewHolder가 관리하는 View에 바인딩
    @Override
    public void onBindViewHolder(@NonNull ReservationListAdapter.ViewHolder holder, int position) {
        holder.onBind(datas.get(position));
    }

    // 현재 adapter가 관리하는 데이터의 개수를 리턴
    @Override
    public int getItemCount() {
        return datas.size();
    }

    public void addItem(ReservationCardData data) {
        datas.add(data);
    }

    public void removeAllItem() {
        datas = new ArrayList<>();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView itemCardView;
        private TextView statusTextView;
        private TextView titleTextView;
        private TextView timeTextView;
        private TextView locationTextView;
        private View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            itemCardView = itemView.findViewById(R.id.card_v_reservation_list);
            statusTextView = itemView.findViewById(R.id.text_v_reservation_chk_status);
            titleTextView = itemView.findViewById(R.id.text_v_reservation_title);
            timeTextView = itemView.findViewById(R.id.text_v_reservation_timestamp);
            locationTextView = itemView.findViewById(R.id.text_v_reservation_location);

            itemCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String status = datas.get(getAdapterPosition()).getStatus();
                    String review = datas.get(getAdapterPosition()).getReview();
                    long reservTime = datas.get(getAdapterPosition()).getTimestamp();
                    long currentTime = System.currentTimeMillis();
                    long result = reservTime-currentTime;

                    //Log.d("Result", reservTime + " / " + currentTime + " / " + result);

                    // 결제를 완료했고, 현재 시간이 예약시간보다 이후이고, 아직 리뷰를 하지 않았다면, 리뷰 bottom sheet를 띄움
                    if((status.equals("결제완료")) && (result < 0)) {
                        if(review.equals("FALSE")){
                            //Log.d("Result", "결제O, 예약시간 지남, 리뷰 아직 안함");
                            ReviewBottomSheet bottomSheet = new ReviewBottomSheet(mActivity, datas, getAdapterPosition());
                            bottomSheet.show(mFragmentManager, "ReviewBottomSheet");
                        }else{
                            // 결제완료, 현재시간이 예약시간 이후, 리뷰 완료한 경우 아무것도 하지 않음
                            //Log.d("Result", "리뷰완료");
                        }
                    }else{
                        // 기타 경우 코스 목록 bottom sheet를 표시함
                        ReservationDetailBottomSheet bottomSheet = new ReservationDetailBottomSheet(mActivity, datas, getAdapterPosition());
                        bottomSheet.show(mFragmentManager, "ReservationDetailBottomSheet");
                    }
                }
            });
        }

        private void onBind(ReservationCardData data) {
            statusTextView.setText(data.getStatus());
            Drawable bg = mActivity.getResources().getDrawable(R.drawable.textview_status_background);

            switch(data.getStatus()){
                case "결제완료":
                    bg.setColorFilter(mActivity.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                    statusTextView.setBackground(bg);
                    statusTextView.setTextColor(Color.WHITE);
                    break;
                case "예약취소":
                    bg.setColorFilter(mActivity.getResources().getColor(R.color.colorDanger), PorterDuff.Mode.SRC_ATOP);
                    statusTextView.setBackground(bg);
                    statusTextView.setTextColor(Color.WHITE);
                    itemCardView.setClickable(false);
                    break;
            }
            titleTextView.setText(data.getTitle());
            timeTextView.setText(convertTimestamp(data.getTimestamp()));
            locationTextView.setText(data.getLocation());
        }

        public String convertTimestamp(long time) {
            String pattern = "yyyy-MM-dd HH:mm";
            SimpleDateFormat formatter = new SimpleDateFormat(pattern);
            String date = (String) formatter.format(new Timestamp(time));
            return date;
        }
    }
}
