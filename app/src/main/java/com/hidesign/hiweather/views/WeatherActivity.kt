package com.hidesign.hiweather.views

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hidesign.hiweather.R
import com.hidesign.hiweather.adapter.RecyclerViewAdapter
import com.hidesign.hiweather.databinding.ActivityWeatherBinding
import com.hidesign.hiweather.models.*
import com.hidesign.hiweather.network.WeatherViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Response
import java.text.MessageFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext

class WeatherActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var weatherCurrent: WeatherCurrentItem
    private var location: LocationResultItem? = null
    private var forecast = ArrayList<DailyForecast>()

    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var binding: ActivityWeatherBinding
    private var uAddress = IntroActivity.uAddress

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbarLayout.title = uAddress.locality + "," + uAddress.countryCode
        binding.toolbarLayout.titleCollapseMode = CollapsingToolbarLayout.TITLE_COLLAPSE_MODE_SCALE
        binding.toolbarLayout.setContentScrimColor(getColor(R.color.colorAccentLight))

        val horizontalLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.content.recyclerView.layoutManager = horizontalLayoutManager

        val df = SimpleDateFormat("dd-MMMM-yyyy", Locale.getDefault())
        val formattedDate = df.format(Calendar.getInstance().time)
        binding.content.date.text = formattedDate
        weatherViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)

        binding.content.swipeLayout.setOnRefreshListener {
            fetchContent()
        }
    }

    override fun onResume() {
        super.onResume()
        fetchContent()
    }

    private fun fetchContent(){
        binding.progressIndicator.visibility = View.VISIBLE
        launch {
            val locationResult = weatherViewModel.getLocation(uAddress.locality)
            onLocationResult(locationResult)

            if (location !=null) {
                val currentResult = weatherViewModel.getCurrentConditions(location!!.key)
                val forecastResult = weatherViewModel.getFiveDayForecast(location!!.key)
                onCurrentResult(currentResult)
                onForecastResult(forecastResult)
            } else {
                val currentResult = weatherViewModel.getCurrentConditions()
                val forecastResult = weatherViewModel.getFiveDayForecast()
                onCurrentResult(currentResult)
                onForecastResult(forecastResult)
            }
            binding.progressIndicator.visibility = View.GONE
            binding.content.swipeLayout.isRefreshing = false
        }
    }

    private fun onLocationResult(result: Response<LocationResult?>?) {
        if (result?.isSuccessful!!) {
            location = result.body()!![0]
        } else {
            displayErrorDialog(result.code(), result.message())
            return
        }
    }

    private fun onCurrentResult(result: Response<WeatherCurrent?>?) {
        if (result?.isSuccessful!!) {
            weatherCurrent = result.body()!![0]
        } else {
            displayErrorDialog(result.code(), result.message())
            return
        }

        binding.content.CurrentTemp.text = MessageFormat.format("{0}°C", weatherCurrent.apparentTemperature.metric.value.toString())
        binding.content.HighTemp.text = MessageFormat.format("High {0}°C", weatherCurrent.temperatureSummary.past24HourRange.maximum.metric.value.toString())
        binding.content.LowTemp.text = MessageFormat.format("Low {0}°C", weatherCurrent.temperatureSummary.past24HourRange.minimum.metric.value.toString())
        binding.content.RealFeelTemp.text = MessageFormat.format("Real Feel {0}°C", weatherCurrent.apparentTemperature.metric.value.toString())
        binding.content.Precipitation.text = MessageFormat.format("{0}% Chance of Rain", weatherCurrent.precipitationSummary.precipitation.metric.value.toString())
        binding.content.Humidity.text = MessageFormat.format("Humidity - {0}%", weatherCurrent.relativeHumidity)
        binding.content.DewPoint.text = MessageFormat.format("Dew Point - {0}°C", weatherCurrent.dewPoint.metric.value.toString())
        binding.content.Pressure.text = MessageFormat.format("Pressure - {0}mBar", weatherCurrent.pressure.metric.value.toString())
        binding.content.UVIndex.text = MessageFormat.format("UV Index - {0}, {1}", weatherCurrent.uVIndexText, weatherCurrent.uVIndex)
        binding.content.Visibility.text = MessageFormat.format("Visibility - {0}Km", weatherCurrent.visibility.metric.value.toString())
        binding.content.WindSpeed.text = weatherCurrent.wind.speed.metric.value.toString()
        binding.content.WindDirectionDegrees.rotation = (weatherCurrent.wind.direction.degrees - 270).toFloat()
        binding.content.WindDirectionText.text = MessageFormat.format("From {0}", weatherCurrent.wind.direction.english)
        when (weatherCurrent.weatherIcon) {
            in 1..5 -> {
                binding.content.skiesImage.setImageResource(R.drawable.sun)
            }
            in 6..11 -> {
                binding.content.skiesImage.setImageResource(R.drawable.overcast)
            }
            in 12..18 -> {
                binding.content.skiesImage.setImageResource(R.drawable.rain)
            }
        }
    }

    private fun onForecastResult(result: Response<WeatherForecast?>?) {
        forecast.clear()
        if (result?.isSuccessful!!) {
            forecast.addAll(result.body()!!.dailyForecasts)
            binding.content.Sunrise.text = result.body()!!.dailyForecasts[0].sun.rise.substring(0, 5)
            binding.content.Sunset.text = result.body()!!.dailyForecasts[0].sun.set.substring(0, 5)
            val adapter = RecyclerViewAdapter(this, forecast)
            binding.content.recyclerView.adapter = adapter
        } else {
            displayErrorDialog(result.code(), result.message())
            return
        }
    }

    private fun displayErrorDialog(code: Int, message: String){
        MaterialAlertDialogBuilder(this, R.style.Theme_MaterialComponents_Dialog_Alert)
            .setTitle("Error $code")
            .setMessage(message)
            .setNeutralButton("OK") { _, _ ->
                return@setNeutralButton
            }
            .show()
    }

}