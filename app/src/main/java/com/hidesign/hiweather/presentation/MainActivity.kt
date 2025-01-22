package com.hidesign.hiweather.presentation

import android.Manifest
import android.app.Activity
import android.location.Address
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.fragment.app.FragmentActivity
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.hidesign.hiweather.BuildConfig
import com.hidesign.hiweather.R
import com.hidesign.hiweather.data.model.Current
import com.hidesign.hiweather.data.model.ErrorType
import com.hidesign.hiweather.data.model.OneCallResponse
import com.hidesign.hiweather.presentation.components.AirPollutionCard
import com.hidesign.hiweather.presentation.components.ForecastCard
import com.hidesign.hiweather.presentation.components.ShimmerEffect
import com.hidesign.hiweather.presentation.components.SolarCard
import com.hidesign.hiweather.presentation.dialog.AirPollutionSheet
import com.hidesign.hiweather.presentation.dialog.CelestialSheet
import com.hidesign.hiweather.presentation.dialog.ForecastSheet
import com.hidesign.hiweather.presentation.dialog.SettingsDialog
import com.hidesign.hiweather.presentation.ui.theme.HiWeatherTheme
import com.hidesign.hiweather.services.APIWorker
import com.hidesign.hiweather.util.AdUtil
import com.hidesign.hiweather.util.Extensions.roundToDecimal
import com.hidesign.hiweather.util.WeatherUtil
import com.permissionx.guolindev.PermissionX
import dagger.hilt.android.AndroidEntryPoint
import java.text.MessageFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt

@AndroidEntryPoint
class MainActivity: FragmentActivity(){

