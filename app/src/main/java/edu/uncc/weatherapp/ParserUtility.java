package edu.uncc.weatherapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.format.SimpleTimeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by NidhiGupta on 4/2/2017.
 */

public class ParserUtility {
    static public class JSONParser {
        static int jsonParserMethod(String in) throws JSONException {
            CurrentLocationWeatherModel model = new CurrentLocationWeatherModel();
            JSONArray rootArray = new JSONArray(in);
            if (rootArray != null) {
                JSONObject object = rootArray.getJSONObject(0);
                int locationKey = object.getInt("Key");
                return locationKey;
            }
            return -1;
        }

        static CurrentLocationWeatherModel weatherForecastJsonParserMethod(String in) throws JSONException, ParseException {
            CurrentLocationWeatherModel model = new CurrentLocationWeatherModel();
            JSONArray rootArray = new JSONArray(in);
            if (rootArray != null) {
                JSONObject object = rootArray.getJSONObject(0);
                String localObservationDateTime = object.getString("LocalObservationDateTime");
                localObservationDateTime = localObservationDateTime.substring(0, localObservationDateTime.length() - 6);
                PrettyTime p = new PrettyTime(Locale.US);
                String formattedTimeAgo = p.format(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(localObservationDateTime));
                model.setLocalDateTime("Updated "+formattedTimeAgo);
                String weatherText = object.getString("WeatherText");
                model.setWeatherText(weatherText);
                int weatherIcon = object.getInt("WeatherIcon");
                if (weatherIcon <= 9)
                    model.setWeatherIconUrl(String.format("http://developer.accuweather.com/sites/default/files/0%s-s.png", String.valueOf(weatherIcon)));
                else
                    model.setWeatherIconUrl(String.format("http://developer.accuweather.com/sites/default/files/%s-s.png", String.valueOf(weatherIcon)));
                JSONObject temperature = object.getJSONObject("Temperature");
                JSONObject metric = temperature.getJSONObject("Metric");
                String tempCelsius="Temperature : "+metric.getString("Value")+"\u00B0 "+metric.getString("Unit");
                model.setCelsiusTemperature(tempCelsius);
                JSONObject imperial = temperature.getJSONObject("Imperial");
                String tempFahrenheit="Temperature : "+imperial.getString("Value")+"\u00B0 "+imperial.getString("Unit");
                model.setFahrenheitTemperature(tempFahrenheit);
                return model;
            }
            return null;
        }
    }

    static public String getLocationApiUrl(String cityName, String countryCode, String apiKey) {
        String url = String.format("http://dataservice.accuweather.com/locations/v1/%s/search?apikey=%s&q=%s", countryCode, apiKey, cityName);
        return url;
    }

    static public String getCurrentForecastForCurrentLocationApiUrl(int locationKey, String apiKey) {
        String url = String.format("http://dataservice.accuweather.com/currentconditions/v1/%d?apikey=%s", locationKey, apiKey);
        return url;
    }
}
