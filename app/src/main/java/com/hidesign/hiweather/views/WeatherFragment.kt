package com.hidesign.hiweather.views

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.location.Address
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearSnapHelper
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.hidesign.hiweather.R
import com.hidesign.hiweather.adapter.DailyRecyclerAdapter
import com.hidesign.hiweather.adapter.HourlyRecyclerAdapter
import com.hidesign.hiweather.databinding.FragmentWeatherBinding
import com.hidesign.hiweather.model.AirPollutionResponse
import com.hidesign.hiweather.model.Daily
import com.hidesign.hiweather.model.Hourly
import com.hidesign.hiweather.model.OneCallResponse
import com.hidesign.hiweather.network.WeatherViewModel
import com.hidesign.hiweather.util.AdUtil
import com.hidesign.hiweather.util.DateUtils
import com.hidesign.hiweather.util.DialogUtil
import com.hidesign.hiweather.util.WeatherUtils
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

class WeatherFragment : Fragment(), CoroutineScope, LifecycleObserver {

    companion object {
        const val TAG = "Weather Fragment"
        fun newInstance(selectedAddress: Address): WeatherFragment {
            val fragment = WeatherFragment()
            fragment.uAddress = selectedAddress
            return fragment
        }
    }

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private var uAddress: Address? = SplashScreenActivity.uAddress
    private lateinit var binding: FragmentWeatherBinding
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var weather: OneCallResponse
    private lateinit var airPollution: AirPollutionResponse
    private var isFetching = false

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentWeatherBinding.inflate(layoutInflater)

        val df = SimpleDateFormat("dd MMMM", Locale.getDefault())
        val formattedDate = df.format(Calendar.getInstance().time)
        binding.date.text = formattedDate

        LinearSnapHelper().attachToRecyclerView(binding.rvHourlyForecast)
        LinearSnapHelper().attachToRecyclerView(binding.rvDailyForecast)

