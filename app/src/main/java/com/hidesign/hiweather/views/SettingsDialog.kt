package com.hidesign.hiweather.views

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
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

        binding.settingsCard.visibility = View.VISIBLE
        binding.close.setOnClickListener {
            WorkManager.getInstance(activity).cancelAllWork()
            val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
            APIWorker.createWorkManagerInstance(activity, sharedPref.getInt(Constants.refreshInterval, 0))
            dismiss()
        }

        val adapter1 = ArrayAdapter(activity,
            R.layout.simple_spinner_item,
            activity.resources.getStringArray(R.array.temperature_units))
        binding.tempUnitInput.setAdapter(adapter1)
        val adapter2 = ArrayAdapter(activity,
            R.layout.simple_spinner_item,
            activity.resources.getStringArray(R.array.refresh_interval))
        binding.refreshIntervalInput.setAdapter(adapter2)
        binding.tempUnitInput.setOnItemClickListener { _, _, position, _ ->
            updateValues(activity.resources.getStringArray(R.array.temperature_units)[position],
                activity.resources.getStringArray(R.array.temperature_units),
                Constants.temperatureUnit)
        }
        binding.refreshIntervalInput.setOnItemClickListener { _, _, position, _ ->
            updateValues(activity.resources.getStringArray(R.array.refresh_interval)[position],
                activity.resources.getStringArray(R.array.refresh_interval),
                Constants.refreshInterval)
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