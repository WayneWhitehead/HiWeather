package com.hidesign.hiweather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hidesign.hiweather.Models.Weather;

import java.text.MessageFormat;
import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<Weather> weatherArrayList;
    private LayoutInflater mInflater;

    // data is passed into the constructor
    RecyclerViewAdapter(Context context, ArrayList<Weather> weathers) {
        this.mInflater = LayoutInflater.from(context);
        weatherArrayList = weathers;
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.forecast_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String date = weatherArrayList.get(position).getDate();
        double high = weatherArrayList.get(position).getHighTemp();
        double low = weatherArrayList.get(position).getLowTemp();
        int precip = weatherArrayList.get(position).getPrecipitation();
        int icon = weatherArrayList.get(position).getIcon();
        String [] temp = date.split("T");
        holder.Date.setText(temp[0]);
        holder.High.setText(MessageFormat.format("High {0}", high));
        holder.Low.setText(MessageFormat.format("Low {0}", low));
        holder.Precipitation.setText(MessageFormat.format("{0}% Chance of Rain", precip));

        if (icon > 0 && icon < 6) {
            holder.Icon.setImageResource(R.drawable.sun);
        } else if (icon > 5 && icon < 12) {
            holder.Icon.setImageResource(R.drawable.overcast);
        } else if ( icon > 11 && icon < 19) {
            holder.Icon.setImageResource(R.drawable.rain);
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return weatherArrayList.size();
    }

    // stores and recycles views as they are scrolled off screen
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView Date, High, Low, Precipitation;
        ImageView Icon;

        ViewHolder(View itemView) {
            super(itemView);
            Date = itemView.findViewById(R.id.date);
            High = itemView.findViewById(R.id.HighTemp);
            Low = itemView.findViewById(R.id.LowTemp);
            Precipitation = itemView.findViewById(R.id.Precipitation);
            Icon = itemView.findViewById(R.id.skiesImage);
        }
    }
}
