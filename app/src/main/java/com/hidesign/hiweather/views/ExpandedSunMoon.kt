package com.hidesign.hiweather.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hidesign.hiweather.databinding.ExpandedSunMoonBinding
import com.hidesign.hiweather.model.Daily
import com.hidesign.hiweather.model.MoonIcon
import com.hidesign.hiweather.util.DateUtils
import java.util.*
import kotlin.math.roundToInt

class ExpandedSunMoon : BottomSheetDialogFragment() {
    private lateinit var binding: ExpandedSunMoonBinding
    private lateinit var weatherDaily: Daily
    private var description = ""
    private var uvIndex = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View{
        binding = ExpandedSunMoonBinding.inflate(inflater)
        binding.Sunrise.text = DateUtils.getDateTime("HH:mm", (weatherDaily.sunrise).toLong())
        binding.Sunset.text = DateUtils.getDateTime("HH:mm", (weatherDaily.sunset).toLong())
        binding.hoursOfSunlight.text = DateUtils.getHours(weatherDaily.sunrise.toLong(), weatherDaily.sunset.toLong())
        binding.description.text = description.replaceFirstChar { it.titlecase() }

        val uvIndex = "UV Index - $uvIndex"
        binding.UVIndex.text = uvIndex

        binding.Moonrise.text = DateUtils.getDateTime("HH:mm", (weatherDaily.moonrise).toLong())
        binding.moonPhase.setImageResource(MoonIcon.getIcon(weatherDaily.moonPhase))
        binding.Moonset.text = DateUtils.getDateTime("HH:mm", (weatherDaily.moonset).toLong())
        binding.hoursOfMoonlight.text = DateUtils.getHours(weatherDaily.moonrise.toLong(), weatherDaily.moonset.toLong())

        return binding.root
    }

    companion object {
        const val TAG = "ForecastBottomSheet"

        @JvmStatic
        fun newInstance(daily: Daily, desc: String, uvi: Double) =
            ExpandedSunMoon().apply {
                arguments = Bundle().apply {
                    weatherDaily = daily
                    description = desc
                    uvIndex = uvi.roundToInt().toString()
                }
            }
    }
}