package com.petabyte.plate.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.petabyte.plate.R;
import com.petabyte.plate.utils.GlideApp;
import com.squareup.picasso.Picasso;

public class ScrollableImageActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView imageView;

    private String imageUri;
    private String title;

    private StorageReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrollable_image);

        Intent intent = getIntent();
        imageUri = intent.getStringExtra("ImageUri");
        title = intent.getStringExtra("Title");

        // 뷰를 불러온다.
        toolbar = (Toolbar)findViewById(R.id.toolbar_av_scrollable_image);
        imageView = (ImageView)findViewById(R.id.image_v_av_scrollable_image);

        // Toolbar를 ActionBar로 변환하고 타이틀을 설정 후 뒤로가기 버튼을 만든다.
        setSupportActionBar(toolbar);
        setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        reference = FirebaseStorage.getInstance("gs://plate-f5144.appspot.com/").getReference().child(imageUri);

        // 이미지를 설정한다.
        GlideApp.with(this).load(reference).into(imageView);
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