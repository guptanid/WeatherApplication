package edu.uncc.weatherapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CityWeatherActivity extends AppCompatActivity {
    SharedPreferences preferences = null;
    private String currentSearchedCity, currentSearchedCountry;
    public static String cityKey;
    public ArrayList<DailyForecast> dailyForecastArrayList;
    TextView headlineText, titleText, forecastTextView, forecastText, dayText, nightText;
    TextView clickMoreDetails, clickExtendedForecast;
    ImageView dayImage, nightImage;
    RecyclerView forecastRecyclerView;
    Headline headline;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    String curreMobileLink;
    private DatabaseReference mDatabaseRef;
    DailyForecast m_DailyForecast;
    boolean cityExitsInDB = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_weather);
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Loading Data) ");
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setIndeterminate(true);
        FirebaseApp.initializeApp(this);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        headlineText = (TextView) findViewById(R.id.textViewHeadtext);
        titleText = (TextView) findViewById(R.id.textViewTitle);
        forecastTextView = (TextView) findViewById(R.id.textViewForecast);
        forecastText = (TextView) findViewById(R.id.textViewForcastText);
        dayText = (TextView) findViewById(R.id.textViewForecastDay);
        nightText = (TextView) findViewById(R.id.textViewForecastNight);


        clickMoreDetails = (TextView) findViewById(R.id.textViewMoreDetails);
        clickMoreDetails.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(curreMobileLink));
                startActivity(intent);
                return true;
            }
        });

        clickExtendedForecast = (TextView) findViewById(R.id.textViewExtendedForecast);
        clickExtendedForecast.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(headline.getMobileLink()));
                startActivity(intent);
                return true;
            }
        });

        dayImage = (ImageView) findViewById(R.id.imageViewDay);
        nightImage = (ImageView) findViewById(R.id.imageViewNight);

        forecastRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewForecasts);

        if (getIntent().getExtras() != null && getIntent().getExtras().get("Country") != null && getIntent().getExtras().get("CityName") != null) {

            currentSearchedCity = getIntent().getExtras().get("CityName").toString();
            currentSearchedCountry = getIntent().getExtras().get("Country").toString();
            preferences = getSharedPreferences(PreferencesActivity.PREFS_NAME, Context.MODE_PRIVATE);
            final boolean isCelciusMetric = preferences.getBoolean(PreferencesActivity.Temp_Metric_Celsius, true);
            if (isConnectedOnline()) {

                titleText.setText("Daily forecast for " + currentSearchedCity + "," + currentSearchedCountry);
                String apiUrl = ParserUtility.getLocationApiUrl(currentSearchedCity, currentSearchedCountry, getResources().getString(R.string.api_key));
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(apiUrl.toString())
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        CityWeatherActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progress.setProgress(0);
                                Toast.makeText(CityWeatherActivity.this, "City not found", Toast.LENGTH_SHORT).show();
                                Intent MainI = new Intent(CityWeatherActivity.this, MainActivity.class);
                                startActivity(MainI);
                            }
                        });

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String jsonString = response.body().string();
                        try {
                            Log.d("demo", "Onresponse1  " + jsonString);
                            progress.setProgress(0);
                            cityKey = LocationAPIUtil.LocationAPIParser.parseLocationAPI(jsonString);

                            mDatabaseRef.child("citydetails").child(cityKey).addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.getValue() != null) {
                                                cityExitsInDB = true;
                                            } else {
                                                cityExitsInDB = false;
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    }
                            );


                            StringBuilder day5APi = new StringBuilder();
                            day5APi.append("http://dataservice.accuweather.com/forecasts/v1/daily/5day/");
                            day5APi.append(cityKey);
                            day5APi.append("?apikey=");
                            day5APi.append(getResources().getString(R.string.api_key));
                            Log.d("demo", "5day api :: " + day5APi.toString());
                            OkHttpClient day5Client = new OkHttpClient();
                            Request newrequest = new Request.Builder()
                                    .url(day5APi.toString())
                                    .build();
                            day5Client.newCall(newrequest).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    CityWeatherActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progress.setProgress(0);
                                            Toast.makeText(CityWeatherActivity.this, "City not found", Toast.LENGTH_SHORT).show();
                                            Intent MainI = new Intent(CityWeatherActivity.this, MainActivity.class);
                                            startActivity(MainI);
                                        }
                                    });

                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    final String newjsonString = response.body().string();
                                    CityWeatherActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.d("demo", "Onresponse2");
                                            try {
                                                headline = Day5Utility.HeadlineParser.parseHeadline(newjsonString);
                                                headlineText.setText(headline.getText());


                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            dailyForecastArrayList = new ArrayList<DailyForecast>();


                                            try {
                                                dailyForecastArrayList = Day5Utility.DailyForecastParser.parseDailyForecasts(newjsonString);
                                                DailyForecast day1 = dailyForecastArrayList.get(0);
                                                m_DailyForecast = day1;

                                                curreMobileLink = day1.getMobileLink();

                                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                                Date dateObj = dateFormat.parse(day1.getDate().substring(0, 10));
                                                Log.d("demo", dateObj.toString());

                                                SimpleDateFormat formated = new SimpleDateFormat("MMM dd, yyyy");

                                                String date = formated.format(dateObj);
                                                forecastTextView.setText("Forecast on " + date);

                                                if (!isCelciusMetric && day1.getMaxUnit().equals("F")) {
                                                    forecastText.setText("Temperature: " + day1.getTemperatureMaxF() + (char) 0x00B0 + " / " + day1.getTemperatureMinF() + (char) 0x00B0);
                                                } else if (isCelciusMetric && day1.getMaxUnit().equals("F")) {
                                                    Double maxTemp = (Double.valueOf(day1.getTemperatureMaxF()) - 32) / 1.8;
                                                    Double minTemp = (Double.valueOf(day1.getTemperatureMinF()) - 32) / 1.8;
                                                    forecastText.setText("Temperature: " + String.valueOf(Math.round(maxTemp * 100) / 100) + (char) 0x00B0 + " / " + String.valueOf(Math.round(minTemp * 100) / 100) + (char) 0x00B0);
                                                } else if (!isCelciusMetric && day1.getMaxUnit().equals("C")) {
                                                    Double maxTemp = (Double.valueOf(day1.getTemperatureMaxF()) * 1.8) + 32;
                                                    Double minTemp = (Double.valueOf(day1.getTemperatureMinF()) * 1.8) + 32;
                                                    forecastText.setText("Temperature: " + String.valueOf(Math.round(maxTemp * 100) / 100) + (char) 0x00B0 + " / " + String.valueOf(Math.round(minTemp * 100) / 100) + (char) 0x00B0);
                                                } else if (isCelciusMetric && day1.getMaxUnit().equals("C")) {
                                                    forecastText.setText("Temperature: " + day1.getTemperatureMaxF() + (char) 0x00B0 + " / " + day1.getTemperatureMinF() + (char) 0x00B0);
                                                }

                                                loadImages(day1.getDayIconID().trim(), true);
                                                loadImages(day1.getDayIconID().trim(), false);

                                                dayText.setText(day1.getDayIcon());
                                                nightText.setText(day1.getNightIcon());

                                                createRecyclerViewDisplay();

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    });
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

            } else {
                Toast.makeText(CityWeatherActivity.this, "No internet connection", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean isConnectedOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.weather_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_city:
                CityDetails m_Details = new CityDetails();
                m_Details.setCityKey(cityKey);
                m_Details.setCityName(currentSearchedCity);
                m_Details.setCountry(currentSearchedCountry);
                m_Details.setTemperature(m_DailyForecast.getTemperatureMaxF());
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
                m_Details.setUpdatedOn(timeStamp);

                if (!cityExitsInDB) {
                    m_Details.setFavourite(false); // first time set it to false
                    mDatabaseRef.child("citydetails").child(cityKey).setValue(m_Details);
                    Toast.makeText(getApplicationContext(), "City Saved.", Toast.LENGTH_LONG).show();
                    cityExitsInDB = true;
                } else {
                    // set value for temp only.
                    mDatabaseRef.child("citydetails").child(cityKey).child("temperature").setValue(m_Details.getTemperature());
                    mDatabaseRef.child("citydetails").child(cityKey).child("updatedOn").setValue(m_Details.getUpdatedOn());
                    Toast.makeText(getApplicationContext(), "City Updated.", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.set_curr_city:
                String mCurrentCity = preferences.getString(PreferencesActivity.Current_City_Code, null);
                String mCurrentCountry = preferences.getString(PreferencesActivity.Current_Country_Code, null);
                if (!mCurrentCity.isEmpty() && !mCurrentCountry.isEmpty()
                        && mCurrentCity.equals(currentSearchedCity) && mCurrentCountry.equals(currentSearchedCountry)) {
                    Toast.makeText(getApplicationContext(), "Current City Updated.", Toast.LENGTH_LONG).show();
                } else {
                    SharedPreferences.Editor editor;
                    editor = preferences.edit();
                    editor.putString(PreferencesActivity.Current_Country_Code, currentSearchedCountry);
                    editor.putString(PreferencesActivity.Current_City_Code, currentSearchedCity);
                    editor.commit();
                    Toast.makeText(getApplicationContext(), "Current City Saved.", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.settings:
                Intent intent = new Intent(CityWeatherActivity.this, PreferencesActivity.class);
                startActivity(intent);
                break;
        }
        return false;
    }

    //true- day false- night
    public void loadImages(String id, boolean isDay) {
        StringBuilder imageAPi = new StringBuilder();
        imageAPi.append("http://developer.accuweather.com/sites/default/files/");
        if (id.length() == 1) {
            StringBuilder idStr = new StringBuilder();
            idStr.append("0");
            idStr.append(id);
            imageAPi.append(idStr.toString());
        } else {
            imageAPi.append(id);
        }

        imageAPi.append("-s.png");


        if (isDay) {
            Log.d("demo", "Image api day: " + imageAPi.toString());
            Picasso.with(getApplicationContext())
                    .load(imageAPi.toString())
                    .resize(80, 80)
                    .centerCrop()
                    .into(dayImage);
        } else {
            Log.d("demo", "Image api night: " + imageAPi.toString());
            Picasso.with(getApplicationContext())
                    .load(imageAPi.toString())
                    .resize(80, 80)
                    .centerCrop()
                    .into(nightImage);
        }
    }

    public void createRecyclerViewDisplay() {

        //List view style
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        forecastRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new RecyclerviewListAdapter(dailyForecastArrayList, this.getBaseContext());
        ((RecyclerviewListAdapter) mAdapter).setOnItemClickListener(new RecyclerviewListAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.d("demo", "Click: " + position);
                recyclerViewOnClick(position);
            }
        });
        forecastRecyclerView.setAdapter(mAdapter);

    }

    public void recyclerViewOnClick(int position) {
        DailyForecast details = dailyForecastArrayList.get(position);
        m_DailyForecast = details;

        curreMobileLink = details.getMobileLink();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dateObj = null;
        try {
            dateObj = dateFormat.parse(details.getDate().substring(0, 10));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d("demo", dateObj.toString());

        SimpleDateFormat formated = new SimpleDateFormat("MMM dd, yyyy");

        String date = formated.format(dateObj);
        forecastTextView.setText("Forecast on " + date);
//        forecastText.setText("Temperature: " + details.getTemperatureMaxF() + (char) 0x00B0 + " / " + details.getTemperatureMinF() + (char) 0x00B0);

        final boolean isCelciusMetric = preferences.getBoolean(PreferencesActivity.Temp_Metric_Celsius, true);
        if (!isCelciusMetric && details.getMaxUnit().equals("F")) {
            forecastText.setText("Temperature: " + details.getTemperatureMaxF() + (char) 0x00B0 + " / " + details.getTemperatureMinF() + (char) 0x00B0);
        } else if (isCelciusMetric && details.getMaxUnit().equals("F")) {
            Double maxTemp = (Double.valueOf(details.getTemperatureMaxF()) - 32) / 1.8;
            Double minTemp = (Double.valueOf(details.getTemperatureMinF()) - 32) / 1.8;
            forecastText.setText("Temperature: " + String.valueOf(Math.round(maxTemp * 100) / 100) + (char) 0x00B0 + " / " + String.valueOf(Math.round(minTemp * 100) / 100) + (char) 0x00B0);
        } else if (!isCelciusMetric && details.getMaxUnit().equals("C")) {
            Double maxTemp = (Double.valueOf(details.getTemperatureMaxF()) * 1.8) + 32;
            Double minTemp = (Double.valueOf(details.getTemperatureMinF()) * 1.8) + 32;
            forecastText.setText("Temperature: " + String.valueOf(Math.round(maxTemp * 100) / 100) + (char) 0x00B0 + " / " + String.valueOf(Math.round(minTemp * 100) / 100) + (char) 0x00B0);
        } else if (isCelciusMetric && details.getMaxUnit().equals("C")) {
            forecastText.setText("Temperature: " + details.getTemperatureMaxF() + (char) 0x00B0 + " / " + details.getTemperatureMinF() + (char) 0x00B0);
        }
        loadImages(details.getDayIconID().trim(), true);
        loadImages(details.getDayIconID().trim(), false);

        dayText.setText(details.getDayIcon());
        nightText.setText(details.getNightIcon());
    }


}
