package com.petabyte.plate.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.petabyte.plate.R;

public class AddDishImageHorizontalList extends ConstraintLayout {

    private ConstraintLayout constraintLayout;
    private ImageView imageView;
    public ImageView removeButton;

    public AddDishImageHorizontalList(@NonNull Context context) {
        super(context);
        initView();
    }

    public AddDishImageHorizontalList(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public AddDishImageHorizontalList(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public AddDishImageHorizontalList(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(infService);
        View v = layoutInflater.inflate(R.layout.view_add_dish_image, this, false);
        addView(v);

        constraintLayout = (ConstraintLayout) findViewById(R.id.layout_addDish);
        imageView = (ImageView) findViewById(R.id.image_addDish);
        removeButton = (ImageView) findViewById(R.id.remove_addDish);
        imageView.setClipToOutline(true);
    }

    public void setImage(Bitmap bm) {
        imageView.setImageBitmap(bm);
    }
}
