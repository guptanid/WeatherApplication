package edu.uncc.weatherapp;

import android.os.AsyncTask;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;

/**
 * Created by NidhiGupta on 4/7/2017.
 */

public class GetCurrentCityWeatherAsycTask extends AsyncTask<CurrentLocationWeatherModel,Void,CurrentLocationWeatherModel> {
    MainActivity activity;
    public GetCurrentCityWeatherAsycTask(MainActivity activity){
        this.activity=activity;
    }
    interface IGetActivity{
        public void SetUpCurrentCityWeatherData(CurrentLocationWeatherModel currentLocationWeatherModels);
    }
    @Override
    protected CurrentLocationWeatherModel doInBackground(CurrentLocationWeatherModel... params) {
        CurrentLocationWeatherModel currentLocationWeatherModel=null;
        int cityId=params[0].getLocationId();
        String apiKey=activity.getResources().getString(R.string.api_key);
        try {
            String apiUrl= ParserUtility.getCurrentForecastForCurrentLocationApiUrl(cityId,apiKey);
            URL url=new URL(apiUrl);
            HttpURLConnection con=(HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int statusCode = con.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = bufferedReader.readLine();
                while (line != null) {
                    sb.append(line);
                    line = bufferedReader.readLine();
                }
                currentLocationWeatherModel= ParserUtility.JSONParser.weatherForecastJsonParserMethod(sb.toString());
                currentLocationWeatherModel.setLocationId(cityId);
                return currentLocationWeatherModel;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return currentLocationWeatherModel;
    }

    @Override
    protected void onPostExecute(CurrentLocationWeatherModel currentLocationWeatherModel) {
       activity.SetUpCurrentCityWeatherData(currentLocationWeatherModel);
    }


}
