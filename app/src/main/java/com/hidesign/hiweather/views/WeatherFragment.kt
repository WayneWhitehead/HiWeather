package com.hidesign.hiweather.views

import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.room.Room
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.hidesign.hiweather.R
import com.hidesign.hiweather.adapter.DailyRecyclerAdapter
import com.hidesign.hiweather.adapter.HourlyRecyclerAdapter
import com.hidesign.hiweather.database.WeatherDatabase
import com.hidesign.hiweather.databinding.FragmentWeatherBinding
import com.hidesign.hiweather.model.AirPollutionResponse
import com.hidesign.hiweather.model.Daily
import com.hidesign.hiweather.model.Hourly
import com.hidesign.hiweather.model.OneCallResponse
import com.hidesign.hiweather.network.WeatherViewModel
import com.hidesign.hiweather.util.Constants
import com.hidesign.hiweather.util.Constants.getAPIKey
import com.hidesign.hiweather.util.DateUtils
import com.hidesign.hiweather.util.DialogUtil
import com.hidesign.hiweather.util.WeatherUtils
import com.hidesign.hiweather.util.WeatherUtils.getWeatherIconUrl
import com.hidesign.hiweather.views.WeatherActivity.Companion.uAddress
import kotlinx.coroutines.*
import java.math.RoundingMode
import java.text.MessageFormat
import java.text.SimpleDateFormat
import java.util.*
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentWeatherBinding.inflate(layoutInflater)
        binding.swipeLayout.setOnRefreshListener { launch { fetchContent() } }

        LinearSnapHelper().attachToRecyclerView(binding.rvHourlyForecast)
        binding.hourlyForecastHeader.headerTitle.text = getString(R.string.hourly_forecast)
        binding.hourlyForecastHeader.headerCard.setOnClickListener {
            if (binding.rvHourlyForecast.isVisible) {
                binding.rvHourlyForecast.slideVisibility(true)
            } else {
                binding.rvHourlyForecast.slideVisibility(false)
            }
            binding.hourlyForecastHeader.displayForecast.rotation =
                binding.hourlyForecastHeader.displayForecast.rotation + 180F
        }
        LinearSnapHelper().attachToRecyclerView(binding.rvDailyForecast)
        binding.dailyForecastHeader.headerTitle.text = getString(R.string.daily_forecast)
        binding.dailyForecastHeader.headerCard.setOnClickListener {
            if (binding.rvDailyForecast.isVisible) {
                binding.rvDailyForecast.slideVisibility(true)
            } else {
                binding.rvDailyForecast.slideVisibility(false)
            }
            binding.dailyForecastHeader.displayForecast.rotation =
                binding.dailyForecastHeader.displayForecast.rotation + 180F
        }
        setFetchingContent()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        firebaseAnalytics = Firebase.analytics
        db = Room.databaseBuilder(requireContext(), WeatherDatabase::class.java, "Weather")
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration().build()
        launch {
            fetchContent()
        }
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
        coroutineScope {
            if (uAddress == null) {
                Toast.makeText(requireContext(),
                    "Something went wrong trying to get the location \n Please try again.",
                    Toast.LENGTH_SHORT).show()
                val activity = requireActivity() as WeatherActivity
                activity.binding.vpContent.setCurrentItem(0, true)
                binding.swipeLayout.isRefreshing = false
                return@coroutineScope
            }

            setFetchingContent()
            val df = SimpleDateFormat("d MMMM HH:mm", Locale.getDefault())
            val formattedDate = df.format(Calendar.getInstance().time)
            binding.dateHeader.date.text = formattedDate
            launch {
                val apiKey = getAPIKey(requireContext(), Constants.openWeatherKey)
                val oneCallResponse = weatherViewModel.getOneCallWeather(apiKey)
                val airPollutionResponse = weatherViewModel.getAirPollution(apiKey)

                binding.swipeLayout.isRefreshing = false

                if (oneCallResponse?.isSuccessful!!) {
                    weather = oneCallResponse.body()!!
                    var found = false
                    db.hourlyDao().getAll().forEach {
                        if (it.dt == weather.hourly[0].dt) {
                            found = true
                        }
                    }
                    if (!found) {
                        val hourly = weather.hourly[0]
                        hourly.timezone = weather.timezone
                        db.hourlyDao().insertAll(hourly)
                    }
                    onWeatherSuccess()
                } else {
                    DialogUtil.displayErrorDialog(requireContext(),
                        oneCallResponse.code(),
                        oneCallResponse.message())
                }

                if (airPollutionResponse?.isSuccessful!!) {
                    airPollution = airPollutionResponse.body()!!
                    onAirPollutionSuccess()
                } else {
                    DialogUtil.displayErrorDialog(requireContext(),
                        airPollutionResponse.code(),
                        airPollutionResponse.message())
                }
            }.join()
        }
    }

    private fun onWeatherSuccess() {
        //region Current Card
        Glide.with(this)
            .load(getWeatherIconUrl(weather.current.weather[0].icon))
            .into(binding.currentCard.skiesImage)
        binding.currentCard.currentTemp.text =
            MessageFormat.format(getString(R.string._0_c), weather.current.temp.roundToInt())
        binding.currentCard.highTemp.text =
            MessageFormat.format(getString(R.string.high_0_c),
                weather.daily[0].temp!!.max.roundToInt())
        binding.currentCard.lowTemp.text =
            MessageFormat.format(getString(R.string.low_0_c),
                weather.daily[0].temp!!.min.roundToInt())
        binding.currentCard.realFeelTemp.text =
            MessageFormat.format(getString(R.string.real_feel_0_c),
                weather.current.feelsLike.roundToInt())
        binding.currentCard.description.text =
            weather.current.weather[0].description
        //endregion Current Card

        //region Current Extra Card
        binding.currentExtraCard.precipitation.text =
            MessageFormat.format(getString(R.string._0_p), (weather.daily[0].pop!! * 100))
        binding.currentExtraCard.humidity.text =
            MessageFormat.format(getString(R.string._0_p), weather.current.humidity)
        binding.currentExtraCard.cloudiness.text =
            MessageFormat.format(getString(R.string._0_p), (weather.daily[0].clouds))
        binding.currentExtraCard.dewPoint.text =
            MessageFormat.format(getString(R.string.dew_point_0_c),
                weather.current.dewPoint.roundToInt())
        binding.currentExtraCard.pressure.text =
            MessageFormat.format(getString(R.string.pressure_0_mbar), weather.current.pressure)
        binding.currentExtraCard.uvIndex.text =
            MessageFormat.format(getString(R.string.uv_index_0),
                weather.current.uvi.roundToInt())
        binding.currentExtraCard.visibility.text =
            MessageFormat.format(getString(R.string.visibility_0_m), weather.current.visibility)
        //endregion Current Extra Card

        //region Wind Card
        binding.windCard.WindSpeed.text = weather.current.windSpeed.roundToDecimal().toString()
        binding.windCard.WindDirectionDegrees.rotation =
            (weather.current.windDeg - 270).toFloat()
        binding.windCard.WindDirectionText.text =
            WeatherUtils.getWindDegreeText(weather.current.windDeg)
        //endregion Wind Card

        //region Hourly Forecast
        val hourlyForecast: ArrayList<Hourly> = ArrayList()
        for (i in 1..23) {
            hourlyForecast.add(weather.hourly[i])
        }
        binding.hourlyForecastHeader.headerCard.isEnabled = true
        binding.rvHourlyForecast.adapter =
            HourlyRecyclerAdapter(requireContext(), hourlyForecast, weather.timezone).apply {
                onItemClick = {
                    ExpandedForecast.newInstance(it, weather.timezone)
                        .show(childFragmentManager, ExpandedForecast.TAG)
                }
            }
        //endregion Hourly Forecast

        //region Daily Forecast
        val dailyForecast: ArrayList<Daily> = ArrayList()
        dailyForecast.addAll(weather.daily)
        dailyForecast.removeAt(0)
        binding.dailyForecastHeader.headerCard.isEnabled = true
        binding.rvDailyForecast.adapter =
            DailyRecyclerAdapter(requireContext(), dailyForecast, weather.timezone).apply {
                onItemClick = {
                    ExpandedForecast.newInstance(it, weather.timezone)
                        .show(childFragmentManager, ExpandedForecast.TAG)
                }
            }
        //endregion Daily Forecast

        //region Sun Card
        binding.sunCard.Sunrise.text = DateUtils.getDateTime("HH:mm",
            (weather.daily[0].sunrise)!!.toLong(),
            weather.timezone)
        binding.sunCard.Sunset.text =
            DateUtils.getDateTime("HH:mm", (weather.daily[0].sunset)!!.toLong(), weather.timezone)
        binding.sunCard.sunContent.setOnClickListener {
            ExpandedSunMoon.newInstance(weather.daily[0], weather.timezone)
                .show(childFragmentManager, ExpandedSunMoon.TAG)
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
            text = MessageFormat.format("CO - {0}",
                airPollution.list[0].components.co.roundToDecimal())
            setOnClickListener { displayAirQualityItemFragment(Constants.carbon_monoxide) }
        }
        binding.airCard.airNhThree.apply {
            text = MessageFormat.format("NH₃ - {0}",
                airPollution.list[0].components.nh3.roundToDecimal())
            setOnClickListener { displayAirQualityItemFragment(Constants.ammonia) }
        }
        binding.airCard.airNo.apply {
            text = MessageFormat.format("NO - {0}",
                airPollution.list[0].components.no.roundToDecimal())
            setOnCloseIconClickListener {
                //TODO
            }
        }
        binding.airCard.airNoTwo.apply {
            text = MessageFormat.format("NO₂ - {0}",
                airPollution.list[0].components.no2.roundToDecimal())
            setOnClickListener { displayAirQualityItemFragment(Constants.nitrogen_dioxide) }
        }
        binding.airCard.airOThree.apply {
            text = MessageFormat.format("O₃ - {0}",
                airPollution.list[0].components.o3.roundToDecimal())
            setOnClickListener { displayAirQualityItemFragment(Constants.ozone) }
        }
        binding.airCard.airPmTen.apply {
            text = MessageFormat.format("PM₁₀ - {0}",
                airPollution.list[0].components.pm10.roundToDecimal())
            setOnClickListener { displayAirQualityItemFragment(Constants.coarse_particle_matter) }
        }
        binding.airCard.airPmTwoFive.apply {
            text = MessageFormat.format("PM₂₅ - {0}",
                airPollution.list[0].components.pm25.roundToDecimal())
            setOnClickListener { displayAirQualityItemFragment(Constants.fine_particle_matter) }
        }
        binding.airCard.airSoTwo.apply {
            text = MessageFormat.format("SO₂ - {0}",
                airPollution.list[0].components.so2.roundToDecimal())
            setOnClickListener {
                setOnClickListener { displayAirQualityItemFragment(Constants.sulphur_dioxide) }
            }
        }

        binding.airCard.airShimmer.slideVisibility(true)
        binding.airCard.airShimmer.stopShimmer()
        binding.airCard.airContent.slideVisibility(false)
    }

    private fun displayAirQualityItemFragment(item: String) {
        ExpandedAirItem.newInstance(item, airPollution.list[0].components)
            .showNow(parentFragmentManager, Constants.airItemScreeName)
    }

    private fun setContentFound() {
        if (!isFetching) {
            return
        }

        binding.currentCard.currentContent.slideVisibility(false)
        binding.currentCard.currentShimmer.slideVisibility(true)
        binding.currentCard.currentShimmer.stopShimmer()
        binding.currentExtraCard.currentExtraContent.slideVisibility(false)
        binding.currentExtraCard.currentExtraShimmer.slideVisibility(true)
        binding.currentExtraCard.currentExtraShimmer.stopShimmer()
        binding.windCard.windContent.slideVisibility(false)
        binding.windCard.windShimmer.slideVisibility(true)
        binding.windCard.windShimmer.stopShimmer()
        binding.sunCard.sunContent.slideVisibility(false)
        binding.sunCard.sunShimmer.slideVisibility(true)
        binding.sunCard.sunShimmer.stopShimmer()

        binding.dailyForecastHeader.headerCard.isEnabled = true
        binding.hourlyForecastHeader.headerCard.isEnabled = true
        isFetching = false
        val activity = requireActivity() as WeatherActivity
        activity.binding.linearProgress.visibility = View.INVISIBLE
    }

    private fun setFetchingContent() {
        if (isFetching) {
            return
        }
        val activity = requireActivity() as WeatherActivity
        activity.binding.linearProgress.visibility = View.VISIBLE

        binding.currentCard.currentContent.slideVisibility(true)
        binding.currentCard.currentShimmer.slideVisibility(false)
        binding.currentCard.currentShimmer.startShimmer()
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

        if (binding.rvHourlyForecast.isVisible) {
            binding.hourlyForecastHeader.displayForecast.rotation = 0F
            binding.rvHourlyForecast.slideVisibility(true)
        }
        if (binding.rvDailyForecast.isVisible) {
            binding.dailyForecastHeader.displayForecast.rotation = 0F
            binding.rvDailyForecast.slideVisibility(true)
        }
        binding.hourlyForecastHeader.headerCard.isEnabled = false
        binding.dailyForecastHeader.headerCard.isEnabled = false
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
            this.visibility = View.GONE
        } else {
            this.visibility = View.VISIBLE
        }
    }

    companion object {
        const val TAG = "Weather Fragment"
    }
}