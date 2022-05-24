package com.hidesign.hiweather.views

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.location.Address
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearSnapHelper
import com.google.android.gms.ads.*
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.hidesign.hiweather.R
import com.hidesign.hiweather.adapter.DailyRecyclerAdapter
import com.hidesign.hiweather.adapter.HourlyRecyclerAdapter
import com.hidesign.hiweather.databinding.ActivityWeatherBinding
import com.hidesign.hiweather.model.*
import com.hidesign.hiweather.network.WeatherViewModel
import com.hidesign.hiweather.util.DateUtils
import com.hidesign.hiweather.util.DialogUtil.displayErrorDialog
import com.hidesign.hiweather.util.DialogUtil.displayInfoDialog
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

class WeatherActivity : AppCompatActivity(), CoroutineScope, LifecycleObserver {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var weather: OneCallResponse
    private lateinit var airPollution: AirPollutionResponse
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var binding: ActivityWeatherBinding
    private var isFetching = false
    private var uAddress: Address? = SplashScreenActivity.uAddress
    private lateinit var autocompleteSupportFragment1: AutocompleteSupportFragment

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onStart() {
        super.onStart()
        fetchContent()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAnalytics = Firebase.analytics
        weatherViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)

        val df = SimpleDateFormat("dd MMMM", Locale.getDefault())
        val formattedDate = df.format(Calendar.getInstance().time)
        binding.content.date.text = formattedDate
        binding.toolbarLayout.titleCollapseMode = CollapsingToolbarLayout.TITLE_COLLAPSE_MODE_SCALE
        binding.toolbarLayout.setContentScrimColor(getColor(R.color.colorAccentLight))

        LinearSnapHelper().attachToRecyclerView(binding.content.rvHourlyForecast)
        LinearSnapHelper().attachToRecyclerView(binding.content.rvDailyForecast)
        binding.content.hourlyForecastCard.setOnClickListener {
            binding.content.rvHourlyForecast.slideVisibility()
            binding.content.displayForecast.rotation = binding.content.displayForecast.rotation + 180F
        }
        binding.content.dailyForecastCard.setOnClickListener {
            binding.content.rvDailyForecast.slideVisibility()
            binding.content.displayDailyForecast.rotation = binding.content.displayDailyForecast.rotation + 180F
        }
        binding.content.swipeLayout.setOnRefreshListener { fetchContent() }
        autocompleteSupportFragment1 = (supportFragmentManager.findFragmentById(R.id.autocomplete_fragment1) as AutocompleteSupportFragment?)!!

