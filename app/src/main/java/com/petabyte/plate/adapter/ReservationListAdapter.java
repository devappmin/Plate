package com.petabyte.plate.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.petabyte.plate.R;
import com.petabyte.plate.data.ReservationCardData;
import java.util.ArrayList;
import java.util.List;

public class ReservationListAdapter extends RecyclerView.Adapter<ReservationListAdapter.ViewHolder> {

    private List<ReservationCardData> datas = new ArrayList<>();
    private static Context mContext;

    public ReservationListAdapter(Context context) {
        mContext = context;
    }

    // item view를 관리하는 ViewHolder 객체를 생성한다.
    @NonNull
    @Override
    public ReservationListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_reservationcard, parent, false);
        return new ReservationListAdapter.ViewHolder(view);
    }

    // position에 해당되는 데이터를 ViewHolder가 관리하는 View에 바인딩 한다.
    @Override
    public void onBindViewHolder(@NonNull ReservationListAdapter.ViewHolder holder, int position) {
        holder.onBind(datas.get(position));
    }

    // 현재 adapter가 관리하는 데이터의 개수를 리턴한다.
    @Override
    public int getItemCount() {
        return datas.size();
    }

    public void addItem(ReservationCardData data) {
        datas.add(data);
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
                    Log.d("ji1dev", "card clicked");
                    //ExampleBottomSheetDialog bottomSheet = new ExampleBottomSheetDialog();
                    //bottomSheet.show(getSupportFragmentManager(), "exampleBottomSheet");
                }
            });
        }

        private void onBind(ReservationCardData data) {
            statusTextView.setText(data.getStatus());

            Drawable bg = mContext.getResources().getDrawable(R.drawable.textview_status_background);

            switch(data.getStatus()){
                case "결제완료":
                    bg.setColorFilter(mContext.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                    statusTextView.setBackground(bg);
                    statusTextView.setTextColor(Color.WHITE);
                    break;
                case "예약취소":
                    bg.setColorFilter(mContext.getResources().getColor(R.color.colorDanger), PorterDuff.Mode.SRC_ATOP);
                    statusTextView.setBackground(bg);
                    statusTextView.setTextColor(Color.WHITE);
                    break;
            }
            titleTextView.setText(data.getTitle());
            timeTextView.setText(data.getTimestamp());
            locationTextView.setText(data.getLocation());
        }
    }
}
