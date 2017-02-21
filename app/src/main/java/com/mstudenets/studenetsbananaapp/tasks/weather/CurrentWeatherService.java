package com.mstudenets.studenetsbananaapp.tasks.weather;


import com.mstudenets.studenetsbananaapp.libs.weatherlibrary.datamodel.WeatherModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CurrentWeatherService
{
    @GET("/v1/current.json")
    Call<WeatherModel> getCurrentWeather(@Query("key") String key, @Query("q") String query);
}
