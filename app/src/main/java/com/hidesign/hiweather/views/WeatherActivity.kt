package com.hidesign.hiweather.views

import android.Manifest
import android.content.Context
import android.location.Address
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleObserver
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.hidesign.hiweather.R
import com.hidesign.hiweather.databinding.ActivityWeatherBinding
import com.hidesign.hiweather.services.APIWorker
import com.hidesign.hiweather.util.AdUtil
import com.hidesign.hiweather.util.Constants
import com.hidesign.hiweather.util.Constants.getAPIKey
import com.hidesign.hiweather.util.DateUtils
import com.hidesign.hiweather.util.LocationUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

class WeatherActivity : AppCompatActivity(), LifecycleObserver, CoroutineScope {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    lateinit var binding: ActivityWeatherBinding
    private lateinit var autocompleteSupportFragment: AutocompleteSupportFragment

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

        autocompleteSupportFragment =
            (supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment?)!!
        setupPlacesAutoComplete()

        binding.bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_location -> {
                    mPermissionResult.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    mPermissionResult.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                    true
                }
                R.id.action_settings -> {
                    setupSettingsView()
                    true
                }
                else -> false
            }
        }
        binding.searchFAB.setOnClickListener {
            binding.autocompleteFragment.findViewById<EditText>(R.id.places_autocomplete_search_input)
                .performClick()
        }
        (binding.toolbar as View).setOnClickListener {
            binding.autocompleteFragment.findViewById<EditText>(R.id.places_autocomplete_search_input)
                .performClick()
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
        binding.AdView.addView(AdUtil.setupAds(this, AdUtil.appBarId))
    }

    private fun setupSettingsView() {
        binding.dialogSettings.settingsCard.visibility = View.VISIBLE
        binding.dialogSettings.close.setOnClickListener {
            binding.dialogSettings.settingsCard.visibility = View.GONE
        }

        ArrayAdapter.createFromResource(this,
            R.array.temperature_units,
            R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.dialogSettings.tempUnit.adapter = adapter
        }

        ArrayAdapter.createFromResource(this, R.array.refresh_interval, R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.dialogSettings.refreshInterval.adapter = adapter
        }

        binding.dialogSettings.tempUnit.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>?,
                    selectedItemView: View?,
                    position: Int,
                    id: Long,
                ) {
                    updateValues(resources.getStringArray(R.array.temperature_units)[position],
                        resources.getStringArray(R.array.temperature_units),
                        Constants.temperatureUnit)
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {
                    // your code here
                }
            }
        binding.dialogSettings.refreshInterval.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>?,
                    selectedItemView: View?,
                    position: Int,
                    id: Long,
                ) {
                    updateValues(resources.getStringArray(R.array.refresh_interval)[position],
                        resources.getStringArray(R.array.refresh_interval),
                        Constants.refreshInterval)
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {
                    // your code here
                }
            }

        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val posTemp = sharedPref.getInt(Constants.temperatureUnit, 0)
        val posRefresh = sharedPref.getInt(Constants.refreshInterval, 0)
        binding.dialogSettings.tempUnit.setSelection(posTemp)
        binding.dialogSettings.refreshInterval.setSelection(posRefresh)
        updateValues(resources.getStringArray(R.array.temperature_units)[posTemp],
            resources.getStringArray(R.array.temperature_units),
            Constants.temperatureUnit)
        updateValues(resources.getStringArray(R.array.refresh_interval)[posRefresh],
            resources.getStringArray(R.array.refresh_interval),
            Constants.refreshInterval)
    }

    private fun updateValues(item: String, values: Array<String>, preference: String) {
        for ((pos, value) in values.withIndex()) {
            if (item == value) {
                val sharedPref = getPreferences(Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putInt(preference, pos)
                    apply()
                }
                val timeValue = DateUtils.getRefreshInterval(this)
                val periodicWorkRequest = PeriodicWorkRequest.Builder(APIWorker::class.java,
                    timeValue,
                    TimeUnit.MINUTES,
                    timeValue,
                    TimeUnit.MINUTES).build()
                WorkManager.getInstance(this).enqueueUniquePeriodicWork("APIWorker",
                    ExistingPeriodicWorkPolicy.REPLACE,
                    periodicWorkRequest)
            }
        }
    }

    private fun checkLocation() {
        if (LocationUtil.verifyPermissions(this)) {
            val activity = this
            launch {
                val address = LocationUtil.getLocation(activity)
                displayFragment(address)
            }
        } else {
            mPermissionResult.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            mPermissionResult.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
            binding.linearProgress.visibility = View.INVISIBLE
        }
    }

    private fun displayFragment(address: Address?) {
        binding.bottomAppBar.performShow()
        if (address != null) {
            uAddress = address
            binding.toolbar.title = address.locality ?: ""
            val list = supportFragmentManager.fragments
            for (frag in list) {
                if (frag.tag == "f1") {
                    val fragment =
                        supportFragmentManager.findFragmentByTag("f1") as WeatherFragment
                    launch {
                        fragment.fetchContent()
                    }
                    return
                }
            }
            supportFragmentManager.beginTransaction()
                .add(binding.fragment.id, WeatherFragment())
                .commitNow()
        } else {
            binding.toolbar.title = "Enter an Address"
            binding.linearProgress.visibility = View.INVISIBLE
        }
    }

    private fun setupPlacesAutoComplete() {
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext,
                getAPIKey(applicationContext, Constants.placesKey))
        }

        autocompleteSupportFragment.setTypeFilter(TypeFilter.CITIES)
        autocompleteSupportFragment.setPlaceFields(listOf(Place.Field.NAME, Place.Field.LAT_LNG))
        autocompleteSupportFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onError(p0: Status) {
                displayFragment(null)
            }

            override fun onPlaceSelected(place: Place) {
                val address = Address(Locale.getDefault()).apply {
                    latitude = place.latLng?.latitude ?: 0.0
                    longitude = place.latLng?.longitude ?: 0.0
                    locality = place.name
                }
                displayFragment(address)
            }
        })
    }

    companion object {
        const val TAG = "Weather Activity"
        var uAddress: Address? = null
    }
}