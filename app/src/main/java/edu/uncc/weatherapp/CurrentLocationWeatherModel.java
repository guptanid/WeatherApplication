package edu.uncc.weatherapp;

import java.util.Date;

/**
 * Created by NidhiGupta on 4/2/2017.
 */

public class CurrentLocationWeatherModel {
    String weatherText, celsiusTemperature, fahrenheitTemperature;
    String localDateTime,weatherIconUrl;
    int  locationId;

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getWeatherText() {
        return weatherText;
    }

    public void setWeatherText(String weatherText) {
        this.weatherText = weatherText;
    }

    public String getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(String localDateTime) {
        this.localDateTime = localDateTime;
    }

    public String getWeatherIconUrl() {
        return weatherIconUrl;
    }

    public void setWeatherIconUrl(String weatherIconUrl) {
        this.weatherIconUrl = weatherIconUrl;
    }

    public String getCelsiusTemperature() {
        return celsiusTemperature;
    }

    public void setCelsiusTemperature(String celsiusTemperature) {
        this.celsiusTemperature = celsiusTemperature;
    }

    public String getFahrenheitTemperature() {
        return fahrenheitTemperature;
    }

    public void setFahrenheitTemperature(String fahrenheitTemperature) {
        this.fahrenheitTemperature = fahrenheitTemperature;
    }
}
