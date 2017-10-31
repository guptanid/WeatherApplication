package edu.uncc.weatherapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class PreferencesActivity extends AppCompatActivity implements View.OnClickListener {
    TextView txtChangeCity, txtChangeTempMetric;
    SharedPreferences preferences = null;
    AlertDialog alertDialogChangeCity = null;
    AlertDialog alertDialogChangeTempMetric = null;
    public static final String PREFS_NAME = "MyPrefsFile_New";
    public static final String Current_City_Code = "current_city";
    public static final String Current_Country_Code = "current_country";
    public static String Temp_Metric_Celsius = "temp_metric";
    boolean isCelsius = true;
    String tempUnitToastText ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        setTitle("Preferences");
        preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        txtChangeCity = (TextView) findViewById(R.id.txtChangeCity);
        txtChangeCity.setOnClickListener(this);
        txtChangeTempMetric = (TextView) findViewById(R.id.txtChangeTempMetric);
        txtChangeTempMetric.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == txtChangeCity.getId()) {
            ChangeCityListener();
        } else if (v.getId() == txtChangeTempMetric.getId()) {
            ChangeTempMetricListener();
        }
    }

    private void ChangeCityListener() {
        LayoutInflater li = LayoutInflater.from(PreferencesActivity.this);
        View changeCityPromptView = li.inflate(R.layout.change_city_prompt, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PreferencesActivity.this);
        alertDialogBuilder.setView(changeCityPromptView);
        final EditText userCity = (EditText) changeCityPromptView.findViewById(R.id.txtChangeCity);
        final EditText userCountry = (EditText) changeCityPromptView.findViewById(R.id.txtChangeCountry);
        final Button btnSetCity = (Button) changeCityPromptView.findViewById(R.id.btnSetCity);
        final Button btnCancelCity = (Button) changeCityPromptView.findViewById(R.id.btnCancelCity);

        // set dialog message
        alertDialogBuilder.setCancelable(false);
        alertDialogChangeCity = alertDialogBuilder.create();
        if (preferences != null && preferences.getString(Current_City_Code, null) != null && preferences.getString(Current_Country_Code, null) != null) {
            userCity.setText(preferences.getString(Current_City_Code, null));
            userCountry.setText(preferences.getString(Current_Country_Code, null));
            btnSetCity.setText("Update");
            alertDialogChangeCity.setTitle("Update city details");
        } else {
            btnSetCity.setText("Set");
            alertDialogChangeCity.setTitle("Enter city details");
        }
        alertDialogChangeCity.show();
        btnCancelCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogChangeCity.dismiss();
            }
        });
        btnSetCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strUserCity = userCity.getText().toString();
                String strUserCountry = userCountry.getText().toString();
                //code to add the city to Shared Preferences
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(Current_City_Code, strUserCity);
                editor.putString(Current_Country_Code, strUserCountry);
                editor.commit();
                alertDialogChangeCity.dismiss();
            }
        });
    }

    private void ChangeTempMetricListener() {
        LayoutInflater li = LayoutInflater.from(PreferencesActivity.this);
        View changeTempMetricPromptView = li.inflate(R.layout.change_temp_pref_prompt, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PreferencesActivity.this);
        alertDialogBuilder.setView(changeTempMetricPromptView);
        final RadioGroup rbGroupChangeTemp = (RadioGroup) changeTempMetricPromptView.findViewById(R.id.rbGroupChangeTemp);
        final RadioButton rbCelsius = (RadioButton) changeTempMetricPromptView.findViewById(R.id.rbCelcius);
        final RadioButton rbFahrenheit = (RadioButton) changeTempMetricPromptView.findViewById(R.id.rbFahrenheit);
        final Button btnDone = (Button) changeTempMetricPromptView.findViewById(R.id.btnTempSettingsDone);
        alertDialogBuilder.setCancelable(false);
        alertDialogChangeTempMetric = alertDialogBuilder.create();
        alertDialogChangeTempMetric.setTitle("Choose Temperature Unit");
        alertDialogChangeTempMetric.show();

        if (preferences != null && preferences.getBoolean(Temp_Metric_Celsius, true)) {
            rbCelsius.setChecked(true);
            rbFahrenheit.setChecked(false);

        } else {
            rbCelsius.setChecked(false);
            rbFahrenheit.setChecked(true);

        }
        rbGroupChangeTemp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbCelcius) {
                    isCelsius = true;
                    tempUnitToastText =getResources().getString(R.string.toastText_FtoC);
                } else if (checkedId == R.id.rbFahrenheit) {
                    isCelsius = false;
                    tempUnitToastText =getResources().getString(R.string.toastText_CtoF);
                }
            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(Temp_Metric_Celsius, isCelsius);
                editor.commit();
                alertDialogChangeTempMetric.dismiss();
                Toast.makeText(PreferencesActivity.this,tempUnitToastText,Toast.LENGTH_LONG).show();
            }
        });
    }
}

