package com.hidesign.hiweather.views

import android.content.Context
import android.content.res.Resources
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.hidesign.hiweather.R
import com.hidesign.hiweather.databinding.ExpandedAirItemBinding
import com.hidesign.hiweather.model.AirPollutionResponse
import com.hidesign.hiweather.model.Components
import com.hidesign.hiweather.util.Constants
import com.hidesign.hiweather.util.Extensions.roundToDecimal
import com.hidesign.hiweather.util.WeatherUtils
import com.hookedonplay.decoviewlib.charts.SeriesItem
import com.hookedonplay.decoviewlib.events.DecoEvent
import java.text.MessageFormat

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AirPollutionCard(
    modifier: Modifier,
    airPollution: AirPollutionResponse
) {
    val context = LocalContext.current
    val backgroundColor = if (airPollution.list.isNotEmpty()) {
        WeatherUtils.getAirQualityColour(airPollution.list[0].main.aqi, context)
    } else {
        Color.White.toArgb()
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(10.dp, 0.dp),
        backgroundColor = Color(backgroundColor),
        contentColor = Color.White,
        shape = RoundedCornerShape(30.dp)
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(10.dp, 10.dp, 10.dp, 20.dp),
            verticalAlignment = Alignment.CenterVertically,) {
            Column(Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = airPollution.list[0].main.aqi.toString(),
                    fontSize = 60.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = WeatherUtils.getAirQualityText(airPollution.list[0].main.aqi),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = context.getString(R.string.air_quality),
                    fontSize = 20.sp,
                    color = Color.Black
                )
            }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                AirQualityButton(
                    title = Constants.CARBON_MONOXIDE, airText = "CO - {0}",
                    airValue = airPollution.list[0].components.co.roundToDecimal()
                )
                AirQualityButton(
                    title = Constants.AMMONIA, airText = "NH₃ - {0}",
                    airValue = airPollution.list[0].components.nh3.roundToDecimal()
                )
                AirQualityButton(
                    title = "", airText = "NO - {0}",
                    airValue = airPollution.list[0].components.no.roundToDecimal()
                )
                AirQualityButton(
                    title = Constants.NITROGEN_DIOXIDE, airText = "NO₂ - {0}",
                    airValue = airPollution.list[0].components.no2.roundToDecimal()
                )
                AirQualityButton(
                    title = Constants.OZONE, airText = "O₃ - {0}",
                    airValue = airPollution.list[0].components.o3.roundToDecimal()
                )
                AirQualityButton(
                    title = Constants.COARSE_MATTER, airText = "PM₁₀ - {0}",
                    airValue = airPollution.list[0].components.pm10.roundToDecimal()
                )
                AirQualityButton(
                    title = Constants.FINE_MATTER, airText = "PM₂₅ - {0}",
                    airValue = airPollution.list[0].components.pm25.roundToDecimal()
                )
                AirQualityButton(
                    title = Constants.SULPHUR_DIOXIDE, airText = "SO₂ - {0}",
                    airValue = airPollution.list[0].components.so2.roundToDecimal()
                )
            }
        }
    }
}

@Composable
fun AirQualityButton(title: String, airText: String, airValue: Double) {
    TextButton(
        onClick = { airItemTitle.value = title },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Black,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            MessageFormat.format(airText, airValue),
            fontSize = 12.sp
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AirQualityDialog(c: Components, dialogTitle: MutableState<String>) {
    val context = LocalContext.current

    ModalBottomSheet(
        containerColor = Color(0xD9000000),
        sheetState = rememberModalBottomSheetState(),
        onDismissRequest = { dialogTitle.value = "" }
    ) {
        AndroidViewBinding(
            ExpandedAirItemBinding::inflate,
            modifier = Modifier.fillMaxWidth()
        ) {

            this.airPicker.minValue = 0
            this.airPicker.maxValue = context.resources.getStringArray(R.array.airTitles).size - 1
            this.airPicker.displayedValues = context.resources.getStringArray(R.array.airTitles)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                this.airPicker.selectionDividerHeight = 0
            }

            for ((pos, item) in context.resources.getStringArray(R.array.airTitles).withIndex()) {
                if (item == dialogTitle.value) {
                    this.airPicker.value = pos
                }
            }

            updateValues(context, context.resources.getStringArray(R.array.airTitles)[0], c, this)

            this.airPicker.setOnValueChangedListener { _, _, newVal ->
                updateValues(context, context.resources.getStringArray(R.array.airTitles)[newVal], c, this)
            }
        }
    }
}

fun updateValues (context: Context, item: String, c: Components, binding: ExpandedAirItemBinding) {
    var airValues = IntArray(0)
    var airStrings = listOf<String>()
    var currentValue = 0F

    when (item) {
        Constants.CARBON_MONOXIDE -> {
            airValues = context.resources.getIntArray(R.array.airCoValues)
            airStrings = context.resources.getStringArray(R.array.airStrings).toList()
            currentValue = c.co.toFloat()
        }
        Constants.SULPHUR_DIOXIDE -> {
            airValues = context.resources.getIntArray(R.array.airSoTwoValues)
            airStrings = context.resources.getStringArray(R.array.airStrings).toList()
            currentValue = c.so2.toFloat()
        }
        Constants.FINE_MATTER -> {
            airValues = context.resources.getIntArray(R.array.airPMTwoFiveValues)
            airStrings = context.resources.getStringArray(R.array.airStrings).toList()
            currentValue = c.pm25.toFloat()
        }
        Constants.COARSE_MATTER -> {
            airValues = context.resources.getIntArray(R.array.airPMTenValues)
            airStrings = context.resources.getStringArray(R.array.airStrings).toList()
            currentValue = c.pm10.toFloat()
        }
        Constants.OZONE -> {
            airValues = context.resources.getIntArray(R.array.airOThreeValues)
            airStrings = context.resources.getStringArray(R.array.airStrings).toList()
            currentValue = c.o3.toFloat()
        }
        Constants.NITROGEN_DIOXIDE -> {
            airValues = context.resources.getIntArray(R.array.airNoTwoValues)
            airStrings = context.resources.getStringArray(R.array.airStrings).toList()
            currentValue = c.no2.toFloat()
        }
        Constants.AMMONIA -> {
            airValues = context.resources.getIntArray(R.array.airNHThreeValues)
            airStrings = context.resources.getStringArray(R.array.airNHThreeStrings).toList()
            currentValue = c.nh3.toFloat()
        }
    }

    binding.progress.deleteAll()
    binding.progress.configureAngles(280, 0)

    val width = (Resources.getSystem().displayMetrics.widthPixels / 12).toFloat()
    val ta = context.resources.obtainTypedArray(R.array.colors)
    val colors = IntArray(ta.length())
    for (i in 0 until ta.length()) {
        colors[i] = ta.getColor(i, 0)
    }
    ta.recycle()

    val activeItem = WeatherUtils.getCurrentActiveSeriesItem(airValues, currentValue)
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

    binding.progress.addSeries(SeriesItem.Builder(android.graphics.Color.BLACK)
        .setRange(0F, airValues[airValues.size - 1].toFloat(), 0F)
        .setLineWidth(width / 2)
        .setShadowSize(20F)
        .build())
    binding.progress.addEvent(DecoEvent.Builder(currentValue)
        .setIndex(airValues.size)
        .setDelay(250)
        .setColor(android.graphics.Color.BLACK)
        .setDuration(750)
        .setDisplayText(MessageFormat.format(context.getString(R.string.air_item_value), currentValue))
        .build())
}