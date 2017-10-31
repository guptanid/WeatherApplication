package edu.uncc.weatherapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by rujut on 4/5/2017.
 */

public class LocationAPIUtil {
    static public class LocationAPIParser {
        static String parseLocationAPI(String in) throws JSONException {
            JSONArray rootArray=new JSONArray(in);
            JSONObject firstObj = rootArray.getJSONObject(0);
            String key = firstObj.getString("Key");
            Log.d("demo", "City Key   " + key);
            return key;
        }
    }
}
