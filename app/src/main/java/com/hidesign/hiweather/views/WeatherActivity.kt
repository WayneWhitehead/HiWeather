package com.hidesign.hiweather.views

import OneCallResponse
import android.app.SearchManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuItemCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hidesign.hiweather.R
import com.hidesign.hiweather.adapter.DailyRecylerAdapter
import com.hidesign.hiweather.adapter.HourlyRecyclerAdapter
import com.hidesign.hiweather.databinding.ActivityWeatherBinding
import com.hidesign.hiweather.model.Daily
import com.hidesign.hiweather.model.Hourly
import com.hidesign.hiweather.model.WeatherIcon
import com.hidesign.hiweather.model.Wind
import com.hidesign.hiweather.network.WeatherViewModel
import com.hidesign.hiweather.util.DateUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Response
import java.math.RoundingMode
import java.text.MessageFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext
import kotlin.math.roundToInt
import kotlin.streams.toList

class WeatherActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var weather: OneCallResponse
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

        val df = SimpleDateFormat("dd-MMMM-yyyy", Locale.getDefault())
        val formattedDate = df.format(Calendar.getInstance().time)
        binding.content.date.text = formattedDate
        weatherViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)

        binding.content.displayForecast.setOnClickListener {
            setVisibility(binding.content.rvHourlyForecast)
        }

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
            val oneCallResponse = weatherViewModel.getOneCallWeather(uAddress.latitude, uAddress.longitude)
            onWeatherSuccess(oneCallResponse)

            binding.progressIndicator.visibility = View.GONE
            binding.content.swipeLayout.isRefreshing = false
        }
    }

    private fun onWeatherSuccess(result: Response<OneCallResponse?>?) {
        if (result?.isSuccessful!!) {
            weather = result.body()!!
        } else {
            displayErrorDialog(result.code(), result.message())
            return
        }

        binding.content.CurrentTemp.text = MessageFormat.format("{0}°C", weather.current.temp.roundToInt())
        binding.content.HighTemp.text = MessageFormat.format("High {0}°C", weather.daily[0].temp.max.roundToInt())
        binding.content.LowTemp.text = MessageFormat.format("Low {0}°C", weather.daily[0].temp.min.roundToInt())
        binding.content.RealFeelTemp.text = MessageFormat.format("Real Feel {0}°C", weather.current.feelsLike.roundToInt())
        binding.content.Precipitation.text = MessageFormat.format("{0}% Chance of Rain", weather.daily[0].pop.toBigDecimal())
        binding.content.Humidity.text = MessageFormat.format("Humidity - {0}%", weather.current.humidity)
        binding.content.DewPoint.text = MessageFormat.format("Dew Point - {0}°C", weather.current.dewPoint.roundToInt())
        binding.content.Pressure.text = MessageFormat.format("Pressure - {0}mBar", weather.current.pressure)
        binding.content.UVIndex.text = MessageFormat.format("UV Index - {0}", weather.current.uvi.roundToInt())
        binding.content.Visibility.text = MessageFormat.format("Visibility - {0}Km", weather.current.visibility)
        binding.content.WindSpeed.text = String.format(weather.current.windSpeed.toBigDecimal().setScale(1, RoundingMode.HALF_EVEN).toString())
        binding.content.WindDirectionDegrees.rotation = (weather.current.windDeg - 270).toFloat()
        binding.content.WindDirectionText.text = Wind.getWindDegreeText(weather.current.windDeg)
        binding.content.skiesImage.setImageResource(WeatherIcon.getIcon(weather.current.weather[0].id))

        val hourlyForecast: ArrayList<Hourly> = ArrayList()
        hourlyForecast.addAll(result.body()!!.hourly.stream().limit(10).toList())
        val hourlyAdapter = HourlyRecyclerAdapter(this, hourlyForecast)
        binding.content.rvHourlyForecast.adapter = hourlyAdapter

        val dailyForecast: ArrayList<Daily> = ArrayList()
        dailyForecast.addAll(result.body()!!.daily)

        binding.content.Sunrise.text = DateUtils.getDateTime("HH:mm", (result.body()!!.daily[0].sunrise).toLong())
        binding.content.Sunset.text = DateUtils.getDateTime("HH:mm", (result.body()!!.daily[0].sunset).toLong())


        val dailyAdapter = DailyRecylerAdapter(this, dailyForecast)
        binding.content.rvDailyForecast.adapter = dailyAdapter
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

    private fun setVisibility(item: RecyclerView) {
        if (item.isVisible) {
            item.visibility = View.GONE
        } else {
            item.visibility = View.VISIBLE
        }
    }

}