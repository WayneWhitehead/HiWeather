package com.hidesign.hiweather.views

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.room.Room
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.hidesign.hiweather.R
import com.hidesign.hiweather.adapter.DailyRecyclerAdapter
import com.hidesign.hiweather.adapter.HourlyRecyclerAdapter
import com.hidesign.hiweather.database.WeatherDatabase
import com.hidesign.hiweather.databinding.FragmentWeatherBinding
import com.hidesign.hiweather.model.AirPollutionResponse
import com.hidesign.hiweather.model.Daily
import com.hidesign.hiweather.model.DbModel
import com.hidesign.hiweather.model.Hourly
import com.hidesign.hiweather.model.OneCallResponse
import com.hidesign.hiweather.network.WeatherViewModel
import com.hidesign.hiweather.util.Constants
import com.hidesign.hiweather.util.Constants.getAPIKey
import com.hidesign.hiweather.util.DateUtils
import com.hidesign.hiweather.util.WeatherUtils
import com.hidesign.hiweather.util.WeatherUtils.getWeatherIconUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.text.MessageFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.coroutines.CoroutineContext
import kotlin.math.roundToInt

class WeatherFragment : Fragment(), CoroutineScope, LifecycleObserver {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var binding: FragmentWeatherBinding
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var weather: OneCallResponse
    private lateinit var airPollution: AirPollutionResponse
    private var isFetching = false
    private lateinit var db: WeatherDatabase

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentWeatherBinding.inflate(layoutInflater)
        binding.swipeLayout.setOnRefreshListener { launch { fetchContent() } }

