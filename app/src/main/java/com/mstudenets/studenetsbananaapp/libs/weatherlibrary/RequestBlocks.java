package com.mstudenets.studenetsbananaapp.libs.weatherlibrary;

import android.text.TextUtils;

public class RequestBlocks {
	
	public static class MethodTypeParemeters
    {
     
        public static String getCurrent()
        {
        	return "/current.json";
        }
        
        public static String getForecast()
        {
        	return "/forecast.json";
        }

        public static String GetParameters(MethodType methodType) throws Exception
        {
            String methodPara = "";
            switch (methodType)
            {
                case Current:
                    methodPara = MethodTypeParemeters.getCurrent();
                    break;
                case Forecast:
                    methodPara = MethodTypeParemeters.getForecast();
                    break;

            }
            if (TextUtils.isEmpty(methodPara) || methodPara == null)
            {
                throw new Exception("Invalid Method Type");
            }
            else
            {
                return methodPara;

            }
        }


    }
	
	public enum MethodType
    {
        Current,
        Forecast
    }

    public enum Days
    {
        One,
        Two,
        Three,
        Four,
        Five,
        Six,
        Seven,
        Eight,
        Nine,
        Ten
    }

    public enum GetBy
    {
        CityName,
        Zip,
        PostCode,
        PostalCode,
        Metar,
        iata,
        IPAddress
    }
    
    public static class ReqestFor
    {
        public static String City(String cityName)
        {
            return "q=" + cityName;
        }

        public static String Zip(String zip)
        {
            return "q=" + zip;
        }

        public static String PostCode(String postcode)
        {
            return "q=" + postcode;
        }

        public static String PostalCode(String postalCode)
        {
            return "q=" + postalCode;
        }

        public static String LatLong(String latitude, String longitude)
        {
            return "q=" + latitude + "," + longitude;
        }

        public static String Metar(String metar)
        {
            return "q=metar:" + metar;
        }

        public static String iata(String iata)
        {
            return "q=iata:" + iata;
        }

        public static String AutoIP()
        {
            return "q=auto:ip";
        }

        public static String IPAddress(String IP)
        {
            return "q=" + IP;
        }

        public static String PrepareQueryParameter(GetBy getby, String value)
        {
            String queryParameter = "";
            switch (getby)
            {
                case CityName:
                    queryParameter = ReqestFor.City(value);
                    break;
                case Zip:
                    queryParameter = ReqestFor.Zip(value);
                    break;
                case PostCode:
                    queryParameter = ReqestFor.PostCode(value);
                    break;
                case PostalCode:
                    queryParameter = ReqestFor.PostalCode(value);
                    break;
                case Metar:
                    queryParameter = ReqestFor.Metar(value);
                    break;
                case iata:
                    queryParameter = ReqestFor.iata(value);
                    break;
                case IPAddress:
                    queryParameter = ReqestFor.IPAddress(value);
                    break;
            }
            return queryParameter;
        }
        public static String PrepareDays(Days days)
        {
            return "days=" + days;
        }


    }
    

}
