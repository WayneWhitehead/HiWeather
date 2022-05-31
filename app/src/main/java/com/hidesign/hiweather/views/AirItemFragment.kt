package com.hidesign.hiweather.views

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hidesign.hiweather.R
import com.hidesign.hiweather.databinding.FragmentAirItemBinding
import com.hidesign.hiweather.model.Components
import com.hidesign.hiweather.util.WeatherUtils
import java.text.MessageFormat


class AirItemFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentAirItemBinding
    private lateinit var airStrings: Array<String>
    private lateinit var airValues: IntArray
    private var currentValue = 0F
    private var title = ""
    private lateinit var components: Components

    override fun onStart() {
        super.onStart()
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
        setAirItemValues()
        binding.airPicker.setOnValueChangedListener { _, _, newVal ->
            updateValues(resources.getStringArray(R.array.airTitles)[newVal])
            setAirItemValues()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAirItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun updateValues(item: String) {
        when (item) {
            "Fine Particle Matter(PM₂₅)" -> {
                airValues = resources.getIntArray(R.array.airPMTwoFiveValues)
                airStrings = resources.getStringArray(R.array.airPMTwoFiveStrings)
                currentValue = components.pm25.toFloat()
                title = "Fine Particle Matter(PM₂₅)"
            }
            "Course Particulate Matter(PM₁₀)" -> {
                airValues = resources.getIntArray(R.array.airPMTenValues)
                airStrings = resources.getStringArray(R.array.airPMTenStrings)
                currentValue = components.pm10.toFloat()
                title = "Course Particulate Matter(PM₁₀)"
            }
            "Ozone(O₃)" -> {
                airValues = resources.getIntArray(R.array.airOThreeValues)
                airStrings = resources.getStringArray(R.array.airOThreeStrings)
                currentValue = components.o3.toFloat()
                title = "Ozone(O₃)"
            }
            "Nitrogen Dioxide(NO₂)" -> {
                airValues = resources.getIntArray(R.array.airNOTwoValues)
                airStrings = resources.getStringArray(R.array.airNOTwoStrings)
                currentValue = components.no2.toFloat()
                title = "Nitrogen Dioxide(NO₂)"
            }
            "Ammonia(NH₃)" -> {
                airValues = resources.getIntArray(R.array.airNHThreeValues)
                airStrings = resources.getStringArray(R.array.airNHThreeStrings)
                currentValue = components.nh3.toFloat()
                title = "Ammonia(NH₃)"
            }
        }
    }

    private fun setAirItemValues() {
        binding.progress.setRange(airValues[0].toFloat(), airValues[airValues.size - 1].toFloat())
        binding.progress.setProgress(currentValue)
        binding.progress.leftSeekBar.thumbHeight = 20
        binding.progress.leftSeekBar.thumbWidth = 20
        binding.progress.setIndicatorText(WeatherUtils.getValueQualityText(airStrings,
            airValues,
            currentValue))
        binding.progress.tickMarkTextArray = airValues.map { it.toString() }.toTypedArray()
        binding.progress.steps = airValues[airValues.size - 1] / 50
        binding.progress.progressColor = Color.TRANSPARENT

        binding.airValue.text = MessageFormat.format("{0}µg/m³", currentValue)
    }

    companion object {
        const val TAG = "Air Item Dialog"
        fun newInstance(
            t: String,
            stringArray: Array<String>,
            valueArray: IntArray,
            current: Float,
            c: Components,
        ): AirItemFragment {
            val fragment = AirItemFragment()
            fragment.currentValue = current
            fragment.airStrings = stringArray
            fragment.airValues = valueArray
            fragment.title = t
            fragment.components = c
            return fragment
        }
    }
}