        LinearSnapHelper().attachToRecyclerView(binding.hourlyForecast.rvForecast)
        binding.hourlyForecast.headerTitle.text = getString(R.string.hourly_forecast)
        LinearSnapHelper().attachToRecyclerView(binding.dailyForecast.rvForecast)
        binding.dailyForecast.headerTitle.text = getString(R.string.daily_forecast)
        setFetchingContent()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        weatherViewModel.oneCallResponse.observe(requireActivity()) { response ->
            weather = response
            updateWidget(weather)
            onWeatherSuccess()
        }
        weatherViewModel.airPollutionResponse.observe(requireActivity()) { response ->
            airPollution = response
            onAirPollutionSuccess()
        }
        firebaseAnalytics = Firebase.analytics
        db = Room.databaseBuilder(requireContext(), WeatherDatabase::class.java, "Weather")
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration().build()
    }

    override fun onResume() {
        super.onResume()
        Bundle().apply {
            this.putString(FirebaseAnalytics.Event.SCREEN_VIEW, TAG)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    suspend fun fetchContent() {
        setFetchingContent()
        val df = SimpleDateFormat("d MMMM HH:mm", Locale.getDefault())
        val formattedDate = df.format(Calendar.getInstance().time)
        binding.dateHeader.date.text = formattedDate

            val apiKey = getAPIKey(requireContext(), Constants.openWeatherKey)

            val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
            val posUnit = sharedPref.getInt(Constants.temperatureUnit, 0)
            var unit = "metric"
            for ((pos, value) in resources.getStringArray(R.array.temperature_units).withIndex()) {
                if (posUnit == pos) {
                    unit = value.lowercase()
                }
            }
            weatherViewModel.getOneCallWeather(apiKey, unit)
            weatherViewModel.getAirPollution(apiKey)

            binding.swipeLayout.isRefreshing = false

    }

    private fun onWeatherSuccess() {
        //region Current Card
        Glide.with(this)
            .load(getWeatherIconUrl(weather.current.weather[0].icon))
            .into(binding.currentCard.skiesImage)
        binding.currentCard.currentTemp.text = MessageFormat.format(getString(R.string._0_c), weather.current.temp.roundToInt())
        binding.currentCard.highTemp.text = MessageFormat.format(getString(R.string._0_c), weather.daily[0].temp.max.roundToInt())
        binding.currentCard.lowTemp.text = MessageFormat.format(getString(R.string._0_c), weather.daily[0].temp.min.roundToInt())
        binding.currentCard.realFeelTemp.text = MessageFormat.format(getString(R.string.real_feel_0_c), weather.current.feelsLike.roundToInt())
        binding.currentCard.description.text = weather.current.weather[0].description.replaceFirstChar {
            it.titlecase(Locale.ROOT)
        }
        binding.currentCard.uvIndex.text = MessageFormat.format(getString(R.string.uv_index_0), weather.current.uvi.roundToDecimal(1))
        //endregion Current Card

        //region Current Extra Card
        binding.currentExtraCard.precipitation.text = MessageFormat.format(getString(R.string._0_p), (weather.daily[0].pop * 100))
        binding.currentExtraCard.humidity.text = MessageFormat.format(getString(R.string._0_p), weather.current.humidity)
        binding.currentExtraCard.cloudiness.text = MessageFormat.format(getString(R.string._0_p), (weather.daily[0].clouds))
        binding.currentExtraCard.dewPoint.text = MessageFormat.format(getString(R.string._0_c), weather.current.dewPoint.roundToInt())
        binding.currentExtraCard.pressure.text = MessageFormat.format(getString(R.string._0_hpa), weather.current.pressure)
        binding.currentExtraCard.visibility.text = MessageFormat.format(getString(R.string._0_m), weather.current.visibility / 1000)
        //endregion Current Extra Card

        //region Wind Card
        binding.windCard.WindSpeed.text = weather.current.windSpeed.roundToDecimal().toString()
        binding.windCard.WindDirectionDegrees.rotation = (weather.current.windDeg - 270).toFloat()
        binding.windCard.WindDirectionText.text = WeatherUtils.getWindDegreeText(weather.current.windDeg)
        //endregion Wind Card

        //region Hourly Forecast
        val hourlyForecast: ArrayList<Hourly> = ArrayList()
        for (i in 1..23) {
            hourlyForecast.add(weather.hourly[i])
        }
        binding.hourlyForecast.rvForecast.adapter = HourlyRecyclerAdapter(requireContext(), hourlyForecast, weather.timezone).apply {
            onItemClick = {
                ExpandedForecast.newInstance(it, weather.timezone).show(childFragmentManager, ExpandedForecast.TAG)
            }
        }
        //endregion Hourly Forecast

        //region Daily Forecast
        val dailyForecast: ArrayList<Daily> = ArrayList()
        dailyForecast.addAll(weather.daily)
        dailyForecast.removeAt(0)
        binding.dailyForecast.rvForecast.adapter = DailyRecyclerAdapter(requireContext(), dailyForecast, weather.timezone).apply {
            onItemClick = {
                ExpandedForecast.newInstance(it, weather.timezone).show(childFragmentManager, ExpandedForecast.TAG)
            }
        }
        //endregion Daily Forecast

        //region Sun Card
        binding.sunCard.Sunrise.text = DateUtils.getDateTime("HH:mm", (weather.daily[0].sunrise).toLong(), weather.timezone)
        binding.sunCard.Sunset.text = DateUtils.getDateTime("HH:mm", (weather.daily[0].sunset).toLong(), weather.timezone)
        binding.sunCard.sunContent.setOnClickListener {
            ExpandedSunMoon.newInstance(weather.daily[0], weather.timezone).show(childFragmentManager, ExpandedSunMoon.TAG)
        }
        //endregion Sun Card

        setContentFound()
    }

    private fun onAirPollutionSuccess() {
        binding.airCard.airQuality.text = airPollution.list[0].main.aqi.toString()
        binding.airCard.airQualityText.apply {
            text = WeatherUtils.getAirQualityText(airPollution.list[0].main.aqi)
        }

        binding.airCard.airCo.apply {
            text = MessageFormat.format("CO - {0}", airPollution.list[0].components.co.roundToDecimal())
            setOnClickListener { displayAirQualityItemFragment(Constants.carbon_monoxide) }
        }
        binding.airCard.airNhThree.apply {
            text = MessageFormat.format("NH₃ - {0}", airPollution.list[0].components.nh3.roundToDecimal())
            setOnClickListener { displayAirQualityItemFragment(Constants.ammonia) }
        }
        binding.airCard.airNo.apply {
            text = MessageFormat.format("NO - {0}", airPollution.list[0].components.no.roundToDecimal())
            setOnCloseIconClickListener {
                //TODO
            }
        }
        binding.airCard.airNoTwo.apply {
            text = MessageFormat.format("NO₂ - {0}", airPollution.list[0].components.no2.roundToDecimal())
            setOnClickListener { displayAirQualityItemFragment(Constants.nitrogen_dioxide) }
        }
        binding.airCard.airOThree.apply {
            text = MessageFormat.format("O₃ - {0}", airPollution.list[0].components.o3.roundToDecimal())
            setOnClickListener { displayAirQualityItemFragment(Constants.ozone) }
        }
        binding.airCard.airPmTen.apply {
            text = MessageFormat.format("PM₁₀ - {0}", airPollution.list[0].components.pm10.roundToDecimal())
            setOnClickListener { displayAirQualityItemFragment(Constants.coarse_particle_matter) }
        }
        binding.airCard.airPmTwoFive.apply {
            text = MessageFormat.format("PM₂₅ - {0}", airPollution.list[0].components.pm25.roundToDecimal())
            setOnClickListener { displayAirQualityItemFragment(Constants.fine_particle_matter) }
        }
        binding.airCard.airSoTwo.apply {
            text = MessageFormat.format("SO₂ - {0}", airPollution.list[0].components.so2.roundToDecimal())
            setOnClickListener { displayAirQualityItemFragment(Constants.sulphur_dioxide) }
        }

        binding.airCard.airShimmer.slideVisibility(true)
        binding.airCard.airShimmer.stopShimmer()
        binding.airCard.airContent.slideVisibility(false)
    }

    private fun displayAirQualityItemFragment(item: String) {
        ExpandedAirItem.newInstance(item, airPollution.list[0].components).showNow(parentFragmentManager, Constants.airItemScreeName)
    }

    private fun updateWidget(weather: OneCallResponse) {
        val gson = Gson()
        val jsonWeather: String = gson.toJson(weather)
        val db = Room.databaseBuilder(requireContext(), WeatherDatabase::class.java, "Weather")
            .allowMainThreadQueries().fallbackToDestructiveMigration().build()
        val weatherDao = db.weatherDao()
        weatherDao.insertAll(DbModel(0, jsonWeather))
        val appWidgetManager = AppWidgetManager.getInstance(context)
        for (appWidgetId in appWidgetManager.getAppWidgetIds(ComponentName(requireContext(),
            WeatherWidget::class.java))) {
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,
                R.layout.weather_widget)
        }
    }

    private fun setContentFound() {
        if (!isFetching) {
            return
        }

        binding.currentCard.currentContent.slideVisibility(false)
        binding.currentCard.currentShimmer.slideVisibility(true)
        binding.currentCard.currentShimmer.stopShimmer()

        binding.hourlyForecast.rvForecast.slideVisibility(false)
        binding.hourlyForecast.rvForecast.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.HORIZONTAL))
        binding.hourlyForecast.forecastShimmer.slideVisibility(true)
        binding.hourlyForecast.forecastShimmer.stopShimmer()

        binding.currentExtraCard.currentExtraContent.slideVisibility(false)
        binding.currentExtraCard.currentExtraShimmer.slideVisibility(true)
        binding.currentExtraCard.currentExtraShimmer.stopShimmer()

        binding.windCard.windContent.slideVisibility(false)
        binding.windCard.windShimmer.slideVisibility(true)
        binding.windCard.windShimmer.stopShimmer()

        binding.sunCard.sunContent.slideVisibility(false)
        binding.sunCard.sunShimmer.slideVisibility(true)
        binding.sunCard.sunShimmer.stopShimmer()

        binding.dailyForecast.rvForecast.slideVisibility(false)
        binding.dailyForecast.rvForecast.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.HORIZONTAL))
        binding.dailyForecast.forecastShimmer.slideVisibility(true)
        binding.dailyForecast.forecastShimmer.stopShimmer()

        isFetching = false
        (requireActivity() as WeatherActivity).stopFetchingContent()
    }

    private fun setFetchingContent() {
        if (isFetching) {
            return
        }
        (requireActivity() as WeatherActivity).setFetchingContent()

        binding.currentCard.currentContent.slideVisibility(true)
        binding.currentCard.currentShimmer.slideVisibility(false)
        binding.currentCard.currentShimmer.startShimmer()

        binding.hourlyForecast.rvForecast.slideVisibility(true)
        binding.hourlyForecast.forecastShimmer.slideVisibility(false)
        binding.hourlyForecast.forecastShimmer.startShimmer()

        binding.currentExtraCard.currentExtraContent.slideVisibility(true)
        binding.currentExtraCard.currentExtraShimmer.slideVisibility(false)
        binding.currentExtraCard.currentExtraShimmer.startShimmer()

        binding.airCard.airContent.slideVisibility(true)
        binding.airCard.airShimmer.slideVisibility(false)
        binding.airCard.airShimmer.startShimmer()
        binding.windCard.windContent.slideVisibility(true)
        binding.windCard.windShimmer.slideVisibility(false)
        binding.windCard.windShimmer.startShimmer()
        binding.sunCard.sunContent.slideVisibility(true)
        binding.sunCard.sunShimmer.slideVisibility(false)
        binding.sunCard.sunShimmer.startShimmer()

        binding.dailyForecast.rvForecast.slideVisibility(true)
        binding.dailyForecast.forecastShimmer.slideVisibility(false)
        binding.dailyForecast.forecastShimmer.startShimmer()

        isFetching = true
    }

    private fun Double.roundToDecimal(decimals: Int = 1): Double {
        val number = this.toBigDecimal().setScale(decimals, RoundingMode.HALF_EVEN)
        return number.toDouble()
    }

    private fun View.slideVisibility(hide: Boolean, durationTime: Long = 300) {
        val transition = Slide(Gravity.BOTTOM)
        transition.apply {
            duration = durationTime
            addTarget(this@slideVisibility)
        }
        TransitionManager.beginDelayedTransition(this.parent as ViewGroup, transition)
        if (hide) {
            this.visibility = View.INVISIBLE
        } else {
            this.visibility = View.VISIBLE
        }
    }

    companion object {
        const val TAG = "Weather Fragment"
    }
}