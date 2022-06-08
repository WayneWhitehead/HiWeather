package com.hidesign.hiweather.views

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.hidesign.hiweather.R
import com.hidesign.hiweather.databinding.FragmentEmptyViewBinding
import com.hidesign.hiweather.util.AdUtil
import com.hidesign.hiweather.util.AdUtil.setupAds

class EmptyView : Fragment() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var binding: FragmentEmptyViewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentEmptyViewBinding.inflate(layoutInflater)
        binding.locationButton.setOnClickListener {
            val activity = requireActivity() as WeatherActivity
            activity.mPermissionResult.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            activity.mPermissionResult.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        binding.searchButton.setOnClickListener {
            val activity = requireActivity() as WeatherActivity
            activity.binding.autocompleteFragment.findViewById<EditText>(R.id.places_autocomplete_search_input)
                .performClick()
        }
        return binding.root
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
        firebaseAnalytics = Firebase.analytics
        val adView = requireView().findViewById<AdView>(R.id.emptyViewAd)
        adView.addView(setupAds(requireContext(), AdUtil.emptyViewId))
    }

    companion object {
        const val TAG = "Empty View"

    }
}