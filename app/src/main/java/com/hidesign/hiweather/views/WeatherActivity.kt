package com.hidesign.hiweather.views

import android.Manifest
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleObserver
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.hidesign.hiweather.R
import com.hidesign.hiweather.databinding.ActivityWeatherBinding
import com.hidesign.hiweather.util.AdUtil
import com.hidesign.hiweather.util.LocationUtil
import com.hidesign.hiweather.views.SplashScreenActivity.Companion.uAddress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.CoroutineContext


class WeatherActivity : AppCompatActivity(), LifecycleObserver, CoroutineScope {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    lateinit var binding: ActivityWeatherBinding
    private lateinit var autocompleteSupportFragment1: AutocompleteSupportFragment

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job

    private val mPermissionResult = registerForActivityResult(RequestPermission()) { result ->
        if (result) {
            binding.linearProgress.visibility = View.VISIBLE
            checkLocation()
        } else {
            binding.linearProgress.visibility = View.INVISIBLE
            Log.e("TAG", "onActivityResult: PERMISSION DENIED")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        binding.linearProgress.visibility = View.VISIBLE
        setContentView(binding.root)
        firebaseAnalytics = Firebase.analytics

        autocompleteSupportFragment1 =
            (supportFragmentManager.findFragmentById(R.id.autocomplete_fragment1) as AutocompleteSupportFragment?)!!
        binding.toolbarLayout.titleCollapseMode = CollapsingToolbarLayout.TITLE_COLLAPSE_MODE_SCALE
        binding.toolbarLayout.setContentScrimColor(getColor(R.color.colorAccentLight))

        binding.nativeAd.addView(AdUtil.setupAds(this, AdUtil.appBarAdmobID))
        setupPlacesAutoComplete()

        if (uAddress == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(binding.contentFrame.id, EmptyView.newInstance())
                .commit()
        } else {
            supportFragmentManager
                .beginTransaction()
                .replace(binding.contentFrame.id, WeatherFragment.newInstance(uAddress!!))
                .commit()
        }
        binding.precisionLocation.setOnClickListener {
            mPermissionResult.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            mPermissionResult.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }

    override fun onResume() {
        super.onResume()
        Bundle().apply {
            this.putString(FirebaseAnalytics.Event.SCREEN_VIEW, TAG)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, this)
        }
    }

    override fun onStart() {
        super.onStart()
        checkLocation()
    }

    private fun checkLocation() {
        if (LocationUtil.verifyPermissions(this)) {
            getLocation()
        }
    }

    private fun getLocation() {
        val activity = this
        launch {
            val address = LocationUtil.getLocation(activity)
            if (address != null) {
                autocompleteSupportFragment1.view?.findViewById<EditText>(R.id.places_autocomplete_search_input)
                    ?.setText(address.locality)
                autocompleteSupportFragment1.view?.findViewById<EditText>(R.id.places_autocomplete_search_input)
                    ?.setTextColor(Color.WHITE)
                supportFragmentManager
                    .beginTransaction()
                    .replace(binding.contentFrame.id, WeatherFragment.newInstance(address))
                    .commit()
            } else if (uAddress != null) {
                autocompleteSupportFragment1.view?.findViewById<EditText>(R.id.places_autocomplete_search_input)
                    ?.setText(uAddress!!.locality)
                autocompleteSupportFragment1.view?.findViewById<EditText>(R.id.places_autocomplete_search_input)
                    ?.setTextColor(Color.WHITE)
                supportFragmentManager
                    .beginTransaction()
                    .replace(binding.contentFrame.id, WeatherFragment.newInstance(uAddress!!))
                    .commit()
            } else {
                supportFragmentManager
                    .beginTransaction()
                    .replace(binding.contentFrame.id, EmptyView.newInstance())
                    .commit()
                binding.linearProgress.visibility = View.INVISIBLE
            }
        }
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
            autocompleteSupportFragment1.view?.findViewById<EditText>(R.id.places_autocomplete_search_input)
                ?.setTextColor(getColor(R.color.black))
        }
        autocompleteSupportFragment1.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onError(p0: Status) {
                Toast.makeText(applicationContext,
                    "Some error occurred " + p0.statusMessage,
                    Toast.LENGTH_SHORT).show()
            }

            override fun onPlaceSelected(place: Place) {
                val latlng = place.latLng

                val selectedAddress = Address(Locale.getDefault()).apply {
                    latitude = latlng!!.latitude
                    longitude = latlng.longitude
                }
                if (latlng != null) {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(binding.contentFrame.id,
                            WeatherFragment.newInstance(selectedAddress))
                        .commit()
                }
                autocompleteSupportFragment1.view?.findViewById<EditText>(R.id.places_autocomplete_search_input)
                    ?.setTextColor(getColor(R.color.white))
            }
        })
    }

    companion object {
        const val TAG = "Weather Activity"
    }
}