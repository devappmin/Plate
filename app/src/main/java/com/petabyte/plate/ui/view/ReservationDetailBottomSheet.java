package com.petabyte.plate.ui.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
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

import java.util.ArrayList;
import java.util.List;

public class ReservationDetailBottomSheet extends BottomSheetDialogFragment {

    private Activity mActivity;
    private static List<ReservationCardData> datas = new ArrayList<>();
    private static int position;
    private List<String> dishes;
    private String UID;
    private String MEMBER_TYPE;
    private String RESERVATION_KEY;

    private FirebaseUser user;
    private DatabaseReference reference;

    private LinearLayout dishList;
    private Button cancelButton;
    private ImageButton exitButton;

    private DialogInterface.OnDismissListener onDismissListener;

    public ReservationDetailBottomSheet(Activity activity, List<ReservationCardData> datas, int adapterPosition)
    {
        this.mActivity = activity;
        this.datas = datas;
        this.position = adapterPosition;
        this.user = FirebaseAuth.getInstance().getCurrentUser();
        this.UID = user.getUid();
        this.dishes = datas.get(position).getDishes();
        this.MEMBER_TYPE = datas.get(position).getMemtype();
        this.RESERVATION_KEY = datas.get(position).getTimeKey();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.view_reservation_detail_bottom_sheet, container, false);
        dishList = (LinearLayout) rootView.findViewById(R.id.linear_layout_dishList_reservation_bottom_sheet);
        cancelButton = (Button) rootView.findViewById(R.id.button_v_cancel_reservation_bottom_sheet);
        exitButton = (ImageButton) rootView.findViewById(R.id.button_v_exit_reservation_bottom_sheet);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // bottom sheet 내부의 linear layout 에 코스 리스트를 불러와서 설정
        setDishlist();

        // 예약취소 버튼 클릭시 실행되는 함수
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference = FirebaseDatabase.getInstance().getReference("User").child(MEMBER_TYPE).child(UID).child("Reservation");
                // 예약 취소
                makeDialog("정말 이 다이닝의 예약을 취소하시겠어요?");
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
        d.setPositiveButton("네 취소할래요", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                reference.child(RESERVATION_KEY).child("Status").setValue("예약취소");
                dismiss();
            }
        });
        d.setNegativeButton("잘못눌렀어요", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {dialog.dismiss();}
        });

        // set btn color
        AlertDialog tmp = d.create();
        tmp.show();
        tmp.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
        tmp.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    // 코스 목록을 textview로 만들어서 layout에 추가
    private void setDishlist() {
        int len = dishes.size();
        for(int i=1; i<len; i++){
            TextView tv = new TextView(mActivity);
            tv.setText(dishes.get(i));
            tv.setTextSize(24);
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tv.setTextColor(getResources().getColor(R.color.textDarkPrimary));
            tv.setPadding(0, 0, 0, 20);
            dishList.addView(tv);
        }
    }
}

