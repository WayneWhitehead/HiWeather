package com.hidesign.hiweather.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.hidesign.hiweather.R
import com.hidesign.hiweather.databinding.ExpandedForecastBinding
import com.hidesign.hiweather.model.Daily
import com.hidesign.hiweather.model.Hourly
import com.hidesign.hiweather.util.AdUtil
import com.hidesign.hiweather.util.DateUtils
import com.hidesign.hiweather.util.WeatherUtils.getWeatherIconUrl
import com.hidesign.hiweather.util.WeatherUtils.getWindDegreeText
import java.math.RoundingMode
import java.text.MessageFormat
import kotlin.math.roundToInt

class ExpandedForecast : BottomSheetDialogFragment() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var binding: ExpandedForecastBinding
    private var weatherDaily: Daily? = null
    private var weatherHourly: Hourly? = null
    private var timezone: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = ExpandedForecastBinding.inflate(inflater)
        binding.nativeAd.addView(AdUtil.setupAds(requireContext(), AdUtil.bottomSheetId))
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        firebaseAnalytics = Firebase.analytics

        val date = weatherHourly?.dt?.toLong() ?: weatherDaily?.dt?.toLong() ?: 0
        val image = getWeatherIconUrl(weatherHourly?.weather?.get(0)?.icon
            ?: weatherDaily?.weather?.get(0)?.icon ?: "")
        Glide.with(this)
            .load(image)
            .into(binding.skiesImage)

        val realFeel = (weatherDaily?.feelsLike?.day ?: weatherHourly?.feelsLike)?.roundToInt() ?: 0
        binding.realFeelTemp.text =
            MessageFormat.format(getString(R.string.real_feel_0_c), realFeel)
        val precipitation = ((weatherDaily?.pop ?: weatherHourly?.pop)?.roundToInt() ?: 0) * 100
        binding.precipitation.text = MessageFormat.format(getString(R.string._0_p), precipitation)
        val humidity = weatherHourly?.humidity ?: weatherDaily?.humidity ?: 0
        binding.humidity.text = MessageFormat.format(getString(R.string._0_p), humidity)
        val dewPoint = (weatherHourly?.dewPoint ?: weatherDaily?.dewPoint)?.roundToInt() ?: 0
        binding.DewPoint.text = MessageFormat.format(getString(R.string.dew_point_0_c), dewPoint)
        val pressure = weatherHourly?.pressure ?: weatherDaily?.pressure ?: 0
        binding.Pressure.text = MessageFormat.format(getString(R.string.pressure_0_mbar), pressure)
        val uvi = (weatherHourly?.uvi ?: weatherDaily?.uvi)?.roundToInt() ?: 0
        binding.UVIndex.text = MessageFormat.format(getString(R.string.uv_index_0), uvi)

        val windSpeed = (weatherHourly?.windSpeed ?: weatherDaily?.windSpeed ?: 0.0).toBigDecimal()
            .setScale(1, RoundingMode.HALF_EVEN)
        binding.WindSpeed.text = String.format(windSpeed.toString())
        val windDirection = weatherHourly?.windDeg ?: weatherDaily?.windDeg ?: 0
        binding.WindDirectionDegrees.rotation = (windDirection - 270).toFloat()
        binding.WindDirectionText.text = getWindDegreeText(windDirection)


        if (weatherHourly != null) {
            binding.date.text = DateUtils.getDateTime("HH:00", date, timezone)
            val currentTemp = weatherHourly?.temp?.roundToInt() ?: 0
            binding.currentTemp.text = MessageFormat.format(getString(R.string._0_c), currentTemp)
            val visibility = weatherHourly?.visibility ?: 0
            binding.Visibility.text =
                MessageFormat.format(getString(R.string.visibility_0_m), visibility / 1000)

            binding.lowTemp.visibility = View.GONE
            binding.highTemp.visibility = View.GONE
        }
        if (weatherDaily != null) {
            binding.date.text =
                DateUtils.getDayOfWeekText(DateUtils.getDateTime("u", date, timezone))
            val high = weatherDaily?.temp?.max?.roundToInt() ?: 0
            binding.highTemp.text = MessageFormat.format(getString(R.string.high_0_c), high)
            val low = weatherDaily?.temp?.min?.roundToInt() ?: 0
            binding.lowTemp.text = MessageFormat.format(getString(R.string.low_0_c), low)

            binding.currentTemp.visibility = View.GONE
            binding.Visibility.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        Bundle().apply {
            this.putString(FirebaseAnalytics.Event.SCREEN_VIEW, TAG)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, this)
        }
    }

    companion object {
        const val TAG = "Forecast BottomSheet"

        @JvmStatic
        fun newInstance(daily: Daily, tz: String) =
            ExpandedForecast().apply {
                arguments = Bundle().apply {
                    weatherDaily = daily
                    timezone = tz
                }
            }

        fun newInstance(hourly: Hourly, tz: String) =
            ExpandedForecast().apply {
                arguments = Bundle().apply {
                    weatherHourly = hourly
                    timezone = tz
                }
            }
    }
}