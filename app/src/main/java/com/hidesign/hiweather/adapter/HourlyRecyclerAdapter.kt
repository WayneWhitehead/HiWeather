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
import kotlin.math.roundToInt

class HourlyRecyclerAdapter internal constructor(context: Context?, weathers: ArrayList<Hourly>) : RecyclerView.Adapter<HourlyRecyclerAdapter.ViewHolder>() {
    private val weatherArrayList: ArrayList<Hourly>
    private val mInflater: LayoutInflater

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.hourly_forecast_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.date.text = DateUtils.getDateTime("HH:00", weatherArrayList[position].dt.toLong())
        holder.temp.text = MessageFormat.format("{0}°C", weatherArrayList[position].temp.roundToInt())
        holder.precipitation.text = MessageFormat.format("{0}%",  weatherArrayList[position].pop * 100)
        holder.icon.setImageResource(WeatherIcon.getIcon(weatherArrayList[position].weather[0].id))
    }

    override fun getItemCount(): Int {
        return weatherArrayList.size
    }

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

    init {
        mInflater = LayoutInflater.from(context)
        weatherArrayList = weathers
    }
}