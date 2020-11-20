package com.petabyte.plate.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.petabyte.plate.R;
import com.petabyte.plate.utils.LogTags;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchInputFragment extends Fragment implements TextWatcher {

    private EditText searchEditText;
    private FloatingActionButton nextButton;

    private List<String> addresses;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search_input, container, false);

        searchEditText = (EditText) v.findViewById(R.id.search_et_fr_search_input);
        nextButton = (FloatingActionButton)v.findViewById(R.id.next_fab_fr_search_input);

        searchEditText.addTextChangedListener(this);

        addresses = Arrays.asList(getResources().getStringArray(R.array.spinner_region_seoul_gangbuk));

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle result = new Bundle();
                result.putString("address", searchEditText.getText().toString());
            }
        });


        //List<String> arrayList = new ArrayList<>(Arrays.asList("서울 강북구 " + getResources().getStringArray(R.array.spinner_region_seoul_gangbuk)));
        //ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, arrayList) ;

        return v;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
