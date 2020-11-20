package com.petabyte.plate.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;

import com.petabyte.plate.R;
import com.petabyte.plate.ui.fragment.SearchInputFragment;
import com.petabyte.plate.ui.fragment.SearchMapFragment;


public class SearchLocationActivity extends AppCompatActivity {

    private static final int NUM_PAGES = 2;

    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location);

        viewPager = (ViewPager)findViewById(R.id.viewpager_av_location);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), 0);
        viewPager.setAdapter(pagerAdapter);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            if(position == 0) fragment = new SearchInputFragment();
            else fragment = new SearchMapFragment();

            return fragment;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}