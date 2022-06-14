package com.hidesign.hiweather.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.hidesign.hiweather.databinding.ExpandedSunMoonBinding
import com.hidesign.hiweather.model.Daily
import com.hidesign.hiweather.util.AdUtil
import com.hidesign.hiweather.util.DateUtils
import com.hidesign.hiweather.util.WeatherUtils.getMoonIcon

class ExpandedSunMoon : BottomSheetDialogFragment() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var binding: ExpandedSunMoonBinding
    private lateinit var weatherDaily: Daily
    private var timeZone = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = ExpandedSunMoonBinding.inflate(inflater)
        firebaseAnalytics = Firebase.analytics

        binding.Sunrise.text =
            DateUtils.getDateTime("HH:mm", (weatherDaily.sunrise)!!.toLong(), timeZone)
        binding.Sunset.text =
            DateUtils.getDateTime("HH:mm", (weatherDaily.sunset)!!.toLong(), timeZone)
        binding.hoursOfSunlight.text =
            DateUtils.getHours(weatherDaily.sunrise!!.toLong(), weatherDaily.sunset!!.toLong())

        binding.Moonrise.text =
            DateUtils.getDateTime("HH:mm", (weatherDaily.moonrise)!!.toLong(), timeZone)
        binding.moonPhase.setImageResource(getMoonIcon(weatherDaily.moonPhase!!))
        binding.Moonset.text =
            DateUtils.getDateTime("HH:mm", (weatherDaily.moonset)!!.toLong(), timeZone)
        binding.hoursOfMoonlight.text =
            DateUtils.getHours(weatherDaily.moonrise!!.toLong(), weatherDaily.moonset!!.toLong())

        binding.nativeAd.addView(AdUtil.setupAds(requireContext(), AdUtil.bottomSheetId))
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        Bundle().apply {
            this.putString(FirebaseAnalytics.Event.SCREEN_VIEW, TAG)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, this)
        }
    }

    companion object {
        const val TAG = "Sun & Moon BottomSheet"

        @JvmStatic
        fun newInstance(daily: Daily, tz: String) =
            ExpandedSunMoon().apply {
                arguments = Bundle().apply {
                    weatherDaily = daily
                    timeZone = tz
                }
            }
    }
}