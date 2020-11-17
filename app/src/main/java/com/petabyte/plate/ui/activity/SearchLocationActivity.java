package com.petabyte.plate.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.petabyte.plate.R;
import com.petabyte.plate.utils.LogTags;

import java.io.IOException;
import java.util.List;


public class SearchLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location);

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map_fr_av_location);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        Context context = this;

        Location soong = addrToPoint(context);
        final LatLng soongsil = new LatLng(soong.getLatitude(), soong.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(soongsil);
        markerOptions.title("숭실대");
        googleMap.addMarker(markerOptions);

        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(soongsil, 17));
    }

    public static Location addrToPoint(Context context) {
        Location location = new Location("");
        Geocoder geocoder = new Geocoder(context);
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocationName("행운1마길 10-4", 5);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null) {
            for (int i = 0; i < addresses.size(); i++) {
                Address lating = addresses.get(i);
                location.setLatitude(lating.getLatitude());
                location.setLongitude(lating.getLongitude());
                Log.d(LogTags.POINT, addresses.size() + "");
            }
        }
        return location;
    }
}