    private val weatherViewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HiWeatherTheme {
                WeatherScreen(weatherViewModel)
            }
        }
        requestPermission(listOf(PermissionX.permission.POST_NOTIFICATIONS, Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    fun requestPermission(permissions: List<String>) {
        PermissionX.init(this)
            .permissions(permissions)
            .onExplainRequestReason { scope, deniedList ->
                if (deniedList.contains(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    run {
                        scope.showRequestReasonDialog(
                            deniedList,
                            "Core fundamental are based on these permissions",
                            "OK",
                            "Cancel"
                        )
                    }
                }
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(deniedList, "You need to allow necessary permissions in Settings manually", "OK", "Cancel")
            }
            .request { _, grantedList, deniedList ->
                grantedList.forEach {
                    if (it == Manifest.permission.ACCESS_COARSE_LOCATION) {
                        weatherViewModel.fetchWeather()
                    }
                }
                deniedList.forEach {
                    when (it) {
                        Manifest.permission.ACCESS_COARSE_LOCATION -> {
                            weatherViewModel.showErrorDialog(ErrorType.LOCATION_PERMISSION_ERROR)
                        }
                    }
                }
            }
    }
}

@Composable
fun WeatherScreen(weatherViewModel: WeatherViewModel) {
    val context = LocalContext.current
    val state by weatherViewModel.state.collectAsState()
    var showSettings by remember { mutableStateOf(false) }

    val intentLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { activityResult ->
        when (activityResult.resultCode) {
            Activity.RESULT_OK -> {
                activityResult.data?.let {
                    val place = Autocomplete.getPlaceFromIntent(it)
                    val address = Address(Locale.getDefault()).apply {
                        latitude = place.latLng?.latitude ?: 0.0
                        longitude = place.latLng?.longitude ?: 0.0
                        locality = place.name
                    }
                    weatherViewModel.fetchWeather(address)
                }
            }
            AutocompleteActivity.RESULT_ERROR -> {
                weatherViewModel.showErrorDialog(ErrorType.PLACES_ERROR)
            }
            Activity.RESULT_CANCELED -> {}
        }
    }
    Scaffold(
        bottomBar = {
            BottomAppBar(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                IconButton(onClick = { showSettings = true }) {
                    Icon(Icons.Filled.Settings, contentDescription = "Settings")
                }
                IconButton(onClick = { weatherViewModel.fetchWeather() }) {
                    Icon(Icons.Filled.MyLocation, contentDescription = "Get Location")
                }
                Text(
                    text = state.lastUsedAddress?.locality ?: "",
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { Places.initialize(context, BuildConfig.PLACES_KEY)
                    val fields = listOf(Place.Field.NAME, Place.Field.LAT_LNG)
                    val intent = Autocomplete
                        .IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                        .build(context)
                    intentLauncher.launch(intent)
                },
                shape = RoundedCornerShape(50.dp),
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(Icons.Filled.Search, "Search")
            }
        },
        floatingActionButtonPosition = FabPosition.EndOverlay,
        containerColor = Color.Black
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            AnimatedVisibility(
                visible = state.lastUsedAddress == null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                LoadingScreen()
            }

            WeatherViews(weatherViewModel)

            AnimatedVisibility(state.errorType != null) {
                ErrorScreen(state.errorType!!)
            }
            AnimatedVisibility(state.airPollutionDialogState.show) {
                AirPollutionSheet(state.airPollutionDialogState) {
                    weatherViewModel.hideAirPollutionDialog()
                }
            }
            AnimatedVisibility(state.celestialDialogState.show) {
                CelestialSheet(state.celestialDialogState) {
                    weatherViewModel.hideCelestialDialog()
                }
            }
            AnimatedVisibility(state.forecastDialogState.show && state.forecastDialogState.weather.weather.isNotEmpty()) {
                ForecastSheet(state.forecastDialogState) {
                    weatherViewModel.hideForecastDialog()
                }
            }

            SettingsDialog(showSettings) {
                showSettings = false
                if (it == 0) {
                    APIWorker.cancelWorker(context)
                } else {
                    APIWorker.initWorker(context)
                }
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            Modifier.size(100.dp),
            color = MaterialTheme.colorScheme.secondary,
            strokeWidth = 20.dp
        )
        CircularProgressIndicator(
            Modifier
                .size(120.dp)
                .rotate(45f),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 20.dp
        )
        CircularProgressIndicator(
            Modifier
                .size(140.dp)
                .rotate(90f),
            color = Color(0xffff6659),
            strokeWidth = 20.dp
        )
        CircularProgressIndicator(
            Modifier
                .size(160.dp)
                .rotate(135f),
            color = Color.White,
            strokeWidth = 20.dp
        )
    }
}

@Composable
fun ErrorScreen(error: ErrorType) {
    val context = LocalContext.current
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier
                .padding(horizontal = 20.dp)
                .align(Alignment.Center)) {
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(70.dp),
                imageVector = error.icon,
                tint = Color.White,
                contentDescription = "Error Icon"
            )

            HorizontalDivider(color = Color.White, modifier = Modifier.padding(0.dp, 20.dp))

            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = error.message,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )

            when(error) {
                ErrorType.LOCATION_PERMISSION_ERROR -> {
                    Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        text = "This app needs location permissions to work properly. Please enable the permissions in the app settings.",
                        color = Color.White,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    TextButton(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        onClick = { (context as MainActivity).requestPermission(listOf( Manifest.permission.ACCESS_COARSE_LOCATION)) }
                    ) {
                        Text("Grant Permission")
                    }
                }
                else -> {}
            }

            HorizontalDivider(color = Color.White, modifier = Modifier.padding(0.dp, 10.dp))
        }
    }
}

