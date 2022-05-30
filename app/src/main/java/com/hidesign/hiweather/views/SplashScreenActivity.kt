package com.hidesign.hiweather.views

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Address
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.hidesign.hiweather.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = Firebase.analytics

        val i = Intent(this@SplashScreenActivity, WeatherActivity::class.java)
        Thread.sleep(1000)
        startActivity(i)
        overridePendingTransition(R.anim.slide_up, R.anim.slide_up_out)
        finish()
    }

    override fun onResume() {
        super.onResume()
        Bundle().apply {
            this.putString(FirebaseAnalytics.Event.SCREEN_VIEW, TAG)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, this)
        }
    }

    companion object {
        @JvmField
        var uAddress: Address? = null
        const val TAG: String = "Splash Screen Activity"
    }
}