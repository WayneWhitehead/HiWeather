package com.hidesign.hiweather.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hidesign.hiweather.R
import com.hidesign.hiweather.model.Daily
import com.hidesign.hiweather.util.DateUtils
import com.hidesign.hiweather.util.WeatherUtils.getWeatherIcon
import java.text.MessageFormat
import kotlin.math.roundToInt

class DailyRecyclerAdapter internal constructor(
    context: Context?,
    weathers: ArrayList<Daily>,
    tz: String,
) : RecyclerView.Adapter<DailyRecyclerAdapter.ViewHolder>() {
    private val weatherArrayList: ArrayList<Daily>
    private var timezone: String = ""
    private val mInflater: LayoutInflater
    var onItemClick: ((Daily) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.card_daily_forecast_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.date.text = DateUtils.getDayOfWeekText(DateUtils.getDateTime("u",
            weatherArrayList[position].dt.toLong(),
            timezone))
        holder.high.text =
            MessageFormat.format("High {0}°C", weatherArrayList[position].temp.max.roundToInt())
        holder.low.text = MessageFormat.format("Low {0}°C", weatherArrayList[position].temp.min.roundToInt())
        holder.precipitation.text = MessageFormat.format("{0}%",  weatherArrayList[position].pop * 100)
        holder.icon.setImageResource(getWeatherIcon(weatherArrayList[position].weather[0].id))
    }

    override fun getItemCount(): Int {
        return weatherArrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var date: TextView
        var high: TextView
        var low: TextView
        var precipitation: TextView
        var icon: ImageView

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(weatherArrayList[bindingAdapterPosition])
            }
            date = itemView.findViewById(R.id.date)
            high = itemView.findViewById(R.id.HighTemp)
            low = itemView.findViewById(R.id.LowTemp)
            precipitation = itemView.findViewById(R.id.Precipitation)
            icon = itemView.findViewById(R.id.skiesImage)
        }
    }

    init {
        mInflater = LayoutInflater.from(context)
        weatherArrayList = weathers
        timezone = tz
    }
}