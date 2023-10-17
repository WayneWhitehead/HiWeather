package com.hidesign.hiweather.views

import android.Manifest
import android.app.Activity
import android.content.Context
import android.location.Address
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
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
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.hidesign.hiweather.R
import com.hidesign.hiweather.dagger.NetworkModule
import com.hidesign.hiweather.model.Current
import com.hidesign.hiweather.model.Daily
import com.hidesign.hiweather.model.Hourly
import com.hidesign.hiweather.model.OneCallResponse
import com.hidesign.hiweather.network.NetworkStatus
import com.hidesign.hiweather.network.WeatherViewModel
import com.hidesign.hiweather.services.APIWorker
import com.hidesign.hiweather.util.Constants
import com.hidesign.hiweather.util.Extensions.roundToDecimal
import com.hidesign.hiweather.util.LocationUtil
import com.hidesign.hiweather.util.WeatherUtils
import com.hidesign.hiweather.views.ui.theme.HiWeatherTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.text.MessageFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

@AndroidEntryPoint
class MainActivity: ComponentActivity(){

    private val weatherViewModel: WeatherViewModel by viewModels()
    private val locationUtil by lazy { LocationUtil(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationPermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissionsResult ->
            if (permissionsResult.all { it.value }) {
                locationUtil.getLastLocation(
                    onSuccess = { location ->
                        locationUtil.handleLocation(location,
                            onSuccess = { address ->
                                uAddress.value = address
                                val pref = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE)
                                pref.edit().putString(Constants.LATITUDE, address.latitude.toString()).apply()
                                pref.edit().putString(Constants.LONGITUDE, address.longitude.toString()).apply()
                                pref.edit().putString(Constants.LOCALITY, address.locality).apply()
                                CoroutineScope(Dispatchers.Main).launch {
                                    weatherViewModel.getOneCallWeather(this@MainActivity, uAddress.value)
                                    weatherViewModel.getAirPollution(uAddress.value)
                                }
                            },
                            onFailure = {
                                Timber.tag("Tag").e("Error getting Address: ")
                            }
                        )
                    },
                    onFailure =  {
                        Timber.tag("Tag").e("Error getting Location: ")
                    }
                )
            } else {
                weatherViewModel.updateUIState(NetworkStatus.ERROR)
            }
        }
        locationPermissions.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION))
        APIWorker.initWorker(this)

        setContent {
            HiWeatherTheme {
                WeatherScreen(weatherViewModel)
            }
        }
    }
}

private lateinit var locationPermissions: ActivityResultLauncher<Array<String>>
private var uAddress = mutableStateOf(Address(Locale.getDefault()))
val airItemTitle = mutableStateOf("")
val showSettings = mutableStateOf(false)

