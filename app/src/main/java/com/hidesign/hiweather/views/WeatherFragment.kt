package com.hidesign.hiweather.views

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
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
import com.hidesign.hiweather.util.DateUtils
import com.hidesign.hiweather.util.DialogUtil
import com.hidesign.hiweather.util.WeatherUtils
import com.hidesign.hiweather.views.WeatherActivity.Companion.uAddress
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

    private lateinit var firebaseAnalytics: FirebaseAnalytics
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
        binding.swipeLayout.setOnRefreshListener { fetchContent() }

        LinearSnapHelper().attachToRecyclerView(binding.rvHourlyForecast)
        LinearSnapHelper().attachToRecyclerView(binding.rvDailyForecast)
        binding.hourlyForecastHeader.headerCard.setOnClickListener {
            if (binding.rvHourlyForecast.isVisible) {
                binding.rvHourlyForecast.slideVisibility(true)
            } else {
                binding.rvHourlyForecast.slideVisibility(false)
            }
            binding.hourlyForecastHeader.displayForecast.rotation =
                binding.hourlyForecastHeader.displayForecast.rotation + 180F
        }
        binding.dailyForecastHeader.headerCard.setOnClickListener {
            if (binding.rvDailyForecast.isVisible) {
                binding.rvDailyForecast.slideVisibility(true)
            } else {
                binding.rvDailyForecast.slideVisibility(false)
            }
            binding.dailyForecastHeader.displayForecast.rotation =
                binding.dailyForecastHeader.displayForecast.rotation + 180F
        }
        binding.hourlyForecastHeader.headerTitle.text = getString(R.string.hourly_forecast)
        binding.dailyForecastHeader.headerTitle.text = getString(R.string.daily_forecast)
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

    fun fetchContent() {
        if (uAddress == null) {
            Toast.makeText(requireContext(),
                "Something went wrong trying to get your location \n Please try again.",
                Toast.LENGTH_SHORT).show()
            val activity = requireActivity() as WeatherActivity
            activity.binding.vpContent.setCurrentItem(0, true)
            return
        }

        val df = SimpleDateFormat("d MMMM HH:mm", Locale.getDefault())
        val formattedDate = df.format(Calendar.getInstance().time)
        binding.dateHeader.date.text = formattedDate
        setFetchingContent()

        launch {
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
        }
    }

    private fun onWeatherSuccess(result: Response<OneCallResponse?>?) {
        if (result?.isSuccessful!!) {
            weather = result.body()!!

            //region Current Card
            binding.currentCard.CurrentTemp.text =
                MessageFormat.format(getString(R.string._0_c), weather.current.temp.roundToInt())
            binding.currentCard.HighTemp.text =
                MessageFormat.format(getString(R.string.high_0_c),
                    weather.daily[0].temp.max.roundToInt())
            binding.currentCard.LowTemp.text =
                MessageFormat.format(getString(R.string.low_0_c),
                    weather.daily[0].temp.min.roundToInt())
            binding.currentCard.RealFeelTemp.text =
                MessageFormat.format(getString(R.string.real_feel_0_c),
                    weather.current.feelsLike.roundToInt())
            //endregion Current Card

            //region Current Extra Card
            binding.currentExtraCard.skiesImage.setImageResource(WeatherUtils.getWeatherIcon(weather.current.weather[0].id))
            binding.currentExtraCard.Precipitation.text =
                MessageFormat.format(getString(R.string._0_p), (weather.daily[0].pop * 100))
            binding.currentExtraCard.Humidity.text =
                MessageFormat.format(getString(R.string.humidity_0), weather.current.humidity)
            binding.currentExtraCard.DewPoint.text =
                MessageFormat.format(getString(R.string.dew_point_0_c),
                    weather.current.dewPoint.roundToInt())
            binding.currentExtraCard.Pressure.text =
                MessageFormat.format(getString(R.string.pressure_0_mbar), weather.current.pressure)
            binding.currentExtraCard.UVIndex.text =
                MessageFormat.format(getString(R.string.uv_index_0),
                    weather.current.uvi.roundToInt())
            binding.currentExtraCard.Visibility.text =
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
                (weather.daily[0].sunrise).toLong(),
                weather.timezone)
            binding.sunCard.Sunset.text =
                DateUtils.getDateTime("HH:mm", (weather.daily[0].sunset).toLong(), weather.timezone)
            binding.sunCard.sunContent.setOnClickListener {
                ExpandedSunMoon.newInstance(weather.daily[0], weather.timezone)
                    .show(childFragmentManager, ExpandedSunMoon.TAG)
            }
            //endregion Sun Card

            setContentFound()
        } else {
            DialogUtil.displayErrorDialog(requireContext(), result.code(), result.message())
            return
        }
    }

    private fun onAirPollutionSuccess(result: Response<AirPollutionResponse?>?) {
        if (result?.isSuccessful!!) {
            airPollution = result.body()!!

            binding.airCard.airQuality.text = airPollution.list[0].main.aqi.toString()
            binding.airCard.airQualityText.apply {
                text = WeatherUtils.getAirQualityText(airPollution.list[0].main.aqi)
            }

            binding.airCard.airCo.apply {
                text = MessageFormat.format("CO - {0}",
                    airPollution.list[0].components.co.roundToDecimal())
                setOnClickListener { displayAirQualityItemFragment("Carbon Monoxide(CO)") }
            }
            binding.airCard.airNhThree.apply {
                text = MessageFormat.format("NH₃ - {0}",
                    airPollution.list[0].components.nh3.roundToDecimal())
                setOnClickListener { displayAirQualityItemFragment("Ammonia(NH₃)") }
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
                setOnClickListener { displayAirQualityItemFragment("Nitrogen Dioxide(NO₂)") }
            }
            binding.airCard.airOThree.apply {
                text = MessageFormat.format("O₃ - {0}",
                    airPollution.list[0].components.o3.roundToDecimal())
                setOnClickListener { displayAirQualityItemFragment("Ozone(O₃)") }
            }
            binding.airCard.airPmTen.apply {
                text = MessageFormat.format("PM₁₀ - {0}",
                    airPollution.list[0].components.pm10.roundToDecimal())
                setOnClickListener { displayAirQualityItemFragment("Coarse Particulate Matter(PM₁₀)") }
            }
            binding.airCard.airPmTwoFive.apply {
                text = MessageFormat.format("PM₂₅ - {0}",
                    airPollution.list[0].components.pm25.roundToDecimal())
                setOnClickListener { displayAirQualityItemFragment("Fine Particle Matter(PM₂₅)") }
            }
            binding.airCard.airSoTwo.apply {
                text = MessageFormat.format("SO₂ - {0}",
                    airPollution.list[0].components.so2.roundToDecimal())
                setOnClickListener {
                    setOnClickListener { displayAirQualityItemFragment("Sulphur Dioxide(SO₂)") }
                }
            }

            binding.airCard.airShimmer.slideVisibility(true)
            binding.airCard.airShimmer.stopShimmer()
            binding.airCard.airContent.slideVisibility(false)
        } else {
            DialogUtil.displayErrorDialog(requireContext(), result.code(), result.message())
            return
        }
    }

    private fun displayAirQualityItemFragment(item: String) {
        AirItemFragment.newInstance(item, airPollution.list[0].components)
            .showNow(parentFragmentManager, "Air Quality Item")
    }

    private fun setContentFound() {
        if (!isFetching) {
            return
        }

        binding.dailyForecastHeader.headerCard.isEnabled = true
        binding.hourlyForecastHeader.headerCard.isEnabled = true
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
        binding.hourlyForecastHeader.headerCard.isEnabled = false
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
        binding.dailyForecastHeader.headerCard.isEnabled = false

        if (binding.rvHourlyForecast.isVisible) {
            binding.hourlyForecastHeader.displayForecast.rotation = 0F
            binding.rvHourlyForecast.slideVisibility(true)
        }
        if (binding.rvDailyForecast.isVisible) {
            binding.dailyForecastHeader.displayForecast.rotation = 0F
            binding.rvDailyForecast.slideVisibility(true)
        }

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