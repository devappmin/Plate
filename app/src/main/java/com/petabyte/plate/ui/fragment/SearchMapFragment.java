package com.petabyte.plate.ui.fragment;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.petabyte.plate.R;
import com.petabyte.plate.utils.LogTags;

import java.io.IOException;
import java.util.List;

public class SearchMapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private String locationName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search_map, container, false);

        MapView mapFragment = (MapView)v.findViewById(R.id.map_fr_fr_location);
        mapFragment.onCreate(savedInstanceState);
        mapFragment.onResume();
        mapFragment.getMapAsync(this);

        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        Context context = getContext();

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
            addresses = geocoder.getFromLocationName("청라자이", 5);
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
