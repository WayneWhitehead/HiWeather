package com.hidesign.hiweather.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import com.hidesign.hiweather.model.Daily
import com.hidesign.hiweather.model.Hourly
import com.hidesign.hiweather.model.OneCallResponse
import com.hidesign.hiweather.util.AdUtil
import com.hidesign.hiweather.util.DateUtils
import com.hidesign.hiweather.util.WeatherUtils.getWeatherIconUrl
import com.hidesign.hiweather.util.WeatherUtils.getWindDegreeText
import java.math.RoundingMode
import java.text.MessageFormat
import kotlin.math.roundToInt

@Composable
fun ForecastCard(modifier: Modifier, weather: OneCallResponse, items: List<Any>) {
    val title = if (items[0] is Hourly) stringResource(R.string.hourly_forecast) else stringResource(R.string.daily_forecast)
    val padding: PaddingValues = if (items[0] is Hourly) PaddingValues(10.dp) else PaddingValues(10.dp, 20.dp, 10.dp, 10.dp)
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2B2B2B),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(30.dp)
    ) {
        Column (horizontalAlignment = Alignment.CenterHorizontally)  {
            Text(modifier = Modifier.padding(padding),
                text = title, fontSize = 28.sp, color = Color.White)
            HorizontalDivider(color = Color.White)

            LazyRow(modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()) {
                items(if (items[0] is Hourly) items else items) {
                    if (it is Hourly) {
                        HourlyItem(
                            modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally),
                            hourly = it,
                            tz = weather.timezone)
                    } else if (it is Daily) {
                        DailyItem(
                            modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally),
                            daily = it,
                            tz = weather.timezone)
                    }
                }
            }
        }
    }
}