        binding.nativeAd.addView(setupAds())
        setupPlacesAutoComplete()
        setFetchingContent()
    }

    @SuppressLint("MissingPermission")
    private fun setupAds(): AdView{
        MobileAds.initialize(this) { }
        val adView = AdView(this)
        adView.adSize = AdSize(AdSize.FULL_WIDTH, AdSize.AUTO_HEIGHT)
        adView.adUnitId = "ca-app-pub-1988108128017627/5605953771"
        adView.adListener = object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
            }
        }
        adView.loadAd(AdRequest.Builder().build())
        return adView
    }

    private fun setupPlacesAutoComplete() {
        val ai: ApplicationInfo = applicationContext.packageManager
            .getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)
        val value = ai.metaData["placesKey"]
        val apiKey = value.toString()
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }

        autocompleteSupportFragment1.setTypeFilter(TypeFilter.CITIES)
        autocompleteSupportFragment1.setPlaceFields(
            listOf(
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG,
            )
        )
        autocompleteSupportFragment1.view?.setOnClickListener {
            autocompleteSupportFragment1.view?.findViewById<EditText>(R.id.places_autocomplete_search_input)?.setTextColor(getColor(R.color.black))
        }
        autocompleteSupportFragment1.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onError(p0: Status) {
                Toast.makeText(applicationContext,"Some error occurred " + p0.statusMessage, Toast.LENGTH_SHORT).show()
            }

            override fun onPlaceSelected(place: Place) {
                val latlng = place.latLng

                val selectedAddress = Address(Locale.getDefault()).apply {
                    latitude = latlng!!.latitude
                    longitude = latlng.longitude
                }
                if (latlng != null) {
                    uAddress = selectedAddress
                    fetchContent()
                }
                autocompleteSupportFragment1.view?.findViewById<EditText>(R.id.places_autocomplete_search_input)?.setTextColor(getColor(R.color.white))
            }
        })
    }

    private fun fetchContent() {
        if (uAddress == null) {
            autocompleteSupportFragment1.setHint("Please Enter a City")
            Toast.makeText(applicationContext,"Please Enter a City in the search bar above ", Toast.LENGTH_SHORT).show()
            binding.autocompleteFragment1.performClick()
            return
        }

        setFetchingContent()

        launch {
            if (uAddress != null) {
                autocompleteSupportFragment1.view?.findViewById<EditText>(R.id.places_autocomplete_search_input)?.setText(uAddress!!.locality)
                autocompleteSupportFragment1.view?.findViewById<EditText>(R.id.places_autocomplete_search_input)?.setTextColor(getColor(R.color.white))
            }

            val ai: ApplicationInfo = applicationContext.packageManager
                .getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)
            val value = ai.metaData["weatherKey"]
            val apiKey = value.toString()

            val oneCallResponse = weatherViewModel.getOneCallWeather(uAddress!!.latitude, uAddress!!.longitude, apiKey)
            val airPollutionResponse = weatherViewModel.getAirPollution(uAddress!!.latitude, uAddress!!.longitude, apiKey)

            binding.content.swipeLayout.isRefreshing = false
            onWeatherSuccess(oneCallResponse)
            onAirPollutionSuccess(airPollutionResponse)
        }
    }

    private fun onWeatherSuccess(result: Response<OneCallResponse?>?) {
        if (result?.isSuccessful!!) {
            weather = result.body()!!

            binding.content.CurrentTemp.text = MessageFormat.format(getString(R.string._0_c), weather.current.temp.roundToInt())
            binding.content.HighTemp.text = MessageFormat.format(getString(R.string.high_0_c), weather.daily[0].temp.max.roundToInt())
            binding.content.LowTemp.text = MessageFormat.format(getString(R.string.low_0_c), weather.daily[0].temp.min.roundToInt())
            binding.content.RealFeelTemp.text = MessageFormat.format(getString(R.string.real_feel_0_c), weather.current.feelsLike.roundToInt())
            binding.content.Precipitation.text = MessageFormat.format(getString(R.string.precipitation_0), weather.daily[0].pop * 100)
            binding.content.Humidity.text = MessageFormat.format(getString(R.string.humidity_0), weather.current.humidity)
            binding.content.DewPoint.text = MessageFormat.format(getString(R.string.dew_point_0_c), weather.current.dewPoint.roundToInt())
            binding.content.Pressure.text = MessageFormat.format(getString(R.string.pressure_0_mbar), weather.current.pressure)
            binding.content.UVIndex.text = MessageFormat.format(getString(R.string.uv_index_0), weather.current.uvi.roundToInt())
            binding.content.Visibility.text = MessageFormat.format(getString(R.string.visibility_0_m), weather.current.visibility)
            binding.content.WindSpeed.text = String.format(weather.current.windSpeed.toBigDecimal().setScale(1, RoundingMode.HALF_EVEN).toString())
            binding.content.WindDirectionDegrees.rotation = (weather.current.windDeg - 270).toFloat()
            binding.content.WindDirectionText.text = Wind.getWindDegreeText(weather.current.windDeg)
            binding.content.skiesImage.setImageResource(WeatherIcon.getIcon(weather.current.weather[0].id))

            val hourlyForecast: ArrayList<Hourly> = ArrayList()
            for (i in 1..10) {
                hourlyForecast.add(weather.hourly[i])
            }
            binding.content.displayForecast.isEnabled = true
            val hourlyAdapter = HourlyRecyclerAdapter(this, hourlyForecast)
            hourlyAdapter.onItemClick = { hourly: Hourly, _: View ->
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
                    param(FirebaseAnalytics.Event.SCREEN_VIEW, "ExpandedForecastCard")
                }
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                    param(FirebaseAnalytics.Param.ITEM_ID, hourly.dt.toString())
                    param(FirebaseAnalytics.Param.ITEM_NAME, ExpandedForecast.TAG)
                    param(FirebaseAnalytics.Param.ITEM_CATEGORY, "Hourly")
                    param(FirebaseAnalytics.Param.CONTENT_TYPE, "Forecast")
                }
                ExpandedForecast.newInstance(hourly).show(supportFragmentManager, ExpandedForecast.TAG)
            }
            binding.content.rvHourlyForecast.adapter = hourlyAdapter

            val dailyForecast: ArrayList<Daily> = ArrayList()
            dailyForecast.addAll(weather.daily)
            val dailyAdapter = DailyRecyclerAdapter(this, dailyForecast)
            dailyAdapter.onItemClick = {
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
                    param(FirebaseAnalytics.Event.SCREEN_VIEW, "ExpandedForecastCard")
                }
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                    param(FirebaseAnalytics.Param.ITEM_ID, it.dt.toString())
                    param(FirebaseAnalytics.Param.ITEM_NAME, ExpandedForecast.TAG)
                    param(FirebaseAnalytics.Param.ITEM_CATEGORY, "Daily")
                    param(FirebaseAnalytics.Param.CONTENT_TYPE, "Forecast")
                }
                ExpandedForecast.newInstance(it).show(supportFragmentManager, ExpandedForecast.TAG)
            }
            binding.content.rvDailyForecast.adapter = dailyAdapter

            binding.content.sunCard.setOnClickListener {
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
                    param(FirebaseAnalytics.Event.SCREEN_VIEW, "Sun&MoonExpandedCard")
                }
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                    param(FirebaseAnalytics.Param.ITEM_ID, weather.current.dt.toString())
                    param(FirebaseAnalytics.Param.ITEM_NAME, ExpandedSunMoon.TAG)
                    param(FirebaseAnalytics.Param.CONTENT_TYPE, "SunCard")
                }
                ExpandedSunMoon.newInstance(weather.daily[0], weather.current.weather[0].description, weather.current.uvi).show(supportFragmentManager, ExpandedSunMoon.TAG)
            }
            binding.content.Sunrise.text = DateUtils.getDateTime("HH:mm", (weather.daily[0].sunrise).toLong())
            binding.content.Sunset.text = DateUtils.getDateTime("HH:mm", (weather.daily[0].sunset).toLong())

            setContentFound()
        } else {
            displayErrorDialog(this, result.code(), result.message())
            return
        }
    }

    private fun onAirPollutionSuccess(result: Response<AirPollutionResponse?>?) {
        if (result?.isSuccessful!!) {
            airPollution = result.body()!!

            binding.content.airQuality.text = airPollution.list[0].main.aqi.toString()
            binding.content.airResponseCard.setOnClickListener {
                displayInfoDialog(this,
                    "Air Quality - " + airPollution.list[0].main.aqi,
                    getString(R.string.airQualityInfo))
            }

            binding.content.airCo.text =
                MessageFormat.format("CO - {0}", airPollution.list[0].components.co.roundToInt())
            binding.content.airCo.setOnClickListener {
                displayInfoDialog(this,
                    "Carbon monoxide - " + (airPollution.list[0].components.co).roundToInt() + " µg/m³",
                    getString(R.string.airCo))
            }
            binding.content.airNhThree.text =
                MessageFormat.format("NH₃ - {0}", airPollution.list[0].components.nh3)
            binding.content.airPmTen.setOnClickListener {
                displayInfoDialog(this,
                    "Ammonia - " + (airPollution.list[0].components.nh3) + " µg/m³",
                    getString(R.string.airNHThree))
            }
            binding.content.airNo.text =
                MessageFormat.format("NO - {0}", airPollution.list[0].components.no)
            binding.content.airNoTwo.text =
                MessageFormat.format("NO₂ - {0}", airPollution.list[0].components.no2)
            binding.content.airNoTwo.setOnClickListener {
                displayInfoDialog(this,
                    " Nitrogen Dioxide - " + (airPollution.list[0].components.no2).toBigDecimal()
                        .setScale(2, RoundingMode.HALF_EVEN) + " µg/m³",
                    getString(R.string.airNOTwo))
            }
            binding.content.airOThree.text =
                MessageFormat.format("O₃ - {0}", airPollution.list[0].components.o3)
            binding.content.airOThree.setOnClickListener {
                displayInfoDialog(this,
                    " Ozone - " + (airPollution.list[0].components.o3) + " µg/m³",
                    getString(R.string.airOThree))
            }
            binding.content.airPmTen.text =
                MessageFormat.format("PM₁₀ - {0}", airPollution.list[0].components.pm10)
            binding.content.airPmTen.setOnClickListener {
                displayInfoDialog(this,
                    "Course Particulate Matter - " + (airPollution.list[0].components.pm10) + " µg/m³",
                    getString(R.string.airPMTen))
            }
            binding.content.airPmTwoFive.text = MessageFormat.format("PM₂₅ - {0}",
                airPollution.list[0].components.pm25.toBigDecimal()
                    .setScale(2, RoundingMode.HALF_EVEN))
            binding.content.airPmTwoFive.setOnClickListener {
                displayInfoDialog(this,
                    "Fine Particle Matter - " + (airPollution.list[0].components.pm25).toBigDecimal()
                        .setScale(2, RoundingMode.HALF_EVEN) + " µg/m³",
                    getString(R.string.airPMTwoFive))
            }
            binding.content.airSoTwo.text =
                MessageFormat.format("SO₂ - {0}", airPollution.list[0].components.so2)
            binding.content.airSoTwo.setOnClickListener {
                displayInfoDialog(this,
                    "Sulfur dioxide - " + (airPollution.list[0].components.so2).toBigDecimal()
                        .setScale(2, RoundingMode.HALF_EVEN) + " µg/m³",
                    getString(R.string.airSoTwo))
            }

            binding.content.airShimmer.slideVisibility()
            binding.content.airCard.slideVisibility()
        } else {
            displayErrorDialog(this, result.code(), result.message())
            return
        }
    }

    private fun setContentFound() {
        if (!isFetching) {
            return
        }
        binding.content.dailyForecastCard.isEnabled = true
        binding.content.hourlyForecastCard.isEnabled = true
        binding.content.currentExtraCard.slideVisibility()
        binding.content.currentExtraShimmer.slideVisibility()
        binding.content.currentCard.slideVisibility()
        binding.content.currentShimmer.slideVisibility()
        binding.content.sunCard.slideVisibility()
        binding.content.sunShimmer.slideVisibility()
        binding.content.windCard.slideVisibility()
        binding.content.windShimmer.slideVisibility()

        isFetching = false
    }

    private fun setFetchingContent(){
        if (isFetching) {
            return
        }
        binding.content.airCard.slideVisibility()
        binding.content.airShimmer.slideVisibility()
        binding.content.dailyForecastCard.isEnabled = false
        binding.content.hourlyForecastCard.isEnabled = false
        binding.content.currentExtraCard.slideVisibility()
        binding.content.currentExtraShimmer.slideVisibility()
        binding.content.currentCard.slideVisibility()
        binding.content.currentShimmer.slideVisibility()
        binding.content.sunCard.slideVisibility()
        binding.content.sunShimmer.slideVisibility()
        binding.content.windCard.slideVisibility()
        binding.content.windShimmer.slideVisibility()

        if (binding.content.rvHourlyForecast.isVisible) {
            binding.content.rvHourlyForecast.slideVisibility()
        }
        if (binding.content.rvDailyForecast.isVisible) {
            binding.content.rvDailyForecast.slideVisibility()
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