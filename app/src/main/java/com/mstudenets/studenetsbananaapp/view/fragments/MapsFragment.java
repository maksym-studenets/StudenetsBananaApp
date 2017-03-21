package com.mstudenets.studenetsbananaapp.view.fragments;


import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
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
import com.mstudenets.studenetsbananaapp.controller.database.DatabaseOperationManager;
import com.mstudenets.studenetsbananaapp.model.MapsPermissionUtils;
import com.mstudenets.studenetsbananaapp.model.MyMapMarker;

import java.util.ArrayList;


public class MapsFragment extends Fragment implements
        OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMapLongClickListener, GoogleMap.OnInfoWindowLongClickListener
{
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 10;
    private boolean isLocationPermissionGranted;
    private boolean isEdit = false;

    private AlertDialog.Builder markerDialog;
    private ArrayList<MyMapMarker> mapMarkers = new ArrayList<>();
    private EditText titleEdit, snippetEdit;
    private DatabaseOperationManager operationManager;

    private GoogleMap mMap;
    private LatLng currentMarkerPosition;
    private MapView mapView;
    private Marker marker;
    private View view;

    public MapsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        isLocationPermissionGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.M;

        operationManager = new DatabaseOperationManager();
        operationManager.getMapMarkerDao();
        mapMarkers = operationManager.selectMarkersFromDatabase();

        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        try {
            MapsInitializer.initialize(getContext());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error while initializing map",
                    Toast.LENGTH_SHORT).show();
        }
        mapView.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setOnMyLocationButtonClickListener(this);

        initializeDialog();
        zoomToCity();
        enableMyLocation();
        enableMapControls();

        for (MyMapMarker myMapMarker : mapMarkers) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(new LatLng(myMapMarker.getLatitude(), myMapMarker.getLongitude()))
                    .title(myMapMarker.getTitle())
                    .snippet(myMapMarker.getSnippet());
            mMap.addMarker(markerOptions);
        }

        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnInfoWindowLongClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        marker = null;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        isEdit = false;
        currentMarkerPosition = latLng;
        removeView();
        markerDialog.show();
    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {
        this.marker = marker;
        isEdit = true;
        removeView();
        markerDialog.show();
    }

    private void enableMyLocation() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                MapsPermissionUtils.requestPermission((AppCompatActivity) getActivity(),
                        LOCATION_PERMISSION_REQUEST_CODE,
                        Manifest.permission.ACCESS_FINE_LOCATION, false);
            } else if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE)
            return;

        try {
            if (MapsPermissionUtils.isPermissionGranted(permissions, grantResults,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                mMap.setMyLocationEnabled(true);
                enableMapControls();
            } else {
                isLocationPermissionGranted = false;
                enableMapControls();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void enableMapControls() {
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setZoomGesturesEnabled(true);
        uiSettings.setMyLocationButtonEnabled(isLocationPermissionGranted);
        uiSettings.setCompassEnabled(true);
        uiSettings.setScrollGesturesEnabled(true);
        uiSettings.setTiltGesturesEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
    }

    private void zoomToCity() {
        LatLng chernivtsi = new LatLng(48.292568, 25.935139);
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(chernivtsi, 13);
        mMap.animateCamera(location);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getContext(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false;
    }

    private void removeView() {
        if (view.getParent() != null)
            ((ViewGroup) view.getParent()).removeView(view);
    }

    private void initializeDialog() {
        markerDialog = new AlertDialog.Builder(getContext());
        view = getActivity().getLayoutInflater().inflate(R.layout.fragment_maps_dialog, null);
        markerDialog.setView(view);

        titleEdit = (EditText) view.findViewById(R.id.fragment_maps_dialog_edit_title);
        snippetEdit = (EditText) view.findViewById(R.id.fragment_maps_dialog_edit_snippet);

        markerDialog.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!isEdit) {
                    String title = titleEdit.getText().toString();
                    String snippet = snippetEdit.getText().toString();

                    MyMapMarker myMapMarker = new MyMapMarker(currentMarkerPosition.latitude,
                            currentMarkerPosition.longitude, title, snippet);

                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(currentMarkerPosition)
                            .title(title).snippet(snippet);
                    marker = mMap.addMarker(markerOptions);
                    boolean isSuccessful = operationManager.addMarker(myMapMarker);
                    if (isSuccessful) {
                        mMap.addMarker(markerOptions);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Marker add failed",
                                Toast.LENGTH_SHORT).show();
                        marker.remove();
                        dialog.dismiss();
                    }
                    titleEdit.setText("");
                    snippetEdit.setText("");
                } else {
                    titleEdit.setText(marker.getTitle());
                    snippetEdit.setText(marker.getTitle());

                    String title = titleEdit.getText().toString();
                    String snippet = snippetEdit.getText().toString();

                    boolean isSucessful = operationManager.updateMarker(title, snippet);
                    if (isSucessful) {
                        MyMapMarker myMapMarker = operationManager.getMapMarker(title, snippet);
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(new LatLng(myMapMarker.getLatitude(),
                                        myMapMarker.getLongitude()))
                                .title(myMapMarker.getTitle())
                                .snippet(myMapMarker.getSnippet());
                        mMap.addMarker(markerOptions);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Error updating marker",
                                Toast.LENGTH_SHORT).show();
                    }

                    titleEdit.setText("");
                    snippetEdit.setText("");
                }
            }
        });

        markerDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                titleEdit.setText("");
                snippetEdit.setText("");
                dialog.dismiss();
            }
        });
    }

}



        /*
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) view.
                findViewById(R.id.fragment_maps_coordinatorLayout);
        NestedScrollView bottomSheet = (NestedScrollView) coordinatorLayout
                .findViewById(R.id.fragment_maps_bottom_sheet);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        */