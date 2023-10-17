package com.hidesign.hiweather.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hidesign.hiweather.R
import com.hidesign.hiweather.model.Daily
import com.hidesign.hiweather.util.DateUtils
import com.hidesign.hiweather.util.WeatherUtils.getMoonIcon

val sunGradient = Brush.linearGradient(listOf(Color(0xFFCC4B4B), Color(0xFFFFF964)))
val moonGradient = Brush.linearGradient(listOf(Color(0xFF15439F), Color(0xFF000000)))

@Composable
fun SunCard(modifier: Modifier, daily: Daily, tz: String, showHours: Boolean = true) {
    SunMoonCard(
        modifier = modifier,
        content = {
            Box(Modifier.background(sunGradient)) {
                Column(
                    Modifier.padding(20.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        RiseSetText(value = daily.sunrise, tz = tz, label = stringResource(id = R.string.sunrise))
                        RiseSetText(value = daily.sunset, tz = tz, label = stringResource(id = R.string.sunset))
                    }
                    if (showHours) {
                        VisibleHoursText(hours = DateUtils.getHours(daily.sunrise.toLong(), daily.sunset.toLong()))
                    }
                }
            }
        },
        color = Color.Black
    )
}
@Composable
fun MoonCard(modifier: Modifier, daily: Daily, tz: String, showHours: Boolean = true) {
    SunMoonCard(
        modifier = modifier,
        content = {
            Box(Modifier.background(moonGradient)) {
                Column(
                    Modifier.padding(20.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        RiseSetText(value = daily.moonrise, tz = tz, label = stringResource(id = R.string.moonrise))
                        Image(
                            modifier = Modifier.size(40.dp),
                            painter = painterResource(id = getMoonIcon(daily.moonPhase)),
                            contentDescription = "Moon Icon",
                            colorFilter = ColorFilter.tint(Color.White)
                        )
                        RiseSetText(value = daily.moonset, tz = tz, label = stringResource(id = R.string.moonset))
                    }
                    if (showHours) {
                        VisibleHoursText(hours = DateUtils.getHours(daily.moonrise.toLong(), daily.moonset.toLong()))
                    }
                }
            }
        },
        color = Color.White
    )
}

@Composable
fun SunMoonCard(modifier: Modifier, content: @Composable () -> Unit, color: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(30.dp),
        backgroundColor = Color.Transparent,
        contentColor = color,
        content = content
    )
}

@Composable
fun RiseSetText(value: Int, tz: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = DateUtils.getDateTime(DateUtils.RISE_SET_FORMAT, (value).toLong(), tz),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
        )

        Text(text = label, fontSize = 18.sp)
    }
}

@Composable
fun VisibleHoursText(hours: String) {
    Text(
        text = hours,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandedSunMoon(daily: Daily, timezone: String) {
    ModalBottomSheet(
        modifier = Modifier.padding(0.dp, 0.dp , 0.dp, 20.dp),
        containerColor = Color(0xD9000000),
        sheetState = rememberModalBottomSheetState(),
        onDismissRequest = { sunWeather.value = null }
    ) {
        SunCard(Modifier.padding(30.dp, 0.dp), daily = daily, tz = timezone)
        Divider(Modifier.padding(0.dp, 15.dp), color = Color.White)
        MoonCard(Modifier.padding(30.dp, 0.dp), daily = daily, tz = timezone)
        Divider(Modifier.padding(0.dp, 20.dp), color = Color.Transparent)
    }
}