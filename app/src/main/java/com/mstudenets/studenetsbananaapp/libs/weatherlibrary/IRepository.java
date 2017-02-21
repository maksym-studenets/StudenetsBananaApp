package com.mstudenets.studenetsbananaapp.libs.weatherlibrary;

public interface IRepository {
	
	void GetWeatherData(String key, RequestBlocks.GetBy getBy, String value, RequestBlocks.Days ForecastOfDays) throws Exception;
	void GetWeatherDataByLatLong(String key, String latitude, String longitude, RequestBlocks.Days ForecastOfDays) throws Exception;
    void GetWeatherDataByAutoIP(String key, RequestBlocks.Days ForecastOfDays) throws Exception;

    void GetWeatherData(String key, RequestBlocks.GetBy getBy, String value) throws Exception;
    void GetWeatherDataByLatLong(String key, String latitude, String longitude) throws Exception;
    void GetWeatherDataByAutoIP(String key) throws Exception;

}
