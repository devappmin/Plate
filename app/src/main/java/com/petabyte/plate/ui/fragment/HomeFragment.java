package com.petabyte.plate.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.petabyte.plate.R;
import com.petabyte.plate.ui.activity.SearchActivity;
import com.petabyte.plate.ui.view.HomeAwardsList;
import com.petabyte.plate.ui.view.HomeHorizontalList;
import com.petabyte.plate.utils.ConnectionCodes;
import com.squareup.picasso.Picasso;

public class HomeFragment extends Fragment {

    private CardView searchButton;
    private ImageView applyImage;
    private HomeHorizontalList specialList;
    private HomeAwardsList awardsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        searchButton = (CardView) v.findViewById(R.id.search_card_fm_home);
        applyImage = (ImageView)v.findViewById(R.id.apply_iv_fm_home);
        specialList = (HomeHorizontalList)v.findViewById(R.id.special_hh_fm_home);
        awardsList = (HomeAwardsList)v.findViewById(R.id.awards_ha_fm_home);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                getActivity().startActivityForResult(intent, ConnectionCodes.REQUEST_SEARCH_ACTIVITY);
            }
        });

        awardsList.setTitle("PLATE 포스트");
        awardsList.addImages("https://firebasestorage.googleapis.com/v0/b/plate-f5144.appspot.com/o/awards%2Fawards1.png?alt=media&token=0838b507-76e4-4281-840d-15a8b376276f");
        awardsList.addImages("https://firebasestorage.googleapis.com/v0/b/plate-f5144.appspot.com/o/awards%2Fawards2.png?alt=media&token=1312a5f8-125e-4ad9-b96d-29bd5ca32234");
        awardsList.addImages("https://firebasestorage.googleapis.com/v0/b/plate-f5144.appspot.com/o/awards%2Fawards3.png?alt=media&token=96ff113d-5a17-4ab8-bd0b-6deeff52ea92");
        awardsList.addImages("https://firebasestorage.googleapis.com/v0/b/plate-f5144.appspot.com/o/awards%2Fawards4.png?alt=media&token=db5aef4a-07e4-494e-bffe-4d5af81b9211");
        awardsList.addImages("https://firebasestorage.googleapis.com/v0/b/plate-f5144.appspot.com/o/awards%2Fawards5.png?alt=media&token=4c211e73-e8ad-49fb-a9dd-42548a9f4321");
        awardsList.addImages("https://firebasestorage.googleapis.com/v0/b/plate-f5144.appspot.com/o/awards%2Fawards6.png?alt=media&token=50096b22-95f1-416a-b4cc-1a20ead5ed7e");

        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/plate-f5144.appspot.com/o/chef.png?alt=media&token=2e0e6f43-2523-482e-82ff-f5cd5ad54e19")
                .fit().centerCrop().into(applyImage);

        return v;
    }
}