@Composable
fun WeatherViews(weatherViewModel: WeatherViewModel) {
    val state by weatherViewModel.state.collectAsState()
    LazyColumn(
        modifier = Modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            AdViewComposable(
                modifier = Modifier,
                adUnitId = AdUtil.APP_BAR_AD
            )
        }

        item {
            DateHeaderCard(
                modifier = Modifier.zIndex(1f),
                tz = state.oneCallResponse?.timezone ?: TimeZone.getDefault().displayName
            )
        }

        item {
            CurrentCard(
                modifier = Modifier.offset(y = (-20).dp).zIndex(0f),
                oneCallResponse = state.oneCallResponse
            )
        }

        item {
            ForecastCard(
                modifier = Modifier.offset(y = (-70).dp).zIndex(2f),
                weatherList = state.oneCallResponse?.hourly?.subList(1, 25),
                tz = state.oneCallResponse?.timezone,
                weatherViewModel = weatherViewModel
            )
        }

        item {
            CurrentExtraCard(
                modifier = Modifier.offset(y = (-75).dp).zIndex(1f),
                oneCallResponse = state.oneCallResponse
            )
        }

        item {
            AirPollutionCard(
                modifier = Modifier.offset(y = (-100).dp).zIndex(2f),
                weatherViewModel = weatherViewModel
            )
        }

        item {
            SolarCard(
                modifier = Modifier
                    .clickable {
                        state.oneCallResponse?.let {
                            weatherViewModel.showCelestialDialog(state.oneCallResponse!!.daily[0], state.oneCallResponse!!.timezone)
                        }
                    }
                    .offset(y = (-120).dp),
                daily = state.oneCallResponse?.daily?.get(0),
                tz = state.oneCallResponse?.timezone ?: TimeZone.getDefault().displayName,
                showHours = false,
            )
        }

        item {
            WindCard(
                modifier = Modifier.offset(y = (-140).dp),
                current = state.oneCallResponse?.current
            )
        }

        item {
            ForecastCard(
                modifier = Modifier.offset(y = (-160).dp),
                weatherList = state.oneCallResponse?.hourly?.subList(1, 25),
                tz = state.oneCallResponse?.timezone,
                weatherViewModel = weatherViewModel
            )
        }
    }
}