val sunWeather: MutableState<Daily?> = mutableStateOf(null)
val forecastTimezone: MutableState<String?> = mutableStateOf(null)
val forecastDaily: MutableState<Daily?> = mutableStateOf(null)
val forecastHourly: MutableState<Hourly?> = mutableStateOf(null)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WeatherScreen(weatherViewModel: WeatherViewModel) {
    val context = LocalContext.current
    val uiState = weatherViewModel.uiState.observeAsState()
    val intentLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { activityResult ->
        when (activityResult.resultCode) {
            Activity.RESULT_OK -> {
                activityResult.data?.let {
                    val place = Autocomplete.getPlaceFromIntent(it)
                    Address(Locale.getDefault()).apply {
                        latitude = place.latLng?.latitude ?: 0.0
                        longitude = place.latLng?.longitude ?: 0.0
                        locality = place.name
                        uAddress.value = (this)
                        CoroutineScope(Dispatchers.Main).launch {
                            weatherViewModel.getOneCallWeather(context, uAddress.value)
                            weatherViewModel.getAirPollution(uAddress.value)
                        }
                    }
                }
            }
            AutocompleteActivity.RESULT_ERROR -> {
                weatherViewModel.updateUIState(NetworkStatus.ERROR)
            }
            Activity.RESULT_CANCELED -> {
                runBlocking {
                    delay(500)
                    weatherViewModel.updateUIState(NetworkStatus.SUCCESS)
                }
            }
        }
    }
    val launchMapInputOverlay = {
        Places.initialize(context, NetworkModule.getAPIKey(context, Constants.PLACES_KEY))
        val fields = listOf(Place.Field.NAME, Place.Field.LAT_LNG)
        val intent = Autocomplete
            .IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            .build(context)
        intentLauncher.launch(intent)
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.value == NetworkStatus.LOADING,
        onRefresh = {
            runBlocking {
                weatherViewModel.updateUIState(NetworkStatus.LOADING)
                weatherViewModel.getOneCallWeather(context, uAddress.value)
                weatherViewModel.getAirPollution(uAddress.value)
            }
        }
    )

    Scaffold(
        bottomBar = {
            BottomAppBar(
                cutoutShape = RoundedCornerShape(50.dp),
                backgroundColor = MaterialTheme.colors.background,
            ){
                IconButton(onClick = {
                    showSettings.value = true
                }) {
                    Icon(Icons.Filled.Settings, contentDescription = "Settings")
                }
                IconButton(onClick = {
                    weatherViewModel.updateUIState(NetworkStatus.LOADING)
                    locationPermissions.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
                }) {
                    Icon(Icons.Filled.MyLocation, contentDescription = "Get Location")
                }
                Text(
                    text = uAddress.value.locality ?: "",
                    modifier = Modifier.padding(10.dp)
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { launchMapInputOverlay() },
                shape = RoundedCornerShape(50.dp),
                backgroundColor = MaterialTheme.colors.secondary
            ) {
                Icon(Icons.Filled.Search, "Search")
            }
        },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.End,
        backgroundColor = Color.Black
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column(
                Modifier
                    .pullRefresh(pullRefreshState)
                    .verticalScroll(rememberScrollState())) {
                WeatherViews(weatherViewModel, uiState)
            }
            if (uiState.value == NetworkStatus.LOADING) {
                LoadingScreen()
            }
            if (showSettings.value) {
                SettingsDialog()
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x4D000000)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            Modifier.size(100.dp),
            color = MaterialTheme.colors.secondary,
            strokeWidth = 20.dp
        )
        CircularProgressIndicator(
            Modifier
                .size(120.dp)
                .rotate(45f),
            color = MaterialTheme.colors.primary,
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
fun WeatherViews(weatherViewModel: WeatherViewModel, uiState: State<NetworkStatus?>) {
    val weatherState = weatherViewModel.oneCallResponse.observeAsState()
    val airPollutionState = weatherViewModel.airPollutionResponse.observeAsState()

    ConstraintLayout(modifier = Modifier.height(1200.dp)) {
            val (header, current, hourly, currentExtra, air, wind, sun, daily) = createRefs()
            if (weatherState.value != null && airPollutionState.value != null) {
                val weather = weatherState.value!!
                val airPollution = airPollutionState.value!!

                ForecastCard(
                    Modifier
                        .padding(10.dp)
                        .constrainAs(daily) {
                            top.linkTo(sun.bottom, (-20).dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                    weather,
                    weather.daily.subList(1, 8)
                )
                CurrentExtraCard(
                    Modifier.constrainAs(currentExtra) {
                        top.linkTo(hourly.bottom, (-20).dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                    weather
                )
                AirPollutionCard(
                    Modifier.constrainAs(air) {
                        top.linkTo(currentExtra.bottom, (-20).dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                    airPollution
                )
                WindCard(
                    Modifier.constrainAs(wind) {
                        top.linkTo(air.bottom, (-20).dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                    weather.current!!
                )
                SunCard(
                    Modifier
                        .padding(50.dp, 0.dp)
                        .clickable { sunWeather.value = weather.daily[0] }
                        .constrainAs(sun) {
                            top.linkTo(wind.bottom, (-20).dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                    daily = weather.daily[0],
                    tz = weather.timezone,
                    showHours = false,
                )
                CurrentCard(
                    Modifier.constrainAs(current) {
                        top.linkTo(header.bottom, (-20).dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                    weather
                )
                ForecastCard(
                    Modifier
                        .padding(10.dp)
                        .constrainAs(hourly) {
                            top.linkTo(current.bottom, (-45).dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                    weather,
                    weather.hourly.subList(1,25)
                )
                LocationHeaderCard(
                    Modifier.constrainAs(header) {
                        top.linkTo(parent.top, 20.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                )
                if (airItemTitle.value != "") {
                    AirQualityDialog(airPollution.list[0].components, airItemTitle)
                }
                if (sunWeather.value != null) {
                    ExpandedSunMoon(sunWeather.value!!, weather.timezone)
                }
                if (forecastTimezone.value != null) {
                    ExpandForecast(
                        daily = forecastDaily.value,
                        hourly = forecastHourly.value,
                        timezone = forecastTimezone.value!!
                    )
                }
            }
        }

    if (uiState.value == NetworkStatus.LOADING) {
        LaunchedEffect(key1 = true, block = {
            delay(1000)
            weatherViewModel.updateUIState(NetworkStatus.SUCCESS)
        })
    }
}

@Composable
fun LocationHeaderCard(modifier: Modifier) {
    Card(
        modifier = modifier.wrapContentSize(),
        backgroundColor = MaterialTheme.colors.secondary,
        contentColor = Color.White,
        shape = RoundedCornerShape(30.dp)
    ) {
        val df = SimpleDateFormat("d MMMM HH:mm", Locale.getDefault())
        val formattedDate = df.format(Calendar.getInstance().time)
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
fun CurrentCard(modifier: Modifier, weather: OneCallResponse) {
    Card(
        modifier = modifier
            .padding(5.dp, 0.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        backgroundColor = Color.White,
        contentColor = Color.Black,
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
                    url = WeatherUtils.getWeatherIconUrl(weather.current?.weather!![0].icon),
                    contentDescription = "Weather icon"
                )

                Text(
                    text = MessageFormat.format(
                        context.getString(R.string._0_c),
                        weather.current?.temp?.roundToInt()
                    ),
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
                            text = MessageFormat.format(
                                context.getString(R.string._0_c),
                                weather.daily[0].temp.max.roundToInt()
                            ),
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
                            text = MessageFormat.format(
                                context.getString(R.string._0_c),
                                weather.daily[0].temp.min.roundToInt()
                            ),
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = MessageFormat.format(
                        context.getString(R.string.real_feel_0_c),
                        weather.current?.feelsLike?.roundToInt()
                    ),
                    fontSize = 16.sp
                )

                Divider(
                    Modifier
                        .width(20.dp)
                        .padding(5.dp, 0.dp),
                    thickness = 1.dp,
                    color = Color.Black
                )

                Image(
                    painter = painterResource(id = R.drawable.uv),
                    contentDescription = "UV index",
                    modifier = Modifier
                        .size(25.dp)
                        .padding(end = 5.dp),
                )

                Text(
                    text = weather.current?.uvi?.toInt().toString(),
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

@Composable
fun CurrentExtraCard(modifier: Modifier, weather: OneCallResponse) {
    Card(
        modifier = modifier
            .wrapContentWidth()
            .wrapContentHeight()
            .padding(10.dp, 0.dp),
        backgroundColor = Color(0xFF174488),
        contentColor = Color.White,
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
                    CurrentImageLabel(
                        forecastItem = MessageFormat.format(stringResource(id = R.string.precipitation_0), weather.current!!.dewPoint.roundToInt()),
                        image = painterResource(id = R.drawable.rain),
                        size = 16
                    )
                    CurrentImageLabel(
                        forecastItem = MessageFormat.format(stringResource(id = R.string.humidity_0), weather.current!!.humidity),
                        image = painterResource(id = R.drawable.precipitation),
                        size = 16
                    )
                    CurrentImageLabel(
                        forecastItem = MessageFormat.format(stringResource(id = R.string.cloudiness_0), weather.current!!.clouds),
                        image = painterResource(id = R.drawable.precipitation),
                        size = 16
                    )
                }
            }
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    CurrentImageLabel(
                        forecastItem = MessageFormat.format(stringResource(id = R.string.dew_point_0_c), weather.current!!.dewPoint.roundToInt()),
                        image = painterResource(id = R.drawable.dew_point),
                        size = 16
                    )
                    CurrentImageLabel(
                        forecastItem = MessageFormat.format(stringResource(id = R.string.pressure_0_hpa), weather.current!!.pressure / 1000),
                        image = painterResource(id = R.drawable.pressure),
                        size = 16
                    )
                    CurrentImageLabel(
                        forecastItem = MessageFormat.format(stringResource(id = R.string.visibility_0_m), weather.current!!.visibility / 1000),
                        image = painterResource(id = R.drawable.visibility),
                        size = 16
                    )
                }
            }
        }
    }
}

@Composable
fun WindCard(modifier: Modifier, current: Current) {
    Card(
        modifier = modifier
            .padding(65.dp, 0.dp)
            .wrapContentSize(),
        backgroundColor = Color.White,
        shape = RoundedCornerShape(30.dp)
    ) {
        Box (Modifier.height(100.dp)) {
            Image(
                painter = painterResource(id = R.drawable.airwaves),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.tint(Color.Cyan)
            )
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 10.dp, 0.dp, 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = current.windSpeed.roundToDecimal().toString(),
                        fontSize = 54.sp,
                        color = Color.Black
                    )
                    Column {
                        Image(
                            painter = painterResource(id = R.drawable.direction),
                            contentDescription = "Wind direction icon",
                            modifier = Modifier
                                .rotate((current.windDeg - 270).toFloat())
                                .size(25.dp),
                        )
                        Text(
                            text = "m/s",
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                    }
                    Text(
                        text = WeatherUtils.getWindDegreeText(current.windDeg),
                        fontSize = 22.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}