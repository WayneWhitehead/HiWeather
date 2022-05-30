package com.hidesign.hiweather.views

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.*
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.hidesign.hiweather.R

class EmptyView : Fragment() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        firebaseAnalytics = Firebase.analytics
        return inflater.inflate(R.layout.fragment_empty_view, container, false)
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
        val adView = requireView().findViewById<AdView>(R.id.emptyViewAd)
        adView.addView(setupAds())
    }

    @SuppressLint("MissingPermission")
    private fun setupAds(): AdView {
        MobileAds.initialize(requireContext())
        val adView = AdView(requireContext())
        adView.adSize = AdSize(AdSize.FULL_WIDTH, AdSize.AUTO_HEIGHT)
        adView.adUnitId = "ca-app-pub-1988108128017627/4633391685"
        adView.adListener = object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
            }
        }
        adView.loadAd(AdRequest.Builder().build())
        return adView
    }

    companion object {
        const val TAG = "Empty View"

        @JvmStatic
        fun newInstance() =
            EmptyView().apply {
            }
    }
}