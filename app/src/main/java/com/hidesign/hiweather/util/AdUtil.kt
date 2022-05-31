package com.hidesign.hiweather.util

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.ads.*

object AdUtil {
    const val appBarAdmobID = "ca-app-pub-1988108128017627/5605953771"
    const val bottomSheetAdmobID = "ca-app-pub-1988108128017627/8728990304"

    @SuppressLint("MissingPermission")
    fun setupAds(context: Context, id: String): AdView {
        MobileAds.initialize(context)
        val adView = AdView(context)
        adView.adSize = AdSize(AdSize.FULL_WIDTH, AdSize.AUTO_HEIGHT)
        adView.adUnitId = id
        adView.adListener = object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
            }
        }
        adView.loadAd(AdRequest.Builder().build())
        return adView
    }
}