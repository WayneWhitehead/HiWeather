package com.hidesign.hiweather.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hidesign.hiweather.R
import com.hidesign.hiweather.databinding.ExpandedForecastBinding
import com.hidesign.hiweather.model.Daily
import com.hidesign.hiweather.model.Hourly
import com.hidesign.hiweather.model.WeatherIcon
import com.hidesign.hiweather.model.Wind
import com.hidesign.hiweather.util.DateUtils
import java.math.RoundingMode
import java.text.MessageFormat
import kotlin.math.roundToInt

class ExpandedForecast : BottomSheetDialogFragment() {
    private lateinit var binding: ExpandedForecastBinding
    private var weatherDaily: Daily? = null
    private var weatherHourly: Hourly? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = ExpandedForecastBinding.inflate(inflater)
        if (weatherHourly != null) {
            binding.HighTemp.visibility = View.GONE
            binding.LowTemp.visibility = View.GONE

            binding.date.text = DateUtils.getDateTime("HH:00", weatherHourly?.dt?.toLong()!!)
            binding.CurrentTemp.text = MessageFormat.format(getString(R.string._0_c), weatherHourly?.temp?.roundToInt())
            binding.RealFeelTemp.text = MessageFormat.format(getString(R.string.real_feel_0_c), weatherHourly?.feelsLike?.roundToInt())
            binding.Precipitation.text = MessageFormat.format(getString(R.string.precipitation_0), (weatherHourly?.pop!! * 100))
            binding.Humidity.text = MessageFormat.format(getString(R.string.humidity_0), weatherHourly?.humidity)
            binding.DewPoint.text = MessageFormat.format(getString(R.string.dew_point_0_c), weatherHourly?.dewPoint?.roundToInt())
            binding.Pressure.text = MessageFormat.format(getString(R.string.pressure_0_mbar), weatherHourly?.pressure)
            binding.UVIndex.text = MessageFormat.format(getString(R.string.uv_index_0), weatherHourly?.uvi?.roundToInt())
            binding.Visibility.text = MessageFormat.format(getString(R.string.visibility_0_m), weatherHourly?.visibility)

            binding.WindSpeed.text = String.format(weatherHourly?.windSpeed?.toBigDecimal()?.setScale(1, RoundingMode.HALF_EVEN).toString())
            binding.WindDirectionDegrees.rotation = ((weatherHourly!!.windDeg - 270).toFloat())
            binding.WindDirectionText.text = Wind.getWindDegreeText(weatherHourly!!.windDeg)

            binding.skiesImage.setImageResource(WeatherIcon.getIcon(weatherHourly?.clouds!!))
        }
        if (weatherDaily != null) {
            binding.CurrentTemp.visibility = View.GONE
            binding.Visibility.visibility = View.GONE

            binding.date.text = DateUtils.getDayOfWeekText(DateUtils.getDateTime("u", weatherDaily?.dt?.toLong()!!))
            binding.LowTemp.text = MessageFormat.format(getString(R.string.low_0_c), weatherDaily?.temp?.min?.roundToInt())
            binding.HighTemp.text = MessageFormat.format(getString(R.string.high_0_c), weatherDaily?.temp?.max?.roundToInt())
            binding.RealFeelTemp.text = MessageFormat.format(getString(R.string.real_feel_0_c), weatherDaily?.feelsLike?.day?.roundToInt())
            binding.Precipitation.text = MessageFormat.format(getString(R.string.precipitation_0), (weatherDaily?.pop!! * 100))
            binding.Humidity.text = MessageFormat.format(getString(R.string.humidity_0), weatherDaily?.humidity)
            binding.DewPoint.text = MessageFormat.format(getString(R.string.dew_point_0_c), weatherDaily?.dewPoint?.roundToInt())
            binding.Pressure.text = MessageFormat.format(getString(R.string.pressure_0_mbar), weatherDaily?.pressure)
            binding.UVIndex.text = MessageFormat.format(getString(R.string.uv_index_0), weatherDaily?.uvi?.roundToInt())

            binding.WindSpeed.text = String.format(weatherDaily?.windSpeed?.toBigDecimal()?.setScale(1, RoundingMode.HALF_EVEN).toString())
            binding.WindDirectionDegrees.rotation = ((weatherDaily!!.windDeg - 270).toFloat())
            binding.WindDirectionText.text = Wind.getWindDegreeText(weatherDaily!!.windDeg)

            binding.skiesImage.setImageResource(WeatherIcon.getIcon(weatherDaily?.weather!![0].id))
        }
        return binding.root
    }

    companion object {
        const val TAG = "ForecastBottomSheet"

        @JvmStatic
        fun newInstance(daily: Daily) =
            ExpandedForecast().apply {
                arguments = Bundle().apply {
                    weatherDaily = daily
                }
            }
        fun newInstance(hourly: Hourly) =
            ExpandedForecast().apply {
                arguments = Bundle().apply {
                    weatherHourly = hourly
                }
            }
    }
}