package edu.uncc.weatherapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by rujut on 4/5/2017.
 */

public class Day5Utility {
    static public class HeadlineParser {
        static Headline parseHeadline(String in) throws JSONException {
            JSONObject root = new JSONObject(in);
            JSONObject headLineObj = root.getJSONObject("Headline");
            Headline headline = new Headline();
            headline.setEffectiveDate(headLineObj.getString("EffectiveDate"));
            headline.setSeverity(Integer.valueOf(headLineObj.getString("Severity")));
            headline.setText(headLineObj.getString("Text"));
            headline.setCategory(headLineObj.getString("Category"));
            headline.setEndDate(headLineObj.getString("EndDate"));
            headline.setMobileLink(headLineObj.getString("MobileLink"));
            headline.setLink(headLineObj.getString("Link"));
            return headline;
        }
    }
    static public class DailyForecastParser{
        static ArrayList<DailyForecast> parseDailyForecasts(String in) throws JSONException {
            ArrayList<DailyForecast> forecastList = new ArrayList<DailyForecast>();
            JSONObject root = new JSONObject(in);
            JSONArray dailyForecastArray = root.getJSONArray("DailyForecasts");

            for (int i = 0; i < dailyForecastArray.length(); i++) {
                JSONObject dailyObj = dailyForecastArray.getJSONObject(i);

                DailyForecast dailyForecast = new DailyForecast();

                dailyForecast.setDate(dailyObj.getString("Date").trim());

                JSONObject tempObj = dailyObj.getJSONObject("Temperature");

                JSONObject minObj = tempObj.getJSONObject("Minimum");
                dailyForecast.setTemperatureMinF(minObj.getString("Value").trim());
                dailyForecast.setMinUnit(minObj.getString("Unit").trim());

                JSONObject maxObj = tempObj.getJSONObject("Maximum");
                dailyForecast.setTemperatureMaxF(maxObj.getString("Value").trim());
                dailyForecast.setMaxUnit(maxObj.getString("Unit").trim());

                JSONObject dayObj = dailyObj.getJSONObject("Day");
                dailyForecast.setDayIconID(dayObj.getString("Icon").trim());
                dailyForecast.setDayIcon(dayObj.getString("IconPhrase").trim());

                JSONObject nightObj = dailyObj.getJSONObject("Night");
                dailyForecast.setNightIconID(nightObj.getString("Icon").trim());
                dailyForecast.setNightIcon(nightObj.getString("IconPhrase").trim());

                dailyForecast.setMobileLink(dailyObj.getString("MobileLink").trim());

                dailyForecast.setLink(dailyObj.getString("Link").trim());

                forecastList.add(dailyForecast);
            }
            return forecastList;
        }
    }
}
