package com.hidesign.hiweather.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hidesign.hiweather.R
import com.hidesign.hiweather.model.Hourly
import com.hidesign.hiweather.model.WeatherIcon
import com.hidesign.hiweather.util.DateUtils
import org.w3c.dom.Text
import java.text.MessageFormat
import kotlin.collections.ArrayList

class HourlyRecyclerAdapter internal constructor(context: Context?, weathers: ArrayList<Hourly>) : RecyclerView.Adapter<HourlyRecyclerAdapter.ViewHolder>() {
    private val weatherArrayList: ArrayList<Hourly>
    private val mInflater: LayoutInflater

    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.hourly_forecast_item, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the view and textview in each row
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.date.text = DateUtils.getDateTime("HH:00", weatherArrayList[position].dt.toLong())
        holder.temp.text = MessageFormat.format("Temperature {0}°C", weatherArrayList[position].temp)
        holder.precipitation.text = MessageFormat.format("{0}% Chance of Rain",  weatherArrayList[position].pop)
        holder.icon.setImageResource(WeatherIcon.getIcon(weatherArrayList[position].weather[0].id))
    }

    // total number of rows
    override fun getItemCount(): Int {
        return weatherArrayList.size
    }

    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var date: TextView
        var temp: TextView
        var precipitation: TextView
        var icon: ImageView

        init {
            date = itemView.findViewById(R.id.date)
            temp = itemView.findViewById(R.id.temp)
            precipitation = itemView.findViewById(R.id.Precipitation)
            icon = itemView.findViewById(R.id.skiesImage)
        }
    }

    // data is passed into the constructor
    init {
        mInflater = LayoutInflater.from(context)
        weatherArrayList = weathers
    }
}