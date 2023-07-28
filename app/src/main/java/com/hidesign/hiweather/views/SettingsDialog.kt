package com.hidesign.hiweather.views

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.work.WorkManager
import com.hidesign.hiweather.R
import com.hidesign.hiweather.databinding.DialogSettingsBinding
import com.hidesign.hiweather.services.APIWorker
import com.hidesign.hiweather.util.Constants


class SettingsDialog(private val activity: WeatherActivity) : Dialog(activity) {
    private val binding: DialogSettingsBinding = DialogSettingsBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        this.window?.setLayout(width, height)
        this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.settingsCard.visibility = View.VISIBLE
        binding.close.setOnClickListener {
            WorkManager.getInstance(activity).cancelAllWork()
            APIWorker.createWorkManagerInstance(activity)
            dismiss()
        }

        binding.tempUnitInput.apply {
            setAdapter(ArrayAdapter(activity, R.layout.simple_spinner_item, resources.getStringArray(R.array.temperature_units)))
            setOnItemClickListener { _, _, position, _ ->
                updateValues(resources.getStringArray(R.array.temperature_units)[position],
                    resources.getStringArray(R.array.temperature_units),
                    Constants.temperatureUnit)
            }
        }
        binding.refreshIntervalInput.apply {
            setAdapter(ArrayAdapter(activity, R.layout.simple_spinner_item, resources.getStringArray(R.array.refresh_interval)))
            setOnItemClickListener { _, _, position, _ ->
                updateValues(resources.getStringArray(R.array.refresh_interval)[position],
                    resources.getStringArray(R.array.refresh_interval),
                    Constants.refreshInterval)
            }
        }


        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
        val posTemp = sharedPref.getInt(Constants.temperatureUnit, 0)
        val posRefresh = sharedPref.getInt(Constants.refreshInterval, 0)
        updateValues(activity.resources.getStringArray(R.array.temperature_units)[posTemp],
            activity.resources.getStringArray(R.array.temperature_units),
            Constants.temperatureUnit)
        updateValues(activity.resources.getStringArray(R.array.refresh_interval)[posRefresh],
            activity.resources.getStringArray(R.array.refresh_interval),
            Constants.refreshInterval)
        binding.tempUnitInput.setText(activity.resources.getStringArray(R.array.temperature_units)[posTemp], false)
        binding.refreshIntervalInput.setText(activity.resources.getStringArray(R.array.refresh_interval)[posRefresh], false)
    }

    private fun updateValues(item: String, values: Array<String>, preference: String) {
        for ((pos, value) in values.withIndex()) {
            if (item == value) {
                val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putInt(preference, pos)
                    apply()
                }
            }
        }
    }
}