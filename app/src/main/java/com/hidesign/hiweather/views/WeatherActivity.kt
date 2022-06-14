package com.hidesign.hiweather.views

import android.Manifest
import android.location.Address
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleObserver
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
import com.hidesign.hiweather.adapter.ViewPagerAdapter
import com.hidesign.hiweather.databinding.ActivityWeatherBinding
import com.hidesign.hiweather.util.AdUtil
import com.hidesign.hiweather.util.Constants
import com.hidesign.hiweather.util.Constants.getAPIKey
import com.hidesign.hiweather.util.LocationUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.CoroutineContext


class WeatherActivity : AppCompatActivity(), LifecycleObserver, CoroutineScope {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    lateinit var binding: ActivityWeatherBinding
    private lateinit var autocompleteSupportFragment: AutocompleteSupportFragment

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job

    val mPermissionResult = registerForActivityResult(RequestPermission()) { result ->
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
        binding.AdView.addView(AdUtil.setupAds(this, AdUtil.appBarId))
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
        val adapter = ViewPagerAdapter(this)
        binding.vpContent.adapter = adapter
        binding.vpContent.isUserInputEnabled = false
        checkLocation()
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
            if (binding.vpContent.currentItem != 1) {
                binding.vpContent.setCurrentItem(1, false)
            }
        } else {
            binding.toolbar.title = "Enter an Address"
            binding.linearProgress.visibility = View.INVISIBLE
            if (binding.vpContent.currentItem != 0) {
                binding.vpContent.setCurrentItem(0, false)
            }
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