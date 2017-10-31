package edu.uncc.weatherapp;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by rujut on 3/18/2017.
 */

public class RecyclerviewListAdapter extends RecyclerView.Adapter<RecyclerviewListAdapter.ViewHolder> {

    ArrayList<DailyForecast> m_PList = null;
    Context m_Context = null;
    private static MyClickListener myClickListener;

      @Override
    public RecyclerviewListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder vh = null;
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.weather_layout, parent, false);
        vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerviewListAdapter.ViewHolder holder, final int position) {
        DailyForecast details = m_PList.get(position);

        View view = holder.mView;
        TextView forecastText = (TextView) view.findViewById(R.id.textViewForecastRecycler);
        ImageView weatherImage = (ImageView) view.findViewById(R.id.imageViewWeather);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dateObj = null;
        try {
            dateObj = dateFormat.parse(details.getDate().substring(0,10));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d("demo", dateObj.toString());

        SimpleDateFormat formated = new SimpleDateFormat("MMM dd, yyyy");

        String date = formated.format(dateObj);

        forecastText.setText(date);
        StringBuilder imageAPi = new StringBuilder();
        imageAPi.append("http://developer.accuweather.com/sites/default/files/");
        if(details.getDayIconID().length() == 1){
            StringBuilder idStr = new StringBuilder();
            idStr.append("0");
            idStr.append(details.getDayIconID());
            imageAPi.append(idStr);
        }
        else {
            imageAPi.append(details.getDayIconID());
        }

        imageAPi.append("-s.png");
        Log.d("demo", "Image api recycler view: " + imageAPi.toString());
        Picasso.with(m_Context)
                .load(imageAPi.toString())
                .resize(80, 80)
                .centerCrop()
                .into(weatherImage);


        view.setFocusable(true);
    }

    @Override
    public int getItemCount() {
        return m_PList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View
            .OnClickListener  {

        // each data item is just a string in this case
        public View mView;
        public ViewHolder(View v) {

            super(v);
            mView = v;
            Log.i("demo", "Adding Listener");
            mView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            Log.d("demo", "OnClick");
            myClickListener.onItemClick(getAdapterPosition());
        }
    }


    public RecyclerviewListAdapter(ArrayList<DailyForecast> list, Context context){
        m_PList = list;
        m_Context = context;
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public interface MyClickListener {
        public void onItemClick(int position);
    }


}
