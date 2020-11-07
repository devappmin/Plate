package com.petabyte.plate.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.petabyte.plate.R;

/**
 * Create custom horizontal recycler view
 * inside of the Nested Scroll View in Home Fragment
 */

public class HomeHorizontalList extends ConstraintLayout {
    public HomeHorizontalList(@NonNull Context context) {
        super(context);
        init();
    }

    public HomeHorizontalList(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HomeHorizontalList(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public HomeHorizontalList(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_homehorizontallist, this);
    }
}
