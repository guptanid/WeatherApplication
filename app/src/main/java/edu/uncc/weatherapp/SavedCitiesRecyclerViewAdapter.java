package edu.uncc.weatherapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.IntegerRes;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

/**
 * Created by NidhiGupta on 4/8/2017.
 */

public class SavedCitiesRecyclerViewAdapter extends RecyclerView.Adapter<SavedCitiesRecyclerViewAdapter.ViewHolder> {
    ArrayList<CityDetails> m_PList = null;
    Context m_Context = null;
    SharedPreferences preferences = null;
    private DatabaseReference mDatabaseRef;

    public SavedCitiesRecyclerViewAdapter(ArrayList<CityDetails> list, Context context) {
        m_PList = list;
        m_Context = context;
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        preferences = m_Context.getSharedPreferences(PreferencesActivity.PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View mView;

        public ViewHolder(View v) {
            super(v);
            mView = v;
        }
    }

    @Override
    public SavedCitiesRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SavedCitiesRecyclerViewAdapter.ViewHolder vh = null;
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.searched_cities_layout, parent, false);
        vh = new SavedCitiesRecyclerViewAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(SavedCitiesRecyclerViewAdapter.ViewHolder holder, final int position) {
        final CityDetails details = m_PList.get(position);
        View view = holder.mView;
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String key = m_PList.get(position).getCityKey();
                mDatabaseRef.child("citydetails").child(key).removeValue();
                return false;
            }
        });
        TextView txtSavedCityCountry = (TextView) view.findViewById(R.id.txtSavedCityCountry);
        TextView txtSavedTemp = (TextView) view.findViewById(R.id.txtSavedTemp);
        TextView txtSavedLastUpdated = (TextView) view.findViewById(R.id.txtSavedLastUpdated);
        final ImageButton imgSavedFav = (ImageButton) view.findViewById(R.id.imgSavedFav);

        txtSavedCityCountry.setText(details.getCityName() + ", " + details.getCountry());
        String temperature = details.getTemperature();
        if (preferences != null
                && preferences.getBoolean(PreferencesActivity.Temp_Metric_Celsius, true)) {
            txtSavedTemp.setText("Temperature: " + details.getTemperature() + "° C");
        } else {
            float tempInF = (float) (1.8 * Float.valueOf(temperature) + 32);
            txtSavedTemp.setText("Temperature: " + tempInF + "° F");
        }

        String lastUpdatedOn = details.getUpdatedOn();
        PrettyTime p = new PrettyTime(Locale.US);
        String formattedTimeAgo = null;
        try {
            formattedTimeAgo = p.format(new SimpleDateFormat("yyyyMMdd_HHmmss").parse(lastUpdatedOn));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        txtSavedLastUpdated.setText("Updated " + formattedTimeAgo);
        if (details.getFavourite().equals(true)) {
            imgSavedFav.setImageResource(R.drawable.star_gold);
        } else {
            imgSavedFav.setImageResource(R.drawable.star_gray);
        }

        mDatabaseRef.child("citydetails").child(details.getCityKey()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
//                            cityExitsInDB = true;
                        } else {
//                            cityExitsInDB = false;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

        imgSavedFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //write code to update fire base with changed Favorite column value.
                if (m_PList.get(position).getFavourite()) {
                    imgSavedFav.setImageResource(R.drawable.star_gray);
                    mDatabaseRef.child("citydetails").child(details.getCityKey()).child("favourite").setValue(false);
                    m_PList.get(position).setFavourite(false);
                    Collections.sort(m_PList, new Comparator<CityDetails>() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public int compare(CityDetails o1, CityDetails o2) {
                            return Boolean.compare(o2.getFavourite(), o1.getFavourite());
                        }
                    });
                } else {
                    imgSavedFav.setImageResource(R.drawable.star_gold);
                    mDatabaseRef.child("citydetails").child(details.getCityKey()).child("favourite").setValue(true);
                    m_PList.get(position).setFavourite(true);
                    Collections.sort(m_PList, new Comparator<CityDetails>() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public int compare(CityDetails o1, CityDetails o2) {
                            return Boolean.compare(o2.getFavourite(), o1.getFavourite());
                        }
                    });
                }


            }
        });
    }

    @Override
    public int getItemCount() {
        return m_PList.size();
    }
}
