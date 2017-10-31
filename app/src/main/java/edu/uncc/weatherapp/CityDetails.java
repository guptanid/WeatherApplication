package edu.uncc.weatherapp;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.firebase.database.Exclude;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rujut on 4/7/2017.
 */

public class CityDetails {
    String cityKey, cityName, country, temperature, updatedOn;
    Boolean favourite;

    public String getCityKey() {
        return cityKey;
    }

    public void setCityKey(String cityKey) {
        this.cityKey = cityKey;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public Boolean getFavourite() {
        return favourite;
    }

    public void setFavourite(Boolean favourite) {
        this.favourite = favourite;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }

    @Override
    public String toString() {
        return "CityDetails{" +
                "cityKey='" + cityKey + '\'' +
                ", cityName='" + cityName + '\'' +
                ", country='" + country + '\'' +
                ", temperature='" + temperature + '\'' +
                ", updatedOn='" + updatedOn + '\'' +
                ", favourite=" + favourite +
                '}';
    }

    //@Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("cityKey", cityKey);
        result.put("cityName", cityName);
        result.put("country", country);
        result.put("temperature", temperature);
        result.put("favourite", favourite);
        result.put("updatedOn", updatedOn);

        return result;
    }
}
