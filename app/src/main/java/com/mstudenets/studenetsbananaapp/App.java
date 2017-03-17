package com.mstudenets.studenetsbananaapp;

import android.app.Application;
import android.content.Context;

import com.mstudenets.studenetsbananaapp.controller.database.DatabaseHelper;
import com.mstudenets.studenetsbananaapp.tasks.CurrentWeatherService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class App extends Application
{
    public static String APIXU_KEY = "263e72f2594a4a5b8be123552171402";
    public static String APIXU_CURRENT_URL = "https://api.apixu.com";

    private static DatabaseHelper databaseHelper;
    private static CurrentWeatherService currentWeatherService;

    public App() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Context context = getApplicationContext();
        databaseHelper = new DatabaseHelper(context);

        Retrofit retrofitCurrent = new Retrofit.Builder().baseUrl(APIXU_CURRENT_URL).
                addConverterFactory(GsonConverterFactory.create()).build();
        currentWeatherService = retrofitCurrent.create(CurrentWeatherService.class);
    }

    public static CurrentWeatherService getCurrentWeatherService() {
        return currentWeatherService;
    }

    public static DatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }
}
