package com.mstudenets.studenetsbananaapp.view.fragments;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.mstudenets.studenetsbananaapp.App;
import com.mstudenets.studenetsbananaapp.R;
import com.mstudenets.studenetsbananaapp.libs.weatherlibrary.datamodel.WeatherModel;
import com.mstudenets.studenetsbananaapp.tasks.weather.CurrentWeatherService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    private String TAG = WeatherFragment.class.getName();
    private static final int PERMISSION_LOCATION_REQUEST_CODE = 150;

    private GoogleApiClient mGoogleApiClient;
    private Location location;

    private Context context;
    private Bundle bundle;
    private TextView cityText;
    private TextView tempText;
    private TextView conditionText;
    private ConstraintLayout fragmentContainer;

    public WeatherFragment() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);

        bundle = savedInstanceState;
        context = getContext();
        cityText = (TextView) view.findViewById(R.id.fragment_weather_city);
        tempText = (TextView) view.findViewById(R.id.fragment_weather_temp);
        conditionText = (TextView) view.findViewById(R.id.fragment_weather_condition);
        fragmentContainer = (ConstraintLayout) view.findViewById(R.id.fragment_weather_container);

        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();


        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API).build();
        }

        if (isConnected) {
            checkLocationPermission();

            /*
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(App.APIXU_CURRENT_URL).addConverterFactory(GsonConverterFactory.create())
                    .build();
            CurrentWeatherService service = retrofit.create(CurrentWeatherService.class);
            String location = "Las Palmas de Gran Canaria";
            service.getCurrentWeather(App.APIXU_KEY, location)
                    .enqueue(new Callback()
                    {
                        @Override
                        public void onResponse(Call call, Response response) {
                            WeatherModel model = (WeatherModel) response.body();
                            String city = model.getLocation().getName() +
                                    ", " + model.getLocation().getRegion() + ", " +
                                    model.getLocation().getCountry();
                            double temperature = model.getCurrent().getTempC();
                            String condition = model.getCurrent().getCondition().getText();

                            checkTemperature(temperature);

                            cityText.setText(city);
                            tempText.setText(String.valueOf(temperature));
                            conditionText.setText(condition);
                        }

                        @Override
                        public void onFailure(Call call, Throwable t) {
                            cityText.setText("Failed to obtain data from the weather service");
                            tempText.setVisibility(View.INVISIBLE);
                            conditionText.setVisibility(View.INVISIBLE);
                        }
                    });
                    */
        } else {
            Toast.makeText(context, "Cannot connect to the Internet. Check network settings",
                    Toast.LENGTH_LONG).show();
            cityText.setVisibility(View.INVISIBLE);
            tempText.setVisibility(View.INVISIBLE);
            conditionText.setVisibility(View.INVISIBLE);
        }

        return view;
    }

    @Override
    public void onConnected(Bundle bundle) {
        try {
            location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                String request = latitude + ", " + longitude;

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

    private void checkLocation() {
        onConnected(bundle);
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
                        String city = model.getLocation().getName() +
                                ", " + model.getLocation().getRegion() + ", " +
                                model.getLocation().getCountry();
                        double temperature = model.getCurrent().getTempC();
                        String condition = model.getCurrent().getCondition().getText();

                        checkTemperature(temperature);

                        cityText.setText(city);
                        tempText.setText(String.valueOf(temperature));
                        conditionText.setText(condition);
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        cityText.setText("Failed to obtain data from the weather service");
                        tempText.setVisibility(View.INVISIBLE);
                        conditionText.setVisibility(View.INVISIBLE);
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
