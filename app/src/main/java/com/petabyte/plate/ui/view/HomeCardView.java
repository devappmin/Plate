package com.petabyte.plate.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.petabyte.plate.R;

public class HomeCardView extends CardView {

    private TextView titleTextView;

    public HomeCardView(@NonNull Context context) {
        super(context);
        init();
    }

    public HomeCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HomeCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void create(String title) {
        titleTextView.setText(title);
    }

    private void init() {
        inflate(getContext(), R.layout.view_homecard, this);

        titleTextView = (TextView)this.findViewById(R.id.title_tv_v_homecard);
    }
}
