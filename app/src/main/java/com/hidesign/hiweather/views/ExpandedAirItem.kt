package com.hidesign.hiweather.views

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.hidesign.hiweather.R
import com.hidesign.hiweather.databinding.ExpandedAirItemBinding
import com.hidesign.hiweather.model.Components
import com.hidesign.hiweather.util.Constants
import com.hidesign.hiweather.util.WeatherUtils
import com.hookedonplay.decoviewlib.charts.SeriesItem
import com.hookedonplay.decoviewlib.events.DecoEvent
import java.text.MessageFormat


class ExpandedAirItem : BottomSheetDialogFragment() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var binding: ExpandedAirItemBinding
    private lateinit var airStrings: Array<String>
    private lateinit var airValues: IntArray
    private var currentValue = 0F
    private var title = ""
    private lateinit var components: Components

    override fun onStart() {
        super.onStart()
        firebaseAnalytics = Firebase.analytics
        updateValues(title)
        setAirItemValues()
        binding.airPicker.setOnValueChangedListener { _, _, newVal ->
            updateValues(resources.getStringArray(R.array.airTitles)[newVal])
            setAirItemValues()
        }
    }

    override fun onResume() {
        super.onResume()
        Bundle().apply {
            this.putString(FirebaseAnalytics.Event.SCREEN_VIEW, TAG)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, this)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = ExpandedAirItemBinding.inflate(inflater, container, false)
        binding.airPicker.minValue = 0
        binding.airPicker.maxValue = resources.getStringArray(R.array.airTitles).size - 1
        binding.airPicker.displayedValues = resources.getStringArray(R.array.airTitles)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            binding.airPicker.selectionDividerHeight = 0
        }

        for ((pos, item) in resources.getStringArray(R.array.airTitles).withIndex()) {
            if (item == title) {
                binding.airPicker.value = pos
            }
        }
        return binding.root
    }

    private fun updateValues(item: String) {
        when (item) {
            Constants.carbon_monoxide -> {
                airValues = resources.getIntArray(R.array.airCoValues)
                airStrings = resources.getStringArray(R.array.airStrings)
                currentValue = components.co.toFloat()
                title = item
            }
            Constants.sulphur_dioxide -> {
                airValues = resources.getIntArray(R.array.airSoTwoValues)
                airStrings = resources.getStringArray(R.array.airStrings)
                currentValue = components.so2.toFloat()
                title = item
            }
            Constants.fine_particle_matter -> {
                airValues = resources.getIntArray(R.array.airPMTwoFiveValues)
                airStrings = resources.getStringArray(R.array.airStrings)
                currentValue = components.pm25.toFloat()
                title = item
            }
            Constants.coarse_particle_matter -> {
                airValues = resources.getIntArray(R.array.airPMTenValues)
                airStrings = resources.getStringArray(R.array.airStrings)
                currentValue = components.pm10.toFloat()
                title = item
            }
            Constants.ozone -> {
                airValues = resources.getIntArray(R.array.airOThreeValues)
                airStrings = resources.getStringArray(R.array.airStrings)
                currentValue = components.o3.toFloat()
                title = item
            }
            Constants.nitrogen_dioxide -> {
                airValues = resources.getIntArray(R.array.airNoTwoValues)
                airStrings = resources.getStringArray(R.array.airStrings)
                currentValue = components.no2.toFloat()
                title = item
            }
            Constants.ammonia -> {
                airValues = resources.getIntArray(R.array.airNHThreeValues)
                airStrings = resources.getStringArray(R.array.airNHThreeStrings)
                currentValue = components.nh3.toFloat()
                title = item
            }
        }
    }

    private fun setAirItemValues() {
        binding.progress.deleteAll()
        binding.progress.configureAngles(280, 0)
        val width = 90F
        val ta = resources.obtainTypedArray(R.array.colors)
        val colors = IntArray(ta.length())
        for (i in 0 until ta.length()) {
            colors[i] = ta.getColor(i, 0)
        }
        ta.recycle()

        val activeItem = WeatherUtils.getCurrentActiveSeriesItem(airValues, currentValue)
        for ((pos, item) in airValues.withIndex().reversed()) {
            val seriesItem = SeriesItem.Builder(colors[pos])
                .setRange(0F, airValues[airValues.size - 1].toFloat(), item.toFloat())
                .setShadowSize(15F)
                .setLineWidth(width)
                .build()

            if (activeItem != -1 && activeItem == pos) {
                binding.airText.text = airStrings[pos]
                binding.airText.setTextColor(colors[pos])
                binding.airValue.text = MessageFormat.format("{0}µg/m³", currentValue)
                binding.airValue.setTextColor(colors[pos])
            }
            binding.progress.addSeries(seriesItem)
        }

        binding.progress.addSeries(SeriesItem.Builder(Color.BLACK)
            .setRange(0F, airValues[airValues.size - 1].toFloat(), 0F)
            .setLineWidth(45F)
            .setShadowSize(20F)
            .build())
        binding.progress.addEvent(DecoEvent.Builder(currentValue)
            .setIndex(airValues.size)
            .setDelay(250)
            .setColor(Color.BLACK)
            .setDuration(750)
            .setDisplayText(MessageFormat.format("{0}µg/m³", currentValue))
            .build())
    }

    companion object {
        const val TAG = "Air Item Dialog"
        fun newInstance(t: String, c: Components): ExpandedAirItem {
            val fragment = ExpandedAirItem()
            fragment.title = t
            fragment.components = c
            return fragment
        }
    }

    override fun getTheme(): Int {
        return R.style.AppTheme_BottomSheet
    }
}