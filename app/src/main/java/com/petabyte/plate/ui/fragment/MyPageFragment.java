package com.petabyte.plate.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.petabyte.plate.R;
import com.petabyte.plate.ui.activity.LoginActivity;
import com.petabyte.plate.ui.activity.MainActivity;
import com.petabyte.plate.ui.activity.RegisterActivity;

import org.w3c.dom.Text;

public class MyPageFragment extends Fragment {

    private TextView text_username;
    private TextView text_usertype;
    private TextView text_usermail;
    private TextView btn_logout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_mypage, container, false);

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)v.findViewById(R.id.collapsing_toolbar_mypage);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);

        btn_logout = (TextView)v.findViewById(R.id.text_v_mypage_logout);
        btn_logout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return v;
    }
}
