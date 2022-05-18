package com.hidesign.hiweather.adapter

import android.content.Context
import com.hidesign.hiweather.models.DailyForecast
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.hidesign.hiweather.R
import java.text.MessageFormat
import java.util.ArrayList

class RecyclerViewAdapter internal constructor(context: Context?, weathers: ArrayList<DailyForecast>) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {
    private val weatherArrayList: ArrayList<DailyForecast>
    private val mInflater: LayoutInflater

    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.forecast_item, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the view and textview in each row
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val date = weatherArrayList[position].date.split("T").toTypedArray()
        holder.date.text = date[0]
        holder.high.text = MessageFormat.format("High {0}", weatherArrayList[position].temperature.maximum.value.toString())
        holder.low.text = MessageFormat.format("Low {0}", weatherArrayList[position].temperature.minimum.value.toString())
        holder.precipitation.text = MessageFormat.format("{0}% Chance of Rain",  weatherArrayList[position].day.precipitationProbability)
        when (weatherArrayList[position].day.icon) {
            in 1..5 -> {
                holder.icon.setImageResource(R.drawable.sun)
            }
            in 6..11 -> {
                holder.icon.setImageResource(R.drawable.overcast)
            }
            in 12..18 -> {
                holder.icon.setImageResource(R.drawable.rain)
            }
        }
    }

    // total number of rows
    override fun getItemCount(): Int {
        return weatherArrayList.size
    }

    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var date: TextView
        var high: TextView
        var low: TextView
        var precipitation: TextView
        var icon: ImageView

        init {
            date = itemView.findViewById(R.id.date)
            high = itemView.findViewById(R.id.HighTemp)
            low = itemView.findViewById(R.id.LowTemp)
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