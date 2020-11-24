package com.petabyte.plate.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.petabyte.plate.R;
import com.petabyte.plate.data.FoodStyle;
import com.petabyte.plate.utils.LogTags;

public class RecommendChipGroup extends ConstraintLayout {

    private ChipGroup chipGroup;

    public RecommendChipGroup(@NonNull Context context) {
        super(context);

        init(context);
    }

    public RecommendChipGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public RecommendChipGroup(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    public RecommendChipGroup(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_recommend_chip_group, this);

        chipGroup = (ChipGroup)this.findViewById(R.id.chip_group_v_recommend_chip);

        addAllChip();
    }


    public void addAllChip() {
        for(FoodStyle style : FoodStyle.values()) {
            Chip chip = new Chip(chipGroup.getContext());
            chip.setText("#" + style.label);
            chipGroup.addView(chip);
        }
    }
}
