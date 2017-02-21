package com.mstudenets.studenetsbananaapp.libs.weatherlibrary.datamodel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Forecast implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@SerializedName("forecastday")
	public ArrayList<Forecastday> forecastday = new ArrayList<Forecastday>();
	
	public ArrayList<Forecastday> getForecastday()
    {
    	return forecastday;
    }
    public void setForecastday(ArrayList<Forecastday> mForecastday)
    {
    	this.forecastday = mForecastday;
    }
	
	
}
