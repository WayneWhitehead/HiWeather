package com.hidesign.hiweather.presentation.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hidesign.hiweather.R
import com.hidesign.hiweather.data.model.Daily
import com.hidesign.hiweather.data.model.FutureWeather
import com.hidesign.hiweather.data.model.Hourly
import com.hidesign.hiweather.presentation.AdViewComposable
import com.hidesign.hiweather.presentation.ForecastImageLabel
import com.hidesign.hiweather.presentation.LoadPicture
import com.hidesign.hiweather.presentation.components.ForecastIconLabel
import com.hidesign.hiweather.util.AdUtil
import com.hidesign.hiweather.util.DateUtils
import com.hidesign.hiweather.util.WeatherUtil
import java.math.RoundingMode
import java.text.MessageFormat
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForecastSheet(weather: FutureWeather, tz: String, onDismissRequest: () -> Unit) {
    ModalBottomSheet(
        containerColor = Color(0xFA000000),
        contentColor = Color.White,
        sheetState = rememberModalBottomSheetState(),
        onDismissRequest = onDismissRequest
    ) {
        val image = WeatherUtil.getWeatherIconUrl(weather.weather[0].icon)
        val date = when (weather) {
            is Hourly -> DateUtils.getDateTime(DateUtils.HOURLY_FORMAT, weather.dt.toLong(), tz)
            is Daily -> DateUtils.getDayOfWeekText(DateUtils.DAILY_FORMAT, weather.dt.toLong(), tz)
            else -> ""
        }
        val realFeel = when (weather) {
            is Hourly -> weather.feelsLike
            is Daily -> weather.feelsLike.day
            else -> 0.0
        }.roundToInt()

        Column(Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy((-30).dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ForecastDialogHeader(image, date)
                when (weather) {
                    is Hourly -> {
                        ForecastDialogHourlyCurrent(
                            currentTemp = MessageFormat.format(stringResource(id = R.string._0_c), weather.temp.roundToInt()),
                            realFeelTemp = MessageFormat.format(stringResource(id = R.string.real_feel_0_c), realFeel)
                        )
                    }
                    is Daily -> {
                        ForecastDialogDailyCurrent(
                            highTemp = MessageFormat.format(stringResource(id = R.string.high_0_c), weather.temp.max.roundToInt()),
                            lowTemp = MessageFormat.format(stringResource(id = R.string.low_0_c), weather.temp.min.roundToInt()),
                            realFeelTemp = MessageFormat.format(stringResource(id = R.string.real_feel_0_c), realFeel)
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp, 10.dp, 10.dp, 30.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.wrapContentHeight(),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ForecastImageLabel(
                        forecastItem = MessageFormat.format(stringResource(id = R.string.precipitation_0), (weather.pop * 100)),
                        image = painterResource(id = R.drawable.rain),
                        size = 16
                    )
                    ForecastImageLabel(
                        forecastItem = MessageFormat.format(stringResource(id = R.string.humidity_0), weather.humidity),
                        image = painterResource(id = R.drawable.humidity),
                        size = 16
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = weather.windSpeed.toBigDecimal().setScale(1, RoundingMode.HALF_EVEN).toString(),
                                fontSize = 40.sp,
                                color = Color.White
                            )

                            val windDirection = weather.windDeg
                            Column {
                                Image(
                                    painter = painterResource(id = R.drawable.direction),
                                    contentDescription = "Wind direction icon",
                                    modifier = Modifier
                                        .rotate((windDirection - 270).toFloat())
                                        .size(16.dp),
                                    colorFilter = ColorFilter.tint(Color.White)
                                )
                                Text(
                                    text = "m/s",
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                            }
                            Text(
                                text = WeatherUtil.getWindDegreeText(windDirection),
                                fontSize = 22.sp,
                                color = Color.White
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier.wrapContentHeight(),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ForecastImageLabel(
                        forecastItem = MessageFormat.format(stringResource(id = R.string.dew_point_0_c), weather.dewPoint.roundToInt()),
                        image = painterResource(id = R.drawable.dew_point),
                        size = 16
                    )
                    ForecastImageLabel(
                        forecastItem = MessageFormat.format(stringResource(id = R.string.pressure_0_hpa), weather.pressure),
                        image = painterResource(id = R.drawable.pressure),
                        size = 16
                    )
                    if (weather is Hourly) {
                        ForecastImageLabel(
                            forecastItem = MessageFormat.format(stringResource(id = R.string.visibility_0_m), weather.visibility / 1000),
                            image = painterResource(id = R.drawable.visibility),
                            size = 16
                        )
                    }
                    ForecastImageLabel(
                        forecastItem = MessageFormat.format(stringResource(id = R.string.uv_index_0), weather.uvi.roundToInt()),
                        image = painterResource(id = R.drawable.uv),
                        size = 16
                    )
                }
            }

            AdViewComposable(modifier = Modifier, adUnitId = AdUtil.BOTTOM_SHEET_AD)
        }
    }
}

@Composable
fun ForecastDialogHeader(image: String, date: String) {
    Row(
        modifier = Modifier.padding(end = 40.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LoadPicture(
            modifier = Modifier.size(80.dp),
            url = image,
            contentDescription = "Cloud cover"
        )
        Text(
            text = date,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp
        )
    }
}

@Composable
fun ForecastDialogHourlyCurrent(currentTemp: String, realFeelTemp: String) {
    Card(
        shape = RoundedCornerShape(30.dp),
        elevation = CardDefaults.elevatedCardElevation(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1F))
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize(Alignment.Center)
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = currentTemp,
                fontSize = 40.sp,
                color = Color.White
            )
            Text(
                text = realFeelTemp,
                color = Color.White
            )
        }
    }
}

@Composable
fun ForecastDialogDailyCurrent(highTemp: String, lowTemp: String, realFeelTemp: String) {
    Card(
        shape = RoundedCornerShape(30.dp),
        elevation = CardDefaults.elevatedCardElevation(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF19191A))
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize(Alignment.Center)
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(verticalArrangement = Arrangement.SpaceBetween) {
                ForecastIconLabel(
                    forecastItem = highTemp,
                    icon = Icons.Filled.KeyboardArrowUp
                )
                ForecastIconLabel(
                    forecastItem = lowTemp,
                    icon = Icons.Filled.KeyboardArrowDown
                )
            }
            Text(
                text = realFeelTemp,
                color = Color.White
            )
        }
    }
}