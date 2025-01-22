package com.hidesign.hiweather.presentation.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hidesign.hiweather.R
import com.hidesign.hiweather.presentation.WeatherViewModel
import com.hidesign.hiweather.util.Constants
import com.hidesign.hiweather.util.Constants.airNamesExpanded
import com.hidesign.hiweather.util.WeatherUtil
import java.text.MessageFormat

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AirPollutionCard(
    modifier: Modifier,
    weatherViewModel: WeatherViewModel
) {
    val context = LocalContext.current
    val containerColour = colorResource(id = R.color.airIndex1)
    val state by weatherViewModel.state.collectAsState()

    Crossfade(targetState = state.airPollutionResponse, label = "") { weather ->
        when (weather) {
            null -> ShimmerEffect(
                modifier = modifier.height(300.dp),
                color = containerColour,
            )
            else -> {
                val defaultAir = weather.list[0]
                val backgroundColor = WeatherUtil.getAirQualityColour(defaultAir.main.aqi, context)

                Card(
                    modifier = modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(backgroundColor),
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(10.dp, 10.dp, 10.dp, 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(
                            Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = defaultAir.main.aqi.toString(),
                                fontSize = 60.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )

                            Text(
                                text = WeatherUtil.getAirQualityText(defaultAir.main.aqi),
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
                            val componentsList = WeatherUtil.getComponentList(defaultAir.components)
                            Constants.airAbbreviations.forEachIndexed { index, item ->
                                AirPollutionButton("$item - {0}", componentsList[index]) {
                                    weatherViewModel.showAirPollutionDialog(defaultAir.components, airNamesExpanded[index])
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AirPollutionButton(airText: String, airValue: Double, onClick: () -> Unit) {
    TextButton(
        onClick = { onClick() },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black,
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