        binding.swipeLayout.setOnRefreshListener { fetchContent() }
        binding.hourlyForecastCard.setOnClickListener {
            binding.rvHourlyForecast.slideVisibility()
            binding.displayForecast.rotation =
                binding.displayForecast.rotation + 180F
        }
        binding.dailyForecastCard.setOnClickListener {
            binding.rvDailyForecast.slideVisibility()
            binding.displayDailyForecast.rotation =
                binding.displayDailyForecast.rotation + 180F
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        firebaseAnalytics = Firebase.analytics
        fetchContent()
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

    private fun fetchContent() {
        if (uAddress == null) {
            Toast.makeText(requireContext(),
                "Please Enter a City in the search bar above ",
                Toast.LENGTH_SHORT).show()
            return
        }

        setFetchingContent()

        launch {
            if (uAddress == null) {
                return@launch
            }

            val ai: ApplicationInfo = requireContext().packageManager.getApplicationInfo(
                requireContext().packageName,
                PackageManager.GET_META_DATA)
            val value = ai.metaData["weatherKey"]
            val apiKey = value.toString()

            val oneCallResponse = weatherViewModel.getOneCallWeather(uAddress!!.latitude,
                uAddress!!.longitude,
                apiKey)
            val airPollutionResponse =
                weatherViewModel.getAirPollution(uAddress!!.latitude, uAddress!!.longitude, apiKey)

            binding.swipeLayout.isRefreshing = false
            onWeatherSuccess(oneCallResponse)
            onAirPollutionSuccess(airPollutionResponse)

            val appWidgetManager = AppWidgetManager.getInstance(context)
            val remoteViews = RemoteViews(requireContext().packageName, R.layout.weather_widget)
            appWidgetManager.updateAppWidget(ComponentName(requireContext().packageName,
                WeatherWidget::class.java.name), remoteViews)
        }
    }

    private fun onWeatherSuccess(result: Response<OneCallResponse?>?) {
        if (result?.isSuccessful!!) {
            weather = result.body()!!

            binding.CurrentTemp.text =
                MessageFormat.format(getString(R.string._0_c), weather.current.temp.roundToInt())
            binding.HighTemp.text = MessageFormat.format(getString(R.string.high_0_c),
                weather.daily[0].temp.max.roundToInt())
            binding.LowTemp.text = MessageFormat.format(getString(R.string.low_0_c),
                weather.daily[0].temp.min.roundToInt())
            binding.RealFeelTemp.text = MessageFormat.format(getString(R.string.real_feel_0_c),
                weather.current.feelsLike.roundToInt())
            binding.Precipitation.text = MessageFormat.format(getString(R.string.precipitation_0),
                weather.daily[0].pop * 100)
            binding.Humidity.text =
                MessageFormat.format(getString(R.string.humidity_0), weather.current.humidity)
            binding.DewPoint.text = MessageFormat.format(getString(R.string.dew_point_0_c),
                weather.current.dewPoint.roundToInt())
            binding.Pressure.text =
                MessageFormat.format(getString(R.string.pressure_0_mbar), weather.current.pressure)
            binding.UVIndex.text = MessageFormat.format(getString(R.string.uv_index_0),
                weather.current.uvi.roundToInt())
            binding.Visibility.text =
                MessageFormat.format(getString(R.string.visibility_0_m), weather.current.visibility)
            binding.WindSpeed.text = String.format(weather.current.windSpeed.toBigDecimal()
                .setScale(1, RoundingMode.HALF_EVEN).toString())
            binding.WindDirectionDegrees.rotation = (weather.current.windDeg - 270).toFloat()
            binding.WindDirectionText.text = WeatherUtils.getWindDegreeText(weather.current.windDeg)
            binding.skiesImage.setImageResource(WeatherUtils.getWeatherIcon(weather.current.weather[0].id))

            val hourlyForecast: ArrayList<Hourly> = ArrayList()
            for (i in 1..10) {
                hourlyForecast.add(weather.hourly[i])
            }
            binding.displayForecast.isEnabled = true
            val hourlyAdapter =
                HourlyRecyclerAdapter(requireContext(), hourlyForecast, weather.timezone)
            hourlyAdapter.onItemClick = { hourly: Hourly, _: View ->
                ExpandedForecast.newInstance(hourly,
                    weather.timezone,
                    AdUtil.setupAds(requireContext(), AdUtil.bottomSheetId))
                    .show(childFragmentManager, ExpandedForecast.TAG)
            }
            binding.rvHourlyForecast.adapter = hourlyAdapter

            val dailyForecast: ArrayList<Daily> = ArrayList()
            dailyForecast.addAll(weather.daily)
            val dailyAdapter =
                DailyRecyclerAdapter(requireContext(), dailyForecast, weather.timezone)
            dailyAdapter.onItemClick = {
                ExpandedForecast.newInstance(it,
                    weather.timezone,
                    AdUtil.setupAds(requireContext(), AdUtil.bottomSheetId))
                    .show(childFragmentManager, ExpandedForecast.TAG)
            }
            binding.rvDailyForecast.adapter = dailyAdapter

            binding.sunCard.setOnClickListener {
                ExpandedSunMoon.newInstance(weather.daily[0],
                    weather.current.weather[0].description,
                    weather.timezone,
                    weather.current.uvi,
                    AdUtil.setupAds(requireContext(), AdUtil.bottomSheetId))
                    .show(childFragmentManager, ExpandedSunMoon.TAG)
            }
            binding.Sunrise.text = DateUtils.getDateTime("HH:mm",
                (weather.daily[0].sunrise).toLong(),
                weather.timezone)
            binding.Sunset.text =
                DateUtils.getDateTime("HH:mm", (weather.daily[0].sunset).toLong(), weather.timezone)

            setContentFound()
        } else {
            DialogUtil.displayErrorDialog(requireContext(), result.code(), result.message())
            return
        }
    }

    private fun onAirPollutionSuccess(result: Response<AirPollutionResponse?>?) {
        if (result?.isSuccessful!!) {
            airPollution = result.body()!!

            binding.airQuality.text = airPollution.list[0].main.aqi.toString()
            binding.airQualityText.apply {
                text = WeatherUtils.getAirQualityText(airPollution.list[0].main.aqi)
            }

            binding.airCo.apply {
                text = MessageFormat.format("CO - {0}",
                    airPollution.list[0].components.co.roundToInt())
                setOnClickListener {
                    //TODO
                    DialogUtil.displayInfoDialog(requireContext(),
                        "Carbon monoxide - " + (airPollution.list[0].components.co).roundToInt() + " µg/m³",
                        getString(R.string.airCo))
                }
            }
            binding.airNhThree.apply {
                text = MessageFormat.format("NH₃ - {0}", airPollution.list[0].components.nh3)
                setOnClickListener { displayAirQualityItemFragment("NH3") }
            }
            binding.airNo.apply {
                text = MessageFormat.format("NO - {0}", airPollution.list[0].components.no)
                setOnCloseIconClickListener {
                    //TODO
                }
            }
            binding.airNoTwo.apply {
                text = MessageFormat.format("NO₂ - {0}", airPollution.list[0].components.no2)
                setOnClickListener { displayAirQualityItemFragment("NO2") }
            }
            binding.airOThree.apply {
                text = MessageFormat.format("O₃ - {0}", airPollution.list[0].components.o3)
                setOnClickListener { displayAirQualityItemFragment("O3") }
            }
            binding.airPmTen.apply {
                text = MessageFormat.format("PM₁₀ - {0}", airPollution.list[0].components.pm10)
                setOnClickListener { displayAirQualityItemFragment("PM10") }
            }
            binding.airPmTwoFive.apply {
                text = MessageFormat.format("PM₂₅ - {0}",
                    airPollution.list[0].components.pm25.toBigDecimal()
                        .setScale(2, RoundingMode.HALF_EVEN))
                setOnClickListener { displayAirQualityItemFragment("PM25") }
            }
            binding.airSoTwo.apply {
                text = MessageFormat.format("SO₂ - {0}", airPollution.list[0].components.so2)
                setOnClickListener {
                    //TODO
                    DialogUtil.displayInfoDialog(requireContext(),
                        "Sulfur dioxide - " + (airPollution.list[0].components.so2).toBigDecimal()
                            .setScale(2, RoundingMode.HALF_EVEN) + " µg/m³",
                        getString(R.string.airSoTwo))
                }
            }

            binding.airShimmer.slideVisibility()
            binding.airShimmer.stopShimmer()
            binding.airCard.slideVisibility()
        } else {
            DialogUtil.displayErrorDialog(requireContext(), result.code(), result.message())
            return
        }
    }

    private fun displayAirQualityItemFragment(item: String) {
        var values: IntArray? = null
        var strings: Array<String>? = null
        var current = 0F
        var title = ""
        var screen = ""

        when (item) {
            "PM25" -> {
                values = resources.getIntArray(R.array.airPMTwoFiveValues)
                strings = resources.getStringArray(R.array.airPMTwoFiveStrings)
                current = airPollution.list[0].components.pm25.toFloat()
                title = "Fine Particle Matter(PM₂₅)"
                screen = "Fine Particle Matter Quality Modal"
            }
            "PM10" -> {
                values = resources.getIntArray(R.array.airPMTenValues)
                strings = resources.getStringArray(R.array.airPMTenStrings)
                current = airPollution.list[0].components.pm10.toFloat()
                title = "Course Particulate Matter(PM₁₀)"
                screen = "Course Particulate Matter Quality Modal"
            }
            "O3" -> {
                values = resources.getIntArray(R.array.airOThreeValues)
                strings = resources.getStringArray(R.array.airOThreeStrings)
                current = airPollution.list[0].components.o3.toFloat()
                title = "Ozone(O₃)"
                screen = "Ozone Quality Modal"
            }
            "NO2" -> {
                values = resources.getIntArray(R.array.airNOTwoValues)
                strings = resources.getStringArray(R.array.airNOTwoStrings)
                current = airPollution.list[0].components.no2.toFloat()
                title = "Nitrogen Dioxide(NO₂)"
                screen = "Nitrogen Dioxide Quality Modal"
            }
            "NH3" -> {
                values = resources.getIntArray(R.array.airNHThreeValues)
                strings = resources.getStringArray(R.array.airNHThreeStrings)
                current = airPollution.list[0].components.nh3.toFloat()
                title = "Ammonia(NH₃)"
                screen = "Ammonia Quality Modal"
            }
        }

        AirItemFragment.newInstance(title,
            strings!!,
            values!!,
            current,
            airPollution.list[0].components)
            .showNow(parentFragmentManager, screen)
    }

    private fun setContentFound() {
        if (!isFetching) {
            return
        }

        binding.dailyForecastCard.isEnabled = true
        binding.hourlyForecastCard.isEnabled = true
        binding.currentCard.slideVisibility()
        binding.currentShimmer.slideVisibility()
        binding.currentShimmer.stopShimmer()
        binding.currentExtraCard.slideVisibility()
        binding.currentExtraShimmer.slideVisibility()
        binding.currentExtraShimmer.stopShimmer()
        binding.windCard.slideVisibility()
        binding.windShimmer.slideVisibility()
        binding.windShimmer.stopShimmer()
        binding.sunCard.slideVisibility()
        binding.sunShimmer.slideVisibility()
        binding.sunShimmer.stopShimmer()

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

        binding.currentCard.slideVisibility()
        binding.currentShimmer.slideVisibility()
        binding.currentShimmer.startShimmer()
        binding.hourlyForecastCard.isEnabled = false
        binding.currentExtraCard.slideVisibility()
        binding.currentExtraShimmer.slideVisibility()
        binding.currentExtraShimmer.startShimmer()
        binding.airCard.slideVisibility()
        binding.airShimmer.slideVisibility()
        binding.airShimmer.startShimmer()
        binding.windCard.slideVisibility()
        binding.windShimmer.slideVisibility()
        binding.windShimmer.startShimmer()
        binding.sunCard.slideVisibility()
        binding.sunShimmer.slideVisibility()
        binding.sunShimmer.startShimmer()
        binding.dailyForecastCard.isEnabled = false

        if (binding.rvHourlyForecast.isVisible) {
            binding.displayForecast.rotation = 0F
            binding.rvHourlyForecast.slideVisibility()
        }
        if (binding.rvDailyForecast.isVisible) {
            binding.displayDailyForecast.rotation = 0F
            binding.rvDailyForecast.slideVisibility()
        }

        isFetching = true
    }

    private fun View.slideVisibility(durationTime: Long = 300) {
        val transition = Slide(Gravity.BOTTOM)
        transition.apply {
            duration = durationTime
            addTarget(this@slideVisibility)
        }
        TransitionManager.beginDelayedTransition(this.parent as ViewGroup, transition)
        if (this.isVisible) {
            this.visibility = View.GONE
        } else {
            this.visibility = View.VISIBLE
        }
    }
}