package edu.uncc.weatherapp;

import android.os.AsyncTask;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by NidhiGupta on 4/2/2017.
 */

public class GetLocationDetailsAsyncTask extends AsyncTask<String, Void, CurrentLocationWeatherModel> {
    MainActivity activity;

    public GetLocationDetailsAsyncTask(MainActivity activity){
        this.activity=activity;
    }
    interface IGetActivity{
        public void SetUpLocationData(CurrentLocationWeatherModel currentLocationWeatherModels);
    }
    @Override
    protected void onPostExecute(CurrentLocationWeatherModel currentLocationWeatherModel) {
        activity.SetUpLocationData(currentLocationWeatherModel);
    }

    @Override
    protected CurrentLocationWeatherModel doInBackground(String... params) {
        String city=params[0];
        String countryCode=params[1];
        String apiKey=activity.getResources().getString(R.string.api_key);
        CurrentLocationWeatherModel currentLocationWeatherModel=null;
        try {
           String apiUrl= ParserUtility.getLocationApiUrl(city,countryCode,apiKey);
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
                int locationKey= ParserUtility.JSONParser.jsonParserMethod(sb.toString());
                if(locationKey==-1){
                    currentLocationWeatherModel=null;
                }else {
                    currentLocationWeatherModel=new CurrentLocationWeatherModel();
                    currentLocationWeatherModel.setLocationId(locationKey);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return currentLocationWeatherModel;
    }
}
