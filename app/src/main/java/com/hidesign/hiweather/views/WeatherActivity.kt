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
import java.util.*
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
            Timber.tag("TAG").e("onActivityResult: PERMISSION DENIED")
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
                    SettingsDialog(this, this).show()
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
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(
                    AppUpdateType.IMMEDIATE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    this,
                    Constants.app_update)
            }
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

    private fun checkLocation() {
        if (LocationUtil.verifyPermissions(this)) {
            val activity = this
            launch {
                uAddress = LocationUtil.getLocation(activity)
                displayFragment(uAddress)
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
            binding.toolbar.title = address.locality ?: ""
            val list = supportFragmentManager.fragments
            for (frag in list) {
                if (frag is WeatherFragment) {
                    launch {
                        frag.fetchContent()
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