package com.hidesign.hiweather.util

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

object AdUtil {
    const val APP_BAR_AD = "ca-app-pub-1988108128017627/5605953771"
    const val BOTTOM_SHEET_AD = "ca-app-pub-1988108128017627/8728990304"

    @SuppressLint("MissingPermission")
    fun setupAds(context: Context, id: String): AdView {
        MobileAds.initialize(context)
        val adView = AdView(context)
        adView.setAdSize(AdSize(AdSize.FULL_WIDTH, AdSize.AUTO_HEIGHT))
        adView.adUnitId = id
        adView.loadAd(AdRequest.Builder().build())
        return adView
    }
}