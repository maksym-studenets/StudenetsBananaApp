package com.mstudenets.studenetsbananaapp;

import android.app.Application;

import com.mstudenets.studenetsbananaapp.tasks.weather.CurrentWeatherService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class App extends Application
{
    public static String APIXU_KEY = "263e72f2594a4a5b8be123552171402";
    public static String APIXU_CURRENT_URL = "http://api.apixu.com";

    private static CurrentWeatherService currentWeatherService;
    private Retrofit retrofitCurrent;

    @Override
    public void onCreate() {
        super.onCreate();

        retrofitCurrent = new Retrofit.Builder().baseUrl(APIXU_CURRENT_URL).
                addConverterFactory(GsonConverterFactory.create()).build();
        currentWeatherService = retrofitCurrent.create(CurrentWeatherService.class);
    }

    public static CurrentWeatherService getCurrentWeatherService() {
        return currentWeatherService;
    }
}
