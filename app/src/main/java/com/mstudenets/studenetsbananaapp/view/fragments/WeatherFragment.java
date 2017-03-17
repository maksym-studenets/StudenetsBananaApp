package com.mstudenets.studenetsbananaapp.view.fragments;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.mstudenets.studenetsbananaapp.App;
import com.mstudenets.studenetsbananaapp.R;
import com.mstudenets.studenetsbananaapp.libs.weatherlibrary.datamodel.WeatherModel;
import com.mstudenets.studenetsbananaapp.tasks.CurrentWeatherService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.LOCATION_SERVICE;

public class WeatherFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    private String TAG = WeatherFragment.class.getName();
    private static final int PERMISSION_LOCATION_REQUEST_CODE = 150;

    private boolean isConnected;

    private GoogleApiClient mGoogleApiClient;
    private Location location;
    private LocationRequest locationRequest;
    private Context context;
    private TextView cityText, regionText, tempText, feelsLikeText;
    private ImageView imageView;
    private ConstraintLayout fragmentContainer;

    public WeatherFragment() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);

        context = getContext();
        cityText = (TextView) view.findViewById(R.id.fragment_weather_city);
        regionText = (TextView) view.findViewById(R.id.fragment_weather_region);
        tempText = (TextView) view.findViewById(R.id.fragment_weather_temp);
        feelsLikeText = (TextView) view.findViewById(R.id.fragment_weather_condition);
        imageView = (ImageView) view.findViewById(R.id.fragment_weather_condition_icon);
        fragmentContainer = (ConstraintLayout) view.findViewById(R.id.fragment_weather_container);
        hideWidgets();

        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API).build();
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (isConnected) {
            checkLocationPermission();
        } else {
            Toast.makeText(context, "Cannot connect to the Internet. Check network settings",
                    Toast.LENGTH_LONG).show();
            hideWidgets();
        }

        LocationManager service = (LocationManager) getContext()
                .getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            //location = new Location()
            double latitude = 28.45;
            double longitude = -16.23;
            String request = latitude + ", " + longitude;
            sendRequest(request);
            /*
            Toast.makeText(getContext(), "Please turn GPS on", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
            */
        }

        try {
            location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                String request = latitude + ", " + longitude;
                sendRequest(request);
            } else {
                double latitude = 28.45;
                double longitude = -16.23;
                String request = latitude + ", " + longitude;
                sendRequest(request);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Snackbar.make(fragmentContainer, "Error. Connection was suspended",
                Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Snackbar.make(fragmentContainer, "Cannot retrieve location. Connection failed",
                Snackbar.LENGTH_LONG).show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLocation();
            } else {
                String location = "Guadalajara";
                sendRequest(location);
            }
        }
    }

    public void onLocationChanged(Location location) {

    }

    /*
    private void checkLocationUpdates() {
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(60 * 1000);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                locationRequest, this);
    }
    */

    private void showWidgets() {
        cityText.setVisibility(View.VISIBLE);
        regionText.setVisibility(View.VISIBLE);
        tempText.setVisibility(View.VISIBLE);
        feelsLikeText.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.VISIBLE);
    }

    private void hideWidgets() {
        cityText.setVisibility(View.INVISIBLE);
        regionText.setVisibility(View.INVISIBLE);
        tempText.setVisibility(View.INVISIBLE);
        feelsLikeText.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.INVISIBLE);
    }

    private void checkLocation() {
        try {
            location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                String request = latitude + ", " + longitude;
                sendRequest(request);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void sendRequest(String location) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(App.APIXU_CURRENT_URL).addConverterFactory(GsonConverterFactory.create())
                .build();
        CurrentWeatherService service = retrofit.create(CurrentWeatherService.class);
        //String location = "Las Palmas de Gran Canaria";
        service.getCurrentWeather(App.APIXU_KEY, location)
                .enqueue(new Callback()
                {
                    @Override
                    public void onResponse(Call call, Response response) {
                        WeatherModel model = (WeatherModel) response.body();
                        String city = model.getLocation().getName();// +
                        String region = model.getLocation().getRegion() + ", " +
                                model.getLocation().getCountry();
                        double temperature = model.getCurrent().getTempC();
                        double feelsLike = model.getCurrent().getFeelslikeC();
                        //String conditionCode = model.getCurrent().getCondition().getText();
                        //String icon = model.getCurrent().getCondition().getIcon();
                        //String iconUrl = icon.substring(2);
                        checkTemperature(temperature);

                        cityText.setText(city);
                        regionText.setText(region);
                        tempText.setText(String.valueOf(temperature));
                        feelsLikeText.setText("Feels like: " + String.valueOf(feelsLike));
                        //Picasso.with(getContext()).load(iconUrl).into(imageView);
                        showWidgets();
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        cityText.setText("Failed to obtain data from the weather service");
                        tempText.setVisibility(View.INVISIBLE);
                        feelsLikeText.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void checkTemperature(double temperature) {
        if (temperature >= 0)
            tempText.setTextColor(Color.parseColor("#B71C1C"));
        else tempText.setTextColor(Color.parseColor("#0D47A1"));
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Snackbar.make(fragmentContainer, "We need location permission to show current" +
                        " weather at your current location", Snackbar.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_LOCATION_REQUEST_CODE);
            }
        }
    }
}
