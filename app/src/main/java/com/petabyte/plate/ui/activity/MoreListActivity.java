package com.petabyte.plate.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.petabyte.plate.R;
import com.petabyte.plate.adapter.MoreListAdapter;
import com.petabyte.plate.data.HomeCardData;

import java.util.ArrayList;

public class MoreListActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MoreListAdapter recyclerAdapter;

    private StorageReference reference;

    private ArrayList<HomeCardData> datum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_list);

        // 데이터 값을 불러온다.
        Intent intent = getIntent();
        if (intent != null)
            datum = intent.getParcelableArrayListExtra("data");

        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing_tb_av_more_list);
        toolbar = (Toolbar)findViewById(R.id.toolbar_av_more_list);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view_av_more_list);

        // Toolbar를 ActionBar로 변환하고 타이틀을 설정 후 뒤로가기 버튼을 만든다.
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Storage Reference 초기화
        reference = FirebaseStorage.getInstance("gs://plate-f5144.appspot.com/").getReference();

        // Collapsing Toolbar에 디자인을 추가한다.
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);

        // RecyclerView에 LayoutManager 연결
        layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // Divider를 각 아이템 사이에 추가한다.
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        // RecyclerView에 Adapter연결
        recyclerAdapter = new MoreListAdapter();
        recyclerAdapter.setReference(reference);
        recyclerAdapter.setDatum(datum);
        recyclerView.setAdapter(recyclerAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}