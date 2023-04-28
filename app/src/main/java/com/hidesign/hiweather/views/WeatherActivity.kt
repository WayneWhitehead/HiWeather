package com.hidesign.hiweather.views

import android.Manifest
import android.content.Context
import android.location.Address
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleObserver
import androidx.work.WorkManager
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.hidesign.hiweather.R
import com.hidesign.hiweather.databinding.ActivityWeatherBinding
import com.hidesign.hiweather.services.APIWorker
import com.hidesign.hiweather.util.AdUtil
import com.hidesign.hiweather.util.Constants
import com.hidesign.hiweather.util.Constants.getAPIKey
import com.hidesign.hiweather.util.LocationUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Locale
import kotlin.coroutines.CoroutineContext

class WeatherActivity : AppCompatActivity(), LifecycleObserver, CoroutineScope {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var autocompleteSupportFragment: AutocompleteSupportFragment

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job

    private val mPermissionResult = registerForActivityResult(RequestPermission()) { result ->
        if (result) {
            binding.linearProgress.visibility = View.VISIBLE
            checkLocation()
        } else {
            binding.linearProgress.visibility = View.INVISIBLE
            Timber.tag("TAG").e("onActivityResult: PERMISSION DENIED")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        binding.linearProgress.visibility = View.VISIBLE
        setContentView(binding.root)
        firebaseAnalytics = Firebase.analytics

        autocompleteSupportFragment = (supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment?)!!
        setupPlacesAutoComplete()

        binding.bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_location -> {
                    mPermissionResult.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    mPermissionResult.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                    true
                }
                R.id.action_settings -> {
                    SettingsDialog(this).show()
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
        WorkManager.getInstance(this).cancelAllWork()
        launch {
            val sharedPref = getPreferences(Context.MODE_PRIVATE)
            APIWorker.createWorkManagerInstance(this@WeatherActivity,
                sharedPref.getInt(Constants.refreshInterval, 0))
            Timber.tag("TAG").d("onCreate: WORKER STARTED")
        }

        val appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    this,
                    Constants.app_update)
            }
        }

        binding.bottomAppBar.performShow()
        supportFragmentManager.beginTransaction()
            .add(binding.fragment.id, weatherFragment)
            .commitNow()
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

    private fun checkLocation() {
        if (LocationUtil.verifyPermissions(this)) {
            val activity = this
            launch {
                LocationUtil.getLocation(activity)
            }
        } else {
            mPermissionResult.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            mPermissionResult.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
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

            }

            override fun onPlaceSelected(place: Place) {
                Address(Locale.getDefault()).apply {
                    latitude = place.latLng?.latitude ?: 0.0
                    longitude = place.latLng?.longitude ?: 0.0
                    locality = place.name
                    uAddress = this
                }
            }
        })
    }

    fun setFetchingContent() {
        binding.linearProgress.visibility = View.VISIBLE
    }

    fun stopFetchingContent() {
        binding.linearProgress.visibility = View.INVISIBLE
    }

    companion object {
        const val TAG = "Weather Activity"
        val weatherFragment: WeatherFragment = WeatherFragment()
        lateinit var binding: ActivityWeatherBinding
        var uAddress: Address? = null
            set(value) {
                if (value != null) {
                    field = value
                    binding.toolbar.title = value.locality ?: ""
                    CoroutineScope(Dispatchers.Main).launch {
                        weatherFragment.fetchContent()
                    }
                } else {
                    binding.toolbar.title = "Enter an Address"
                    binding.linearProgress.visibility = View.INVISIBLE
                }
            }
    }
}