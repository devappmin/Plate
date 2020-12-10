package com.petabyte.plate.ui.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.petabyte.plate.R;
import com.petabyte.plate.data.ReservationCardData;
import com.petabyte.plate.ui.activity.DetailActivity;

import java.util.ArrayList;
import java.util.List;

public class ReviewBottomSheet extends BottomSheetDialogFragment {

    private Activity mActivity;
    private static List<ReservationCardData> datas = new ArrayList<>();
    private static int position;
    private String UID;
    private String MEMBER_TYPE;
    private String RESERVATION_KEY;
    private String diningUID;
    private String hostUID;

    private FirebaseUser user;
    private DatabaseReference ref_user, ref_host;

    private Button submitButton;
    private ImageButton exitButton;
    private RatingBar ratingBar;
    private float ratingNum;

    private DialogInterface.OnDismissListener onDismissListener;

    public ReviewBottomSheet(Activity activity, List<ReservationCardData> datas, int adapterPosition)
    {
        this.mActivity = activity;
        this.datas = datas;
        this.position = adapterPosition;
        this.user = FirebaseAuth.getInstance().getCurrentUser();
        this.UID = user.getUid();

        // adapter의 현재 position에 따라 datas의 값 가져옴
        this.MEMBER_TYPE = datas.get(position).getMemtype();
        this.RESERVATION_KEY = datas.get(position).getTimeKey();
        this.diningUID = datas.get(position).getDiningUID();
        this.hostUID = diningUID.substring(0, 28);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.view_review_bottom_sheet, container, false);

        exitButton = (ImageButton) rootView.findViewById(R.id.button_v_exit_review_bottom_sheet);
        submitButton = (Button) rootView.findViewById(R.id.button_v_submit_review_bottom_sheet);
        ratingBar = (RatingBar) rootView.findViewById(R.id.rating_bar_review_bottom_sheet);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 예약취소 버튼 클릭시 실행되는 함수
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref_user = FirebaseDatabase.getInstance().getReference("User").child(MEMBER_TYPE).child(UID).child("Reservation");
                ref_host = FirebaseDatabase.getInstance().getReference("User").child("Host").child(hostUID).child("Profile");
                makeDialog("평가는 수정이 불가능해요.\n이대로 등록하시겠어요?");
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingNum = rating;
            }
        });

        // bottom sheet exit 버튼 클릭시 실행되는 함수
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void makeDialog(String message){
        final AlertDialog.Builder d = new AlertDialog.Builder(getContext());
        d.setMessage(message);
        d.setPositiveButton("네!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // 예약 내역의 리뷰 여부를 TRUE로 바꿈
                ref_user.child(RESERVATION_KEY).child("Review").setValue("TRUE");

                // 평가한 다이닝의 Host-Profile-Rating과 RatingCount를 수정함
                // 원본 값을 가져와서 계산한 뒤 다시 업데이트 함
                ref_host.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        float srcRatingCnt = Float.parseFloat(String.valueOf(snapshot.child("RatingCount").getValue()));
                        float srcRating = Float.parseFloat(String.valueOf(snapshot.child("Rating").getValue()));
                        float newRating = (srcRating * srcRatingCnt + ratingNum) / (srcRatingCnt + 1);

                        ref_host.child("RatingCount").setValue(srcRatingCnt+1);
                        ref_host.child("Rating").setValue(newRating);
                        //Log.d("Result", srcRatingCnt+" / "+srcRating+" / "+newRating);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });


                dismiss();
            }
        }).setNegativeButton("다시할래요", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // set btn color
        AlertDialog tmp = d.create();
        tmp.show();
        tmp.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
        tmp.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
    }
}

