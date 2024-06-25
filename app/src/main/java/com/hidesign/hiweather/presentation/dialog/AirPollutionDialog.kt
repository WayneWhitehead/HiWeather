package com.hidesign.hiweather.presentation.dialog

import android.content.Context
import android.content.res.Resources
import android.os.Build
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.hidesign.hiweather.R
import com.hidesign.hiweather.databinding.AirPollutionDialogBinding
import com.hidesign.hiweather.data.model.Components
import com.hidesign.hiweather.presentation.AdViewComposable
import com.hidesign.hiweather.presentation.WeatherViewModel
import com.hidesign.hiweather.util.AdUtil
import com.hidesign.hiweather.util.Constants
import com.hidesign.hiweather.util.WeatherUtil
import com.hookedonplay.decoviewlib.charts.SeriesItem
import com.hookedonplay.decoviewlib.events.DecoEvent
import java.text.MessageFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AirPollutionDialog(weatherViewModel: WeatherViewModel) {
    val context = LocalContext.current
    val airPollutionDialogState by weatherViewModel.airPollutionDialogState.collectAsState()

    airPollutionDialogState?.let { state ->
        data class AirPollutionDialogData(
            val valueRange: Int,
            val rangeStrings: Int,
            val componentValue: (Components) -> Float
        )

        val airPollutionDataMap = remember {
            mapOf(
                Constants.airNamesExpanded[0] to AirPollutionDialogData(R.array.airCoValues, R.array.airStrings) { it.co.toFloat() },
                Constants.airNamesExpanded[1] to AirPollutionDialogData(R.array.airNHThreeValues, R.array.airNHThreeStrings) { it.nh3.toFloat() } ,
                Constants.airNamesExpanded[2] to AirPollutionDialogData(R.array.airNoValues, R.array.airStrings) { it.no.toFloat() },
                Constants.airNamesExpanded[3] to AirPollutionDialogData(R.array.airNoTwoValues, R.array.airStrings) { it.no2.toFloat() },
                Constants.airNamesExpanded[4] to AirPollutionDialogData(R.array.airOThreeValues, R.array.airStrings) { it.o3.toFloat() },
                Constants.airNamesExpanded[5] to AirPollutionDialogData(R.array.airPMTenValues, R.array.airStrings) { it.pm10.toFloat() },
                Constants.airNamesExpanded[6] to AirPollutionDialogData(R.array.airPMTwoFiveValues, R.array.airStrings) { it.pm25.toFloat() },
                Constants.airNamesExpanded[7] to AirPollutionDialogData(R.array.airSoTwoValues, R.array.airStrings) { it.so2.toFloat() },
            )
        }

        ModalBottomSheet(
            containerColor = Color.Black.copy(alpha = 0.85f),
            sheetState = rememberModalBottomSheetState(),
            onDismissRequest = { weatherViewModel.hideAirPollutionDialog() }
        ) {
            val c = state.components
            val airItemTitle = state.title

            fun updateValues(
                context: Context,
                item: String,
                c: Components,
                binding: AirPollutionDialogBinding,
                airPollutionDataMap: Map<String, AirPollutionDialogData>
            ) {
                val airPollutionData = airPollutionDataMap[item] ?: return

                val airValues = context.resources.getIntArray(airPollutionData.valueRange)
                val airStrings = context.resources.getStringArray(airPollutionData.rangeStrings).toList()
                val currentValue = airPollutionData.componentValue(c)

                binding.progress.deleteAll()
                binding.progress.configureAngles(280, 0)

                val width = (Resources.getSystem().displayMetrics.widthPixels / 12).toFloat()
                val ta = context.resources.obtainTypedArray(R.array.colors)
                val colors = IntArray(ta.length())
                for (i in 0 until ta.length()) {
                    colors[i] = ta.getColor(i, 0)
                }
                ta.recycle()

                val activeItem = WeatherUtil.getCurrentActiveSeriesItem(airValues, currentValue)
                for ((pos, value) in airValues.withIndex().reversed()) {
                    val seriesItem = SeriesItem.Builder(colors[pos])
                        .setRange(0F, airValues[airValues.size - 1].toFloat(), value.toFloat())
                        .setShadowSize(15F)
                        .setLineWidth(width)
                        .build()

                    if (activeItem != -1 && activeItem == pos) {
                        binding.airText.text = airStrings[pos]
                        binding.airText.setTextColor(colors[pos])
                        binding.airValue.text =
                            MessageFormat.format(context.getString(R.string.air_item_value), currentValue)
                        binding.airValue.setTextColor(colors[pos])
                    }
                    binding.progress.addSeries(seriesItem)
                }

                binding.progress.addSeries(
                    SeriesItem.Builder(android.graphics.Color.BLACK)
                        .setRange(0F, airValues[airValues.size - 1].toFloat(), 0F)
                        .setLineWidth(width / 2)
                        .setShadowSize(20F)
                        .build())
                binding.progress.addEvent(
                    DecoEvent.Builder(currentValue)
                        .setIndex(airValues.size)
                        .setDelay(250)
                        .setColor(android.graphics.Color.BLACK)
                        .setDuration(750)
                        .setDisplayText(MessageFormat.format(context.getString(R.string.air_item_value), currentValue))
                        .build())
            }

            AndroidViewBinding(
                factory = AirPollutionDialogBinding::inflate,
                modifier = Modifier.fillMaxWidth()
            ) {
                this.airPicker.minValue = 0
                this.airPicker.maxValue = Constants.airNamesExpanded.size - 1
                this.airPicker.displayedValues = Constants.airNamesExpanded.toTypedArray()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    this.airPicker.selectionDividerHeight = 0
                }

                val initialIndex = Constants.airNamesExpanded.indexOf(airItemTitle)
                if (initialIndex != -1) {
                    this.airPicker.value = initialIndex
                    updateValues(context, Constants.airNamesExpanded[initialIndex], c, this, airPollutionDataMap)
                }

                this.airPicker.setOnValueChangedListener { _, _, newVal ->
                    updateValues(context, Constants.airNamesExpanded[newVal], c, this, airPollutionDataMap)
                }
            }

            AdViewComposable(modifier = Modifier, adUnitId = AdUtil.BOTTOM_SHEET_AD)
        }
    }
}