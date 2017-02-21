package com.mstudenets.studenetsbananaapp.view.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mstudenets.studenetsbananaapp.App;
import com.mstudenets.studenetsbananaapp.R;
import com.mstudenets.studenetsbananaapp.libs.weatherlibrary.datamodel.WeatherModel;
import com.mstudenets.studenetsbananaapp.tasks.weather.CurrentWeatherService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherFragment extends Fragment
{
    private String TAG = WeatherFragment.class.getName();
    private String location = "Las Palmas de Gran Canaria";

    private TextView cityText;
    private TextView tempText;
    private TextView conditionText;

    public WeatherFragment() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);

        cityText = (TextView) view.findViewById(R.id.fragment_weather_city);
        tempText = (TextView) view.findViewById(R.id.fragment_weather_temp);
        conditionText = (TextView) view.findViewById(R.id.fragment_weather_condition);
        
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(App.APIXU_CURRENT_URL).addConverterFactory(GsonConverterFactory.create())
                .build();
        CurrentWeatherService service = retrofit.create(CurrentWeatherService.class);
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

                    }
                });

        return view;
    }

    private void checkTemperature(double temperature) {
        if (temperature >= 0)
            tempText.setTextColor(Color.parseColor("#B71C1C"));
        else tempText.setTextColor(Color.parseColor("#0D47A1"));
    }
}
