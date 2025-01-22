package com.hidesign.hiweather.presentation.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hidesign.hiweather.R
import com.hidesign.hiweather.data.model.Daily
import com.hidesign.hiweather.data.model.Hourly
import com.hidesign.hiweather.presentation.ForecastImageLabel
import com.hidesign.hiweather.presentation.LoadPicture
import com.hidesign.hiweather.presentation.WeatherViewModel
import com.hidesign.hiweather.util.DateUtils
import com.hidesign.hiweather.util.WeatherUtil.getWeatherIconUrl
import java.text.MessageFormat
import java.util.TimeZone
import kotlin.math.roundToInt

@Composable
fun ForecastCard(modifier: Modifier, weatherList: List<Any>?, tz: String?, weatherViewModel: WeatherViewModel) {
    val containerColour = Color(0xFF2B2B2B)

    Crossfade(targetState = weatherList, label = "") { items ->
        when (items) {
            null -> ShimmerEffect(
                modifier = modifier.height(150.dp),
                color = containerColour,
            )
            else -> Card(
                modifier = modifier,
                colors = CardDefaults.cardColors(containerColor = containerColour, contentColor = Color.White),
                shape = RoundedCornerShape(30.dp)
            ) {
                val title = when (items[0]) {
                    is Hourly -> stringResource(R.string.hourly_forecast)
                    is Daily -> stringResource(R.string.daily_forecast)
                    else -> ""
                }
                val padding = when (items[0]) {
                    is Hourly -> PaddingValues(10.dp)
                    is Daily -> PaddingValues(10.dp, 20.dp, 10.dp, 10.dp)
                    else -> PaddingValues(0.dp)
                }

                Column (horizontalAlignment = Alignment.CenterHorizontally)  {
                    Text(modifier = Modifier.padding(padding), text = title, fontSize = 28.sp, color = Color.White)

                    HorizontalDivider(color = Color.White)

                    LazyRow(modifier = Modifier.wrapContentHeight().fillMaxWidth()) {
                        items(items) {
                            when (it) {
                                is Hourly -> HourlyItem(it, tz) {
                                    weatherViewModel.showForecastDialog(it, tz!!)
                                }
                                is Daily -> DailyItem(it, tz) {
                                    weatherViewModel.showForecastDialog(it, tz!!)
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
fun HourlyItem(hourly: Hourly, tz: String?, onClick: () -> Unit) {
    Column(
        modifier = Modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ForecastHeader(value = DateUtils.getDateTime(DateUtils.HOURLY_FORMAT, hourly.dt.toLong(), tz ?: TimeZone.getDefault().displayName))
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
fun DailyItem(daily: Daily, tz: String?, onClick: () -> Unit) {
    Column(
        modifier = Modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ForecastHeader(value = DateUtils.getDayOfWeekText(DateUtils.DAILY_FORMAT, daily.dt.toLong(), tz ?: TimeZone.getDefault().displayName))
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

@Composable
fun ForecastIconLabel(forecastItem: String, icon: ImageVector, size: Int = 15) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        Image(
            imageVector = icon,
            modifier = Modifier.size(size.dp),
            colorFilter = ColorFilter.tint(Color.White),
            contentDescription = "$forecastItem Icon"
        )

        Text(
            text = forecastItem,
            fontSize = size.sp,
            color = Color.White
        )
    }
}
