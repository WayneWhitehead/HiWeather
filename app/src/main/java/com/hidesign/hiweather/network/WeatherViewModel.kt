package com.hidesign.hiweather.network

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hidesign.hiweather.R
import com.hidesign.hiweather.model.AirPollutionResponse
import com.hidesign.hiweather.model.OneCallResponse
import com.hidesign.hiweather.util.Constants
import com.hidesign.hiweather.views.WeatherActivity.Companion.uAddress
import retrofit2.Response


class WeatherViewModel : ViewModel() {

    private val _oneCallResponse = MutableLiveData<OneCallResponse>()
    val oneCallResponse: LiveData<OneCallResponse> get() = _oneCallResponse

    private val _airPollutionResponse = MutableLiveData<AirPollutionResponse>()
    val airPollutionResponse: LiveData<AirPollutionResponse> get() = _airPollutionResponse

    suspend fun getOneCallWeather(context: Context) {
        val apiClient = getApiClient()

        val response = apiClient?.getOneCall(
            uAddress!!.latitude,
            uAddress!!.longitude,
            "minutely",
            getAPIKey(context),
            getUnit(context))

        if (response!!.isSuccessful) {
            _oneCallResponse.value = response.body()
        }
    }

    suspend fun getBackgroundWeather(context: Context): Response<OneCallResponse?>? {
        val apiClient = getApiClient()

        return apiClient?.getOneCall(
            uAddress!!.latitude,
            uAddress!!.longitude,
            "minutely",
            getAPIKey(context),
            getUnit(context))
    }

    suspend fun getAirPollution(context: Context) {
        val apiClient = getApiClient()
        val response = apiClient?.getAirPollution(uAddress!!.latitude, uAddress!!.longitude, getAPIKey(context))

        if (response!!.isSuccessful) {
            _airPollutionResponse.value = response.body()
        }
    }

    private fun getUnit(context: Context): String {
        val sharedPref = context.getSharedPreferences(Constants.preferences, Context.MODE_PRIVATE)
        val posUnit = sharedPref.getInt(Constants.temperatureUnit, 0)
        var unit = "celsius"
        for ((pos, value) in context.resources.getStringArray(R.array.temperature_units).withIndex()) {
            if (posUnit == pos) {
                unit = value.lowercase()
            }
        }
        return when (unit) {
            "celsius" -> "metric"
            "fahrenheit" -> "imperial"
            "kelvin" -> ""
            else -> "metric"
        }
    }

    private fun getAPIKey(context: Context): String {
        return Constants.getAPIKey(context, Constants.openWeatherKey)
    }

    private fun getApiClient() = ApiClient().apiService
}

