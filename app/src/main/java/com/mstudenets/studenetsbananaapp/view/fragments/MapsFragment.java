package com.mstudenets.studenetsbananaapp.view.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mstudenets.studenetsbananaapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapsFragment extends SupportMapFragment implements
        OnMapReadyCallback

{
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean permissionDenied = false;
    private MapView mapView;
    private GoogleMap mMap;

    public MapsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_maps, container, false);
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng chernivtsi = new LatLng(48.2937878, 25.9172208);
        mMap.addMarker(new MarkerOptions().position(chernivtsi).title("Chernivtsi"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(chernivtsi));
    }

/*

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng chernivtsi = new LatLng(48.2937878, 25.9172208);
        mMap.addMarker(new MarkerOptions().position(chernivtsi).title("Chernivtsi"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(chernivtsi));
        /*
        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();
        */
  /*  }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getContext(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }
        if (MapsPermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            enableMyLocation();
        } else {
            permissionDenied = true;
        }
    }

    /*
    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            permissionDenied = false;
        }
    }
    */

  /*

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            MapsPermissionUtils.requestPermission((AppCompatActivity) getActivity(),
                    LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }
    }

    /*
    private void showMissingPermissionError() {
        MapsPermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getFragmentManager(), "dialog");
    }
    */
}
