package com.mstudenets.studenetsbananaapp.view.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mstudenets.studenetsbananaapp.R;
import com.mstudenets.studenetsbananaapp.model.MyMapMarker;

import java.util.ArrayList;


public class MapsFragment extends Fragment implements
        OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener
{
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 10;
    private boolean isLocationPermissionGranted;
    private boolean isEdit = false;

    private AlertDialog.Builder alertDialog;
    private ArrayList<MyMapMarker> mapMarkers = new ArrayList<>();
    //private ArrayList<MarkerOptions> mMarkerOptions = new ArrayList<>();
    //private MarkersOperationManager operationManager;

    private MapView mapView;
    private GoogleMap mMap;

    public MapsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        try {
            MapsInitializer.initialize(getContext());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Maps init error", Toast.LENGTH_SHORT).show();
        }
        mapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        mMap = map;
        enableMapControls();

        LatLng city = new LatLng(48.291, 25.935);
        mMap.addMarker(new MarkerOptions().position(city).title("Chernivtsi"))
                .setDraggable(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(city));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2500, null);
        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("My marker");
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.addMarker(markerOptions);

                MyMapMarker mapMarker = new MyMapMarker(latLng.latitude, latLng.longitude,
                        markerOptions.getTitle(), "");
                mapMarkers.add(mapMarker);
            }
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener()
        {
            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                LatLng latLng = marker.getPosition();
                for (int i = 0; i < mapMarkers.size(); i++) {
                    if (mapMarkers.get(i).getLatitude() == latLng.latitude &&
                            mapMarkers.get(i).getLongitude() == latLng.longitude) {
                        mapMarkers.get(i).setLatitude(latLng.latitude);
                        mapMarkers.get(i).setLongitude(latLng.longitude);

                        //operationManager.updateRow(mapMarkers.get(i));
                    }
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateUi(isLocationPermissionGranted);
                } else {
                    Toast.makeText(getContext(), "Cannot show your current location",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void enableMapControls() {
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setZoomGesturesEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setScrollGesturesEnabled(true);
        uiSettings.setTiltGesturesEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(getContext(), "We need location permission to personalize the map",
                        Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }
    }

    private void updateUi(boolean isLocationPermissionGranted) {
        try {
            if (isLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void addMarkers() {
        if (mapMarkers != null) {
            for (MyMapMarker myMapMarker : mapMarkers) {
                mMap.addMarker(new MarkerOptions().position(new LatLng(myMapMarker.getLatitude(),
                        myMapMarker.getLongitude())).title("")).setDraggable(false);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(myMapMarker.getLatitude(),
                        myMapMarker.getLongitude())));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 1500, null);
            }
        }
    }
}
