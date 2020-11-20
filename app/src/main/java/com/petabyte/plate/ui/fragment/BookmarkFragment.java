package com.petabyte.plate.ui.fragment;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.petabyte.plate.R;
import com.squareup.picasso.Picasso;


public class BookmarkFragment extends Fragment {



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bookmark, container, false);

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)v.findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);

        LottieAnimationView lottie = (LottieAnimationView)v.findViewById(R.id.lottie);
        lottie.playAnimation();

        Context thisContext = container.getContext();
        final ImageView imageView = (ImageView)v.findViewById(R.id.image_view_bookmark);
        GradientDrawable drawable = (GradientDrawable)thisContext.getDrawable(R.drawable.image_rounding);
        imageView.setBackground(drawable);
        imageView.setClipToOutline(true);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        storageReference.child("chef.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d("URI",uri.toString());
                Picasso.get().load(uri.toString()).resize(150,150).into(imageView);
            }
        });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final TextView diningName = (TextView)v.findViewById(R.id.dining_name);
        final TextView diningDate = (TextView)v.findViewById(R.id.dining_date);
        final TextView diningLocation = (TextView)v.findViewById(R.id.dining_location);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                diningName.setText(snapshot.child("Dining").child("HostUID-TimeStamp-1").child("Title").getValue().toString());
                diningDate.setText(snapshot.child("Dining").child("HostUID-TimeStamp-1").child("Schedules").child("RANDOMKEY").child("start").getValue().toString());
                diningLocation.setText(snapshot.child("Dining").child("HostUID-TimeStamp-1").child("Location").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        return v;
    }
}
