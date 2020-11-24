package com.petabyte.plate.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.petabyte.plate.R;

public class NumberPickerBottomSheet extends BottomSheetDialogFragment {

    private NumberPicker numberPicker;
    private Button submitButton;

    private int min, max;

    public NumberPickerBottomSheet() {
        this.min = 1;
        this.max = 30;
    }

    public NumberPickerBottomSheet(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.view_number_picker_bottom_sheet, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // View 객체 할당
        submitButton = (Button)getView().findViewById(R.id.button_v_number_bottom_sheet);
        numberPicker = (NumberPicker)getView().findViewById(R.id.picker_v_number_bottom_sheet);

        // NumberPicker의 최소 값을 1, 최대 값을 30으로 설정
        numberPicker.setMaxValue(max);
        numberPicker.setMinValue(min);

        // 확인 버튼을 클릭했을 경우,
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // numberPicker의 값을 NumberPickerSelectedListener 인터페이스의 onNumberPickerSelected에 넘기고
                NumberPickerSelectedListener listener = (NumberPickerSelectedListener)getContext();
                listener.onNumberPickerSelected(numberPicker.getValue());

                // BottomSheet를 내린다.
                dismiss();
            }
        });
    }

    /**
     * NumberPicker를 통해서 얻은 값을 호출한 곳으로 보내기 위해 사용한 interface
     */
    public interface NumberPickerSelectedListener {
        void onNumberPickerSelected(int selectedValue);
    }
}
