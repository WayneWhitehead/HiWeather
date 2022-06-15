package com.hidesign.hiweather.util

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.ktx.Firebase
import com.google.firebase.perf.ktx.performance

object AdUtil {
    const val appBarId = "ca-app-pub-1988108128017627/5605953771"
    const val bottomSheetId = "ca-app-pub-1988108128017627/8728990304"

    private val myTrace = Firebase.performance.newTrace("FetchingAds")

    @SuppressLint("MissingPermission")
    fun setupAds(context: Context, id: String): AdView {
        myTrace.start()
        MobileAds.initialize(context)
        val adView = AdView(context)
        adView.setAdSize(AdSize(AdSize.FULL_WIDTH, AdSize.AUTO_HEIGHT))
        adView.adUnitId = id
        adView.loadAd(AdRequest.Builder().build())
        myTrace.stop()
        return adView
    }
}