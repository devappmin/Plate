package com.petabyte.plate.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.petabyte.plate.R;
import com.petabyte.plate.ui.fragment.*;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView    bottomNavigationView;
    private Fragment[]              fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        fragments = new Fragment[4];
        fragments[0] = new HomeFragment();
        fragments[1] = new ResultFragment();
        fragments[2] = new BookmarkFragment();
        fragments[3] = new MyPageFragment();

        loadFragment(fragments[0]);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.action_home:
                fragment = fragments[0];
                break;

            case R.id.action_result:
                fragment = fragments[1];
                break;

            case R.id.action_bookmark:
                fragment = fragments[2];
                break;

            case R.id.action_mypage:
                fragment = fragments[3];
                break;
        }
        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_main, fragment).commit();
            return true;
        }
        return false;
    }
}