@Composable
fun DateHeaderCard(modifier: Modifier, tz: String?) {
    Card(
        modifier = modifier.wrapContentSize().padding(top = 10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(30.dp)
    ) {
        val df = SimpleDateFormat("d MMMM HH:mm", Locale.getDefault())
        df.timeZone = TimeZone.getTimeZone(tz)
        val formattedDate = df.format(Calendar.getInstance(df.timeZone).time)
        Text(
            text = formattedDate,
            modifier = Modifier.padding(20.dp),
            color = Color.White,
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CurrentCard(modifier: Modifier, oneCallResponse: OneCallResponse?) {
    val containerColour = Color.White

    Crossfade(targetState = oneCallResponse, label = "") { weather ->
        when (weather) {
            null -> ShimmerEffect(
                modifier = modifier.height(260.dp),
                color = containerColour,
            )
            else -> Card(
                modifier = modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                colors = CardDefaults.cardColors(
                    containerColor = containerColour,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(30.dp)
            ) {
                val context = LocalContext.current
                Column(
                    Modifier
                        .padding(0.dp, 20.dp, 0.dp, 50.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        LoadPicture(
                            Modifier
                                .height(80.dp)
                                .width(80.dp)
                                .align(Alignment.CenterVertically),
                            url = WeatherUtil.getWeatherIconUrl(weather.current.weather[0].icon),
                            contentDescription = "Weather icon"
                        )

                        Text(
                            text = MessageFormat.format(context.getString(R.string._0_c), weather.current.temp.roundToInt()),
                            fontSize = 55.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )

                        Column {
                            Row(
                                Modifier.padding(top = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    imageVector = Icons.Outlined.KeyboardArrowUp,
                                    contentDescription = "High temperature",
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = MessageFormat.format(context.getString(R.string._0_c), weather.daily[0].temp.max.roundToInt()),
                                    fontSize = 16.sp
                                )
                            }

                            Row(
                                Modifier.padding(top = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    imageVector = Icons.Outlined.KeyboardArrowDown,
                                    contentDescription = "Low temperature",
                                    modifier = Modifier.size(16.dp)
                                )

                                Text(
                                    text = MessageFormat.format(context.getString(R.string._0_c), weather.daily[0].temp.min.roundToInt()),
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = MessageFormat.format(context.getString(R.string.real_feel_0_c), weather.current.feelsLike.roundToInt()),
                            fontSize = 16.sp
                        )

                        HorizontalDivider(
                            Modifier.width(20.dp).padding(5.dp, 0.dp),
                            thickness = 1.dp,
                            color = Color.Black
                        )

                        Image(
                            painter = painterResource(id = R.drawable.uv),
                            contentDescription = "UV index",
                            modifier = Modifier.size(25.dp).padding(end = 5.dp),
                        )

                        Text(
                            text = weather.current.uvi.toInt().toString(),
                            fontSize = 16.sp
                        )
                    }

                    Text(
                        text = weather.daily[0].summary,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(10.dp, 0.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CurrentExtraCard(modifier: Modifier, oneCallResponse: OneCallResponse?) {
    val containerColour = Color(0xFF174488)

    Crossfade(targetState = oneCallResponse, label = "") { weather ->
        when (weather) {
            null -> ShimmerEffect(
                modifier = modifier.height(160.dp),
                color = containerColour,
            )
            else -> Card(
                modifier = modifier.wrapContentSize().padding(10.dp, 0.dp),
                colors = CardDefaults.cardColors(
                    containerColor = containerColour,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(30.dp)
            ) {
                LazyVerticalGrid(
                    modifier = Modifier.padding(10.dp, 30.dp, 10.dp, 40.dp),
                    columns = GridCells.Fixed(2)
                ) {
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            ForecastImageLabel(
                                forecastItem = MessageFormat.format(
                                    stringResource(id = R.string.precipitation_0),
                                    weather.daily[0].pop.roundToInt()
                                ),
                                image = painterResource(id = R.drawable.rain),
                                size = 18
                            )
                            ForecastImageLabel(
                                forecastItem = MessageFormat.format(
                                    stringResource(id = R.string.humidity_0),
                                    weather.current.humidity
                                ),
                                image = painterResource(id = R.drawable.precipitation),
                                size = 18
                            )
                            ForecastImageLabel(
                                forecastItem = MessageFormat.format(
                                    stringResource(id = R.string.cloudiness_0),
                                    weather.current.clouds
                                ),
                                image = painterResource(id = R.drawable.precipitation),
                                size = 18
                            )
                        }
                    }
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            ForecastImageLabel(
                                forecastItem = MessageFormat.format(
                                    stringResource(id = R.string.dew_point_0_c),
                                    weather.current.dewPoint.roundToInt()
                                ),
                                image = painterResource(id = R.drawable.dew_point),
                                size = 18
                            )
                            ForecastImageLabel(
                                forecastItem = MessageFormat.format(
                                    stringResource(id = R.string.pressure_0_hpa),
                                    weather.current.pressure / 1000
                                ),
                                image = painterResource(id = R.drawable.pressure),
                                size = 18
                            )
                            ForecastImageLabel(
                                forecastItem = MessageFormat.format(
                                    stringResource(id = R.string.visibility_0_m),
                                    weather.current.visibility / 1000
                                ),
                                image = painterResource(id = R.drawable.visibility),
                                size = 18
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WindCard(modifier: Modifier, current: Current?) {
    val containerColour = Color.White

    Crossfade(targetState = current, label = "") { weather ->
        when (weather) {
            null -> ShimmerEffect(
                modifier = modifier
                    .padding(10.dp, 0.dp)
                    .fillMaxWidth()
                    .height(300.dp),
                color = containerColour,
            )
            else -> Card(
                modifier = modifier.padding(horizontal = 65.dp).wrapContentSize(),
                colors = CardDefaults.cardColors(
                    containerColor = containerColour,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(30.dp)
            ) {
                Box(Modifier.height(100.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.airwaves),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        colorFilter = ColorFilter.tint(Color.Cyan)
                    )
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = weather.windSpeed.roundToDecimal().toString(),
                                fontSize = 54.sp,
                                color = Color.Black
                            )
                            Column {
                                Image(
                                    painter = painterResource(id = R.drawable.direction),
                                    contentDescription = "Wind direction icon",
                                    modifier = Modifier
                                        .rotate((weather.windDeg - 270).toFloat())
                                        .size(25.dp),
                                )
                                Text(
                                    text = "m/s",
                                    fontSize = 18.sp,
                                    color = Color.Black
                                )
                            }
                            Text(
                                text = WeatherUtil.getWindDegreeText(weather.windDeg),
                                fontSize = 22.sp,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}