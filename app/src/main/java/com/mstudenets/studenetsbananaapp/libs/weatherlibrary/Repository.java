package com.mstudenets.studenetsbananaapp.libs.weatherlibrary;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mstudenets.studenetsbananaapp.libs.weatherlibrary.RequestBlocks.MethodType;
import com.mstudenets.studenetsbananaapp.libs.weatherlibrary.datamodel.Current;
import com.mstudenets.studenetsbananaapp.libs.weatherlibrary.datamodel.Forecast;
import com.mstudenets.studenetsbananaapp.libs.weatherlibrary.datamodel.Location;
import com.mstudenets.studenetsbananaapp.libs.weatherlibrary.datamodel.WeatherModel;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public abstract class Repository implements IRepository{

	private String APIURL = "http://api.apixu.com/v1";
	private WeatherModel weatherModel;
	private Gson gson=new GsonBuilder().create();
	
	Context context;
	
	String url = "";
	 //region Get Weather Forecast Data
	@Override
	public void GetWeatherData(String key, RequestBlocks.GetBy getBy, String value,
			RequestBlocks.Days ForecastOfDays) throws Exception {
		// TODO Auto-generated method stub
		url = APIURL +RequestBuilder.PrepareRequest(MethodType.Forecast, key, getBy, value, ForecastOfDays);
		
		System.out.println("url==========>"+url);
		new getWeatherList().execute(url);
	}

	@Override
	public void GetWeatherDataByLatLong(String key, String latitude,
			String longitude, RequestBlocks.Days ForecastOfDays) throws Exception {
		// TODO Auto-generated method stub
		url = APIURL + RequestBuilder.PrepareRequestByLatLong(MethodType.Forecast, key,latitude,longitude, ForecastOfDays);
		
		System.out.println("url==========>"+url);
		new getWeatherList().execute(url);
	}

	@Override
	public void GetWeatherDataByAutoIP(String key, RequestBlocks.Days ForecastOfDays) throws Exception {
		// TODO Auto-generated method stub
	
		url = APIURL + RequestBuilder.PrepareRequestByAutoIP(MethodType.Forecast, key, ForecastOfDays);
		
		System.out.println("url==========>"+url);
		new getWeatherList().execute(url);
	}
	
	//endregion

    //region Get Weather Current Data

	@Override
	public void GetWeatherData(String key, RequestBlocks.GetBy getBy, String value) throws Exception {
		// TODO Auto-generated method stub

		url = APIURL + RequestBuilder.PrepareRequest(MethodType.Current, key, getBy, value);
		
		System.out.println("url==========>"+url);
		new getWeatherList().execute(url);
	}

	@Override
	public void GetWeatherDataByLatLong(String key, String latitude,
			String longitude) throws Exception {
		// TODO Auto-generated method stub

		url = APIURL + RequestBuilder.PrepareRequestByLatLong(MethodType.Current, key, latitude, longitude);
		
		System.out.println("url==========>"+url);
		new getWeatherList().execute(url);
		
	}

	@Override
	public void GetWeatherDataByAutoIP(String key) throws Exception {
		// TODO Auto-generated method stub

		url = APIURL + RequestBuilder.PrepareRequestByAutoIP(MethodType.Current, key);
		
		System.out.println("url==========>"+url);
		new getWeatherList().execute(url);
	}
	
	//endregion
	
	private WeatherModel GetData(String url)
    {
		getWeatherList task = new getWeatherList();
		task.execute();
		return weatherModel;
		

    }
	
	public class getWeatherList extends AsyncTask<String, Void, String>
	{
		private ProgressDialog dialog;
		String responseText="";
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

		}

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			StringBuilder stringBuilder =new StringBuilder();
			HttpParams httpParameters =new BasicHttpParams();
			
			DefaultHttpClient httpClient=new DefaultHttpClient();
			httpClient.setParams(httpParameters);
			HttpGet httpGet=new HttpGet(url);
			httpGet.setHeader("Cache-Control","no-cache");
			
			HttpResponse response = null;
			try {
				response = httpClient.execute(httpGet);
				StatusLine statusLine=response.getStatusLine();
				int statusCode=statusLine.getStatusCode();
				System.out.println("Status Code is=======>"+statusCode);
				
				if(statusCode==200)
				{
					HttpEntity entity=response.getEntity();
					InputStream inputStream =entity.getContent();
					BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));
					String line;
					while((line=reader.readLine())!=null)
					{
						stringBuilder.append(line);
					}
					inputStream.close();
					responseText=stringBuilder.toString();
					
				}
				
				System.out.println("Response==============>"+responseText);
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			return responseText;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
					
			if(!TextUtils.isEmpty(responseText))
			{
				JSONObject jObj=null;
					
				try {
					jObj=new JSONObject(responseText);
					
					weatherModel = gson.fromJson(jObj.toString(), WeatherModel.class);
					System.out.println("weatherModel==============>"+weatherModel);
					
					if(jObj.has("location"))
					{
					
						JSONObject locObj = jObj.getJSONObject("location");
					
						Location location = gson.fromJson(locObj.toString(), Location.class);

						System.out.println("Repository : location.getName()===========>"+location.name);
					}
					
					if(jObj.has("current"))
					{
					
						JSONObject curObj = jObj.getJSONObject("current");
					
						Current current = gson.fromJson(curObj.toString(), Current.class);
					
						System.out.println("Repository : current===========>"+current);

						System.out.println("Repository : current.temp_c===========>"+current.temp_c);
					}
					
					if(jObj.has("forecast"))
					{
					
						JSONObject forecastObj = jObj.getJSONObject("forecast");
					
						Forecast forecast = gson.fromJson(forecastObj.toString(), Forecast.class);
					
						System.out.println("Repository : forecast===========>"+forecast);

					}
				
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		
	}
	

}