@Composable
fun HourlyItem(modifier: Modifier, hourly: Hourly, tz: String) {
    Column(
        modifier
            .clickable {
                forecastTimezone.value = tz
                forecastHourly.value = hourly
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ForecastHeader(value = DateUtils.getDateTime(DateUtils.HOURLY_FORMAT, hourly.dt.toLong(), tz))
        Row(verticalAlignment = Alignment.CenterVertically) {
            LoadPicture(Modifier.size(80.dp),
                url = getWeatherIconUrl(hourly.weather[0].icon),
                contentDescription = "Weather icon"
            )

            Column {
                Text(
                    text = MessageFormat.format("{0}°", hourly.temp.roundToInt()),
                    fontSize = 30.sp,
                    color = Color.White
                )
                ForecastImageLabel(
                    forecastItem = MessageFormat.format("{0}%", (hourly.pop * 100)),
                    image = painterResource(id = R.drawable.rain)
                )
            }
        }
    }
}

@Composable
fun DailyItem(modifier: Modifier, daily: Daily, tz: String) {
    Column(
        modifier.clickable {
            forecastTimezone.value = tz
            forecastDaily.value = daily },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ForecastHeader(value = DateUtils.getDayOfWeekText(DateUtils.DAILY_FORMAT, daily.dt.toLong(), tz))
        Row(verticalAlignment = Alignment.CenterVertically) {
            LoadPicture(Modifier.size(80.dp),
                url = getWeatherIconUrl(daily.weather[0].icon),
                contentDescription = "Weather icon"
            )

            Column {
                ForecastIconLabel(
                    forecastItem = MessageFormat.format("{0}°", daily.temp.max.roundToInt()),
                    icon = Icons.Filled.KeyboardArrowUp
                )

                ForecastIconLabel(
                    forecastItem = MessageFormat.format("{0}°", daily.temp.min.roundToInt()),
                    icon = Icons.Filled.KeyboardArrowDown
                )
                ForecastImageLabel(
                    forecastItem = MessageFormat.format("{0}%", daily.pop * 100),
                    image = painterResource(id = R.drawable.rain)
                )
            }
        }
    }
}

@Composable
fun ForecastHeader(value: String) {
    Text(
        text = value,
        fontSize = 18.sp,
        color = Color.White
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandForecast(daily: Daily? = null, hourly: Hourly? = null, timezone: String, onDismissRequest: () -> Unit) {
    ModalBottomSheet(
        containerColor = Color(0xFA000000),
        contentColor = Color.White,
        sheetState = rememberModalBottomSheetState(),
        onDismissRequest = onDismissRequest
    ) {
        val image = getWeatherIconUrl(hourly?.weather?.get(0)?.icon ?: daily?.weather?.get(0)?.icon ?: "")
        val date = if (hourly != null) DateUtils.getDateTime(DateUtils.HOURLY_FORMAT, hourly.dt.toLong(), timezone) else DateUtils.getDayOfWeekText(DateUtils.DAILY_FORMAT, daily?.dt?.toLong() ?: 0, timezone)
        val realFeel = (daily?.feelsLike?.day ?: hourly?.feelsLike)?.roundToInt() ?: 0

        Column(Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy((-30).dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ExpandedForecastHeader(image, date)

                if (hourly != null) {
                    ExpandedHourlyCurrent(
                        currentTemp = MessageFormat.format(stringResource(id = R.string._0_c), hourly.temp.roundToInt()),
                        realFeelTemp = MessageFormat.format(stringResource(id = R.string.real_feel_0_c), realFeel)
                    )
                }
                if (daily != null) {
                    ExpandedDailyCurrent(
                        highTemp = MessageFormat.format(stringResource(id = R.string.high_0_c), daily.temp.max.roundToInt()),
                        lowTemp = MessageFormat.format(stringResource(id = R.string.low_0_c), daily.temp.min.roundToInt()),
                        realFeelTemp = MessageFormat.format(stringResource(id = R.string.real_feel_0_c), realFeel)
                    )
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
                    val precipitation = ((daily?.pop ?: hourly?.pop)?.roundToInt() ?: 0) * 100
                    ForecastImageLabel(
                        forecastItem = MessageFormat.format(stringResource(id = R.string.precipitation_0), precipitation),
                        image = painterResource(id = R.drawable.rain),
                        size = 16
                    )
                    val humidity = hourly?.humidity ?: daily?.humidity ?: 0
                    ForecastImageLabel(
                        forecastItem = MessageFormat.format(stringResource(id = R.string.humidity_0), humidity),
                        image = painterResource(id = R.drawable.humidity),
                        size = 16
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = (hourly?.windSpeed ?: daily?.windSpeed ?: 0.0).toBigDecimal().setScale(1, RoundingMode.HALF_EVEN).toString(),
                                fontSize = 40.sp,
                                color = Color.White
                            )

                            val windDirection = hourly?.windDeg ?: daily?.windDeg ?: 0
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
                                text = getWindDegreeText(windDirection),
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
                    val dewPoint = (hourly?.dewPoint ?: daily?.dewPoint)?.roundToInt() ?: 0
                    ForecastImageLabel(
                        forecastItem = MessageFormat.format(stringResource(id = R.string.dew_point_0_c), dewPoint),
                        image = painterResource(id = R.drawable.dew_point),
                        size = 16
                    )
                    val pressure = hourly?.pressure ?: daily?.pressure ?: 0
                    ForecastImageLabel(
                        forecastItem = MessageFormat.format(stringResource(id = R.string.pressure_0_hpa), pressure),
                        image = painterResource(id = R.drawable.pressure),
                        size = 16
                    )
                    if (hourly != null) {
                        ForecastImageLabel(
                            forecastItem = MessageFormat.format(stringResource(id = R.string.visibility_0_m), hourly.visibility / 1000),
                            image = painterResource(id = R.drawable.visibility),
                            size = 16
                        )
                    }
                    val uvi = (hourly?.uvi ?: daily?.uvi)?.roundToInt() ?: 0
                    ForecastImageLabel(
                        forecastItem = MessageFormat.format(stringResource(id = R.string.uv_index_0), uvi),
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
fun ExpandedForecastHeader(image: String, date: String) {
    Card(
        modifier = Modifier.wrapContentHeight(),
        shape = RoundedCornerShape(45.dp),
        elevation = CardDefaults.elevatedCardElevation(5.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFA0E0D0D))
    ) {
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
}

@Composable
fun ExpandedHourlyCurrent(currentTemp: String, realFeelTemp: String) {
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
fun ExpandedDailyCurrent(highTemp: String, lowTemp: String, realFeelTemp: String) {
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
