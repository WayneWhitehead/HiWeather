package com.hidesign.hiweather;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.mmin18.widget.RealtimeBlurView;
import com.hidesign.hiweather.Models.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.hidesign.hiweather.SplashActivity.uAddress;
import static com.hidesign.hiweather.SplashActivity.uLocation;

public class ScrollingActivity extends AppCompatActivity {
    String result, locationKey, CurrentConditionsResult;
    public Weather weatherCurrent = new Weather();
    public ArrayList<Weather> forecast = new ArrayList<>();
    public TextView _CurrentTemp, _LowTemp, _HighTemp, _RealFeel, _Precipitation, _Humidity, _Sunrise, _Sunset, _Pressure, _WindSpeed, _DewPoint, _UVIndex, _Visibility, _WindDirectionText, _Date;
    public ImageView _WindDirectionDegrees, _Skies;
    public SwipeRefreshLayout swipeRefreshLayout;
    public int sky;
    public RealtimeBlurView blurBottom;
    private SearchView searchView;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(uAddress);
        setSupportActionBar(toolbar);

        blurBottom = findViewById(R.id.mainBlur);

        searchView = findViewById(R.id.action_search);

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(ScrollingActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);

        _Date = findViewById(R.id.date);
        SimpleDateFormat df = new SimpleDateFormat("dd-MMMM-yyyy");
        String formattedDate = df.format(Calendar.getInstance().getTime());
        _Date.setText(formattedDate);
        _CurrentTemp = findViewById(R.id.CurrentTemp);
        _LowTemp = findViewById(R.id.LowTemp);
        _HighTemp = findViewById(R.id.HighTemp);
        _RealFeel = findViewById(R.id.RealFeelTemp);
        _Skies = findViewById(R.id.skiesImage);
        _Precipitation = findViewById(R.id.Precipitation);
        _Humidity = findViewById(R.id.Humidity);
        _Sunrise = findViewById(R.id.Sunrise);
        _Sunset = findViewById(R.id.Sunset);
        _Pressure = findViewById(R.id.Pressure);
        _WindDirectionDegrees = findViewById(R.id.WindDirectionDegrees);
        _WindSpeed = findViewById(R.id.WindSpeed);
        _DewPoint = findViewById(R.id.DewPoint);
        _UVIndex = findViewById(R.id.UVIndex);
        _Visibility = findViewById(R.id.Visibility);
        _WindDirectionText = findViewById(R.id.WindDirectionText);

        AsyncTask myasynctask =new AsyncTask();
        myasynctask.execute();

        swipeRefreshLayout = findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            blurScreen(0, 40);
            AsyncTask refreshTask =new AsyncTask();
            refreshTask.execute();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    class AsyncTask extends android.os.AsyncTask<Void, Void, String>
    {
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            //public String TAG = "Main Activity";
            String apiKey = "aDv8fsGqxTBQ0zmXKfqxLA53uuCnJK4Z";
            String urll = "http://dataservice.accuweather.com/locations/v1/cities/geoposition/search?apikey="+ apiKey +"&q=" +uLocation.getLatitude()+"%2C"+uLocation.getLongitude();
            UrlConnection urlconnect = new UrlConnection();
            result = urlconnect.Url(urll);

            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    locationKey = jsonObject.getString("Key");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //locationKey = "298833";
            if(locationKey!=null) {
                CurrentConditionsResult = urlconnect.CurrentConditions(locationKey);
                //CurrentConditionsResult = "[{\"LocalObservationDateTime\":\"2020-01-15T11:56:00+02:00\",\"EpochTime\":1579082160,\"WeatherText\":\"Mostly sunny\",\"WeatherIcon\":2,\"HasPrecipitation\":false,\"PrecipitationType\":null,\"IsDayTime\":true,\"Temperature\":{\"Metric\":{\"Value\":30.1,\"Unit\":\"C\",\"UnitType\":17},\"Imperial\":{\"Value\":86.0,\"Unit\":\"F\",\"UnitType\":18}},\"RealFeelTemperature\":{\"Metric\":{\"Value\":34.9,\"Unit\":\"C\",\"UnitType\":17},\"Imperial\":{\"Value\":95.0,\"Unit\":\"F\",\"UnitType\":18}},\"RealFeelTemperatureShade\":{\"Metric\":{\"Value\":30.8,\"Unit\":\"C\",\"UnitType\":17},\"Imperial\":{\"Value\":87.0,\"Unit\":\"F\",\"UnitType\":18}},\"RelativeHumidity\":56,\"DewPoint\":{\"Metric\":{\"Value\":20.4,\"Unit\":\"C\",\"UnitType\":17},\"Imperial\":{\"Value\":69.0,\"Unit\":\"F\",\"UnitType\":18}},\"Wind\":{\"Direction\":{\"Degrees\":90,\"Localized\":\"E\",\"English\":\"E\"},\"Speed\":{\"Metric\":{\"Value\":12.6,\"Unit\":\"km/h\",\"UnitType\":7},\"Imperial\":{\"Value\":7.8,\"Unit\":\"mi/h\",\"UnitType\":9}}},\"WindGust\":{\"Speed\":{\"Metric\":{\"Value\":17.3,\"Unit\":\"km/h\",\"UnitType\":7},\"Imperial\":{\"Value\":10.8,\"Unit\":\"mi/h\",\"UnitType\":9}}},\"UVIndex\":8,\"UVIndexText\":\"Very High\",\"Visibility\":{\"Metric\":{\"Value\":17.7,\"Unit\":\"km\",\"UnitType\":6},\"Imperial\":{\"Value\":11.0,\"Unit\":\"mi\",\"UnitType\":2}},\"ObstructionsToVisibility\":\"\",\"CloudCover\":25,\"Ceiling\":{\"Metric\":{\"Value\":1250.0,\"Unit\":\"m\",\"UnitType\":5},\"Imperial\":{\"Value\":4100.0,\"Unit\":\"ft\",\"UnitType\":0}},\"Pressure\":{\"Metric\":{\"Value\":1016.0,\"Unit\":\"mb\",\"UnitType\":14},\"Imperial\":{\"Value\":30.0,\"Unit\":\"inHg\",\"UnitType\":12}},\"PressureTendency\":{\"LocalizedText\":\"Steady\",\"Code\":\"S\"},\"Past24HourTemperatureDeparture\":{\"Metric\":{\"Value\":2.5,\"Unit\":\"C\",\"UnitType\":17},\"Imperial\":{\"Value\":4.0,\"Unit\":\"F\",\"UnitType\":18}},\"ApparentTemperature\":{\"Metric\":{\"Value\":32.2,\"Unit\":\"C\",\"UnitType\":17},\"Imperial\":{\"Value\":90.0,\"Unit\":\"F\",\"UnitType\":18}},\"WindChillTemperature\":{\"Metric\":{\"Value\":30.0,\"Unit\":\"C\",\"UnitType\":17},\"Imperial\":{\"Value\":86.0,\"Unit\":\"F\",\"UnitType\":18}},\"WetBulbTemperature\":{\"Metric\":{\"Value\":23.5,\"Unit\":\"C\",\"UnitType\":17},\"Imperial\":{\"Value\":74.0,\"Unit\":\"F\",\"UnitType\":18}},\"Precip1hr\":{\"Metric\":{\"Value\":0.0,\"Unit\":\"mm\",\"UnitType\":3},\"Imperial\":{\"Value\":0.0,\"Unit\":\"in\",\"UnitType\":1}},\"PrecipitationSummary\":{\"Precipitation\":{\"Metric\":{\"Value\":0.0,\"Unit\":\"mm\",\"UnitType\":3},\"Imperial\":{\"Value\":0.0,\"Unit\":\"in\",\"UnitType\":1}},\"PastHour\":{\"Metric\":{\"Value\":0.0,\"Unit\":\"mm\",\"UnitType\":3},\"Imperial\":{\"Value\":0.0,\"Unit\":\"in\",\"UnitType\":1}},\"Past3Hours\":{\"Metric\":{\"Value\":0.0,\"Unit\":\"mm\",\"UnitType\":3},\"Imperial\":{\"Value\":0.0,\"Unit\":\"in\",\"UnitType\":1}},\"Past6Hours\":{\"Metric\":{\"Value\":0.0,\"Unit\":\"mm\",\"UnitType\":3},\"Imperial\":{\"Value\":0.0,\"Unit\":\"in\",\"UnitType\":1}},\"Past9Hours\":{\"Metric\":{\"Value\":0.0,\"Unit\":\"mm\",\"UnitType\":3},\"Imperial\":{\"Value\":0.0,\"Unit\":\"in\",\"UnitType\":1}},\"Past12Hours\":{\"Metric\":{\"Value\":0.0,\"Unit\":\"mm\",\"UnitType\":3},\"Imperial\":{\"Value\":0.0,\"Unit\":\"in\",\"UnitType\":1}},\"Past18Hours\":{\"Metric\":{\"Value\":0.0,\"Unit\":\"mm\",\"UnitType\":3},\"Imperial\":{\"Value\":0.0,\"Unit\":\"in\",\"UnitType\":1}},\"Past24Hours\":{\"Metric\":{\"Value\":1.4,\"Unit\":\"mm\",\"UnitType\":3},\"Imperial\":{\"Value\":0.06,\"Unit\":\"in\",\"UnitType\":1}}},\"TemperatureSummary\":{\"Past6HourRange\":{\"Minimum\":{\"Metric\":{\"Value\":20.2,\"Unit\":\"C\",\"UnitType\":17},\"Imperial\":{\"Value\":68.0,\"Unit\":\"F\",\"UnitType\":18}},\"Maximum\":{\"Metric\":{\"Value\":30.1,\"Unit\":\"C\",\"UnitType\":17},\"Imperial\":{\"Value\":86.0,\"Unit\":\"F\",\"UnitType\":18}}},\"Past12HourRange\":{\"Minimum\":{\"Metric\":{\"Value\":20.2,\"Unit\":\"C\",\"UnitType\":17},\"Imperial\":{\"Value\":68.0,\"Unit\":\"F\",\"UnitType\":18}},\"Maximum\":{\"Metric\":{\"Value\":30.1,\"Unit\":\"C\",\"UnitType\":17},\"Imperial\":{\"Value\":86.0,\"Unit\":\"F\",\"UnitType\":18}}},\"Past24HourRange\":{\"Minimum\":{\"Metric\":{\"Value\":20.2,\"Unit\":\"C\",\"UnitType\":17},\"Imperial\":{\"Value\":68.0,\"Unit\":\"F\",\"UnitType\":18}},\"Maximum\":{\"Metric\":{\"Value\":30.1,\"Unit\":\"C\",\"UnitType\":17},\"Imperial\":{\"Value\":86.0,\"Unit\":\"F\",\"UnitType\":18}}}},\"MobileLink\":\"http://m.accuweather.com/en/za/athlone/298833/current-weather/298833?lang=en-us\",\"Link\":\"http://www.accuweather.com/en/za/athlone/298833/current-weather/298833?lang=en-us\"}]";
                if (CurrentConditionsResult != null) {
                    try {
                        JSONArray jsonArrayresult = new JSONArray(CurrentConditionsResult);
                        JSONObject jsonObject = jsonArrayresult.getJSONObject(0);

                        weatherCurrent = new Weather(
                                "Today",
                                jsonObject.getJSONObject("Wind").getJSONObject("Direction").getString("Localized"),
                                jsonObject.getString("UVIndexText"),
                                jsonObject.getJSONObject("Temperature").getJSONObject("Metric").getDouble("Value"),
                                jsonObject.getJSONObject("RealFeelTemperature").getJSONObject("Metric").getDouble("Value"),
                                jsonObject.getJSONObject("TemperatureSummary").getJSONObject("Past24HourRange").getJSONObject("Minimum").getJSONObject("Metric").getDouble("Value"),
                                jsonObject.getJSONObject("TemperatureSummary").getJSONObject("Past24HourRange").getJSONObject("Maximum").getJSONObject("Metric").getDouble("Value"),
                                jsonObject.getJSONObject("Wind").getJSONObject("Speed").getJSONObject("Metric").getDouble("Value"),
                                jsonObject.getJSONObject("DewPoint").getJSONObject("Metric").getDouble("Value"),
                                jsonObject.getJSONObject("Visibility").getJSONObject("Metric").getDouble("Value"),
                                jsonObject.getInt("RelativeHumidity"),
                                jsonObject.getInt("UVIndex"),
                                jsonObject.getJSONObject("Pressure").getJSONObject("Metric").getInt("Value"),
                                jsonObject.getJSONObject("PrecipitationSummary").getJSONObject("Precipitation").getJSONObject("Metric").getInt("Value"),
                                jsonObject.getJSONObject("Wind").getJSONObject("Direction").getInt("Degrees"));
                        sky = jsonObject.getInt("WeatherIcon");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                CurrentConditionsResult = urlconnect.forecast(locationKey);
                //CurrentConditionsResult = "{\"Headline\":{\"EffectiveDate\":\"2020-01-18T01:00:00+02:00\",\"EffectiveEpochDate\":1579302000,\"Severity\":5,\"Text\":\"Expect showers late Friday night\",\"Category\":\"rain\",\"EndDate\":\"2020-01-18T07:00:00+02:00\",\"EndEpochDate\":1579323600,\"MobileLink\":\"http://m.accuweather.com/en/za/athlone/298833/extended-weather-forecast/298833?unit=c&lang=en-us\",\"Link\":\"http://www.accuweather.com/en/za/athlone/298833/daily-weather-forecast/298833?unit=c&lang=en-us\"},\"DailyForecasts\":[{\"Date\":\"2020-01-16T07:00:00+02:00\",\"EpochDate\":1579150800,\"Sun\":{\"Rise\":\"2020-01-16T05:10:00+02:00\",\"EpochRise\":1579144200,\"Set\":\"2020-01-16T19:00:00+02:00\",\"EpochSet\":1579194000},\"Moon\":{\"Rise\":\"2020-01-16T23:22:00+02:00\",\"EpochRise\":1579209720,\"Set\":\"2020-01-17T12:02:00+02:00\",\"EpochSet\":1579255320,\"Phase\":\"WaningGibbous\",\"Age\":21},\"Temperature\":{\"Minimum\":{\"Value\":20.3,\"Unit\":\"C\",\"UnitType\":17},\"Maximum\":{\"Value\":33.0,\"Unit\":\"C\",\"UnitType\":17}},\"RealFeelTemperature\":{\"Minimum\":{\"Value\":21.6,\"Unit\":\"C\",\"UnitType\":17},\"Maximum\":{\"Value\":38.1,\"Unit\":\"C\",\"UnitType\":17}},\"RealFeelTemperatureShade\":{\"Minimum\":{\"Value\":21.6,\"Unit\":\"C\",\"UnitType\":17},\"Maximum\":{\"Value\":33.7,\"Unit\":\"C\",\"UnitType\":17}},\"HoursOfSun\":13.1,\"DegreeDaySummary\":{\"Heating\":{\"Value\":0.0,\"Unit\":\"C\",\"UnitType\":17},\"Cooling\":{\"Value\":9.0,\"Unit\":\"C\",\"UnitType\":17}},\"AirAndPollen\":[{\"Name\":\"AirQuality\",\"Value\":0,\"Category\":\"Good\",\"CategoryValue\":1,\"Type\":\"Ozone\"},{\"Name\":\"Grass\",\"Value\":0,\"Category\":\"Low\",\"CategoryValue\":1},{\"Name\":\"Mold\",\"Value\":0,\"Category\":\"Low\",\"CategoryValue\":1},{\"Name\":\"Ragweed\",\"Value\":0,\"Category\":\"Low\",\"CategoryValue\":1},{\"Name\":\"Tree\",\"Value\":0,\"Category\":\"Low\",\"CategoryValue\":1},{\"Name\":\"UVIndex\",\"Value\":12,\"Category\":\"Extreme\",\"CategoryValue\":5}],\"Day\":{\"Icon\":1,\"IconPhrase\":\"Sunny\",\"HasPrecipitation\":false,\"ShortPhrase\":\"Brilliant sunshine\",\"LongPhrase\":\"Brilliant sunshine\",\"PrecipitationProbability\":1,\"ThunderstormProbability\":0,\"RainProbability\":1,\"SnowProbability\":0,\"IceProbability\":0,\"Wind\":{\"Speed\":{\"Value\":11.1,\"Unit\":\"km/h\",\"UnitType\":7},\"Direction\":{\"Degrees\":51,\"Localized\":\"NE\",\"English\":\"NE\"}},\"WindGust\":{\"Speed\":{\"Value\":22.2,\"Unit\":\"km/h\",\"UnitType\":7},\"Direction\":{\"Degrees\":94,\"Localized\":\"E\",\"English\":\"E\"}},\"TotalLiquid\":{\"Value\":0.0,\"Unit\":\"mm\",\"UnitType\":3},\"Rain\":{\"Value\":0.0,\"Unit\":\"mm\",\"UnitType\":3},\"Snow\":{\"Value\":0.0,\"Unit\":\"cm\",\"UnitType\":4},\"Ice\":{\"Value\":0.0,\"Unit\":\"mm\",\"UnitType\":3},\"HoursOfPrecipitation\":0.0,\"HoursOfRain\":0.0,\"HoursOfSnow\":0.0,\"HoursOfIce\":0.0,\"CloudCover\":5},\"Night\":{\"Icon\":34,\"IconPhrase\":\"Mostly clear\",\"HasPrecipitation\":false,\"ShortPhrase\":\"Mainly clear and humid\",\"LongPhrase\":\"Clear to partly cloudy and humid\",\"PrecipitationProbability\":1,\"ThunderstormProbability\":0,\"RainProbability\":1,\"SnowProbability\":0,\"IceProbability\":0,\"Wind\":{\"Speed\":{\"Value\":7.4,\"Unit\":\"km/h\",\"UnitType\":7},\"Direction\":{\"Degrees\":8,\"Localized\":\"N\",\"English\":\"N\"}},\"WindGust\":{\"Speed\":{\"Value\":16.7,\"Unit\":\"km/h\",\"UnitType\":7},\"Direction\":{\"Degrees\":50,\"Localized\":\"NE\",\"English\":\"NE\"}},\"TotalLiquid\":{\"Value\":0.0,\"Unit\":\"mm\",\"UnitType\":3},\"Rain\":{\"Value\":0.0,\"Unit\":\"mm\",\"UnitType\":3},\"Snow\":{\"Value\":0.0,\"Unit\":\"cm\",\"UnitType\":4},\"Ice\":{\"Value\":0.0,\"Unit\":\"mm\",\"UnitType\":3},\"HoursOfPrecipitation\":0.0,\"HoursOfRain\":0.0,\"HoursOfSnow\":0.0,\"HoursOfIce\":0.0,\"CloudCover\":17},\"Sources\":[\"AccuWeather\"],\"MobileLink\":\"http://m.accuweather.com/en/za/athlone/298833/daily-weather-forecast/298833?day=1&unit=c&lang=en-us\",\"Link\":\"http://www.accuweather.com/en/za/athlone/298833/daily-weather-forecast/298833?day=1&unit=c&lang=en-us\"},{\"Date\":\"2020-01-17T07:00:00+02:00\",\"EpochDate\":1579237200,\"Sun\":{\"Rise\":\"2020-01-17T05:11:00+02:00\",\"EpochRise\":1579230660,\"Set\":\"2020-01-17T19:00:00+02:00\",\"EpochSet\":1579280400},\"Moon\":{\"Rise\":\"2020-01-17T23:58:00+02:00\",\"EpochRise\":1579298280,\"Set\":\"2020-01-18T13:05:00+02:00\",\"EpochSet\":1579345500,\"Phase\":\"Last\",\"Age\":22},\"Temperature\":{\"Minimum\":{\"Value\":22.6,\"Unit\":\"C\",\"UnitType\":17},\"Maximum\":{\"Value\":32.7,\"Unit\":\"C\",\"UnitType\":17}},\"RealFeelTemperature\":{\"Minimum\":{\"Value\":23.5,\"Unit\":\"C\",\"UnitType\":17},\"Maximum\":{\"Value\":37.6,\"Unit\":\"C\",\"UnitType\":17}},\"RealFeelTemperatureShade\":{\"Minimum\":{\"Value\":23.5,\"Unit\":\"C\",\"UnitType\":17},\"Maximum\":{\"Value\":34.2,\"Unit\":\"C\",\"UnitType\":17}},\"HoursOfSun\":12.8,\"DegreeDaySummary\":{\"Heating\":{\"Value\":0.0,\"Unit\":\"C\",\"UnitType\":17},\"Cooling\":{\"Value\":10.0,\"Unit\":\"C\",\"UnitType\":17}},\"AirAndPollen\":[{\"Name\":\"AirQuality\",\"Value\":0,\"Category\":\"Good\",\"CategoryValue\":1,\"Type\":\"Ozone\"},{\"Name\":\"Grass\",\"Value\":0,\"Category\":\"Low\",\"CategoryValue\":1},{\"Name\":\"Mold\",\"Value\":0,\"Category\":\"Low\",\"CategoryValue\":1},{\"Name\":\"Ragweed\",\"Value\":0,\"Category\":\"Low\",\"CategoryValue\":1},{\"Name\":\"Tree\",\"Value\":0,\"Category\":\"Low\",\"CategoryValue\":1},{\"Name\":\"UVIndex\",\"Value\":12,\"Category\":\"Extreme\",\"CategoryValue\":5}],\"Day\":{\"Icon\":1,\"IconPhrase\":\"Sunny\",\"HasPrecipitation\":false,\"ShortPhrase\":\"Humid with plenty of sunshine\",\"LongPhrase\":\"Humid with plenty of sunshine\",\"PrecipitationProbability\":0,\"ThunderstormProbability\":0,\"RainProbability\":0,\"SnowProbability\":0,\"IceProbability\":0,\"Wind\":{\"Speed\":{\"Value\":16.7,\"Unit\":\"km/h\",\"UnitType\":7},\"Direction\":{\"Degrees\":50,\"Localized\":\"NE\",\"English\":\"NE\"}},\"WindGust\":{\"Speed\":{\"Value\":27.8,\"Unit\":\"km/h\",\"UnitType\":7},\"Direction\":{\"Degrees\":65,\"Localized\":\"ENE\",\"English\":\"ENE\"}},\"TotalLiquid\":{\"Value\":0.0,\"Unit\":\"mm\",\"UnitType\":3},\"Rain\":{\"Value\":0.0,\"Unit\":\"mm\",\"UnitType\":3},\"Snow\":{\"Value\":0.0,\"Unit\":\"cm\",\"UnitType\":4},\"Ice\":{\"Value\":0.0,\"Unit\":\"mm\",\"UnitType\":3},\"HoursOfPrecipitation\":0.0,\"HoursOfRain\":0.0,\"HoursOfSnow\":0.0,\"HoursOfIce\":0.0,\"CloudCover\":0},\"Night\":{\"Icon\":40,\"IconPhrase\":\"Mostly cloudy w/ showers\",\"HasPrecipitation\":true,\"PrecipitationType\":\"Rain\",\"PrecipitationIntensity\":\"Light\",\"ShortPhrase\":\"A shower in places late\",\"LongPhrase\":\"Humid with increasing clouds; a shower in spots late\",\"PrecipitationProbability\":43,\"ThunderstormProbability\":20,\"RainProbability\":43,\"SnowProbability\":0,\"IceProbability\":0,\"Wind\":{\"Speed\":{\"Value\":7.4,\"Unit\":\"km/h\",\"UnitType\":7},\"Direction\":{\"Degrees\":174,\"Localized\":\"S\",\"English\":\"S\"}},\"WindGust\":{\"Speed\":{\"Value\":16.7,\"Unit\":\"km/h\",\"UnitType\":7},\"Direction\":{\"Degrees\":68,\"Localized\":\"ENE\",\"English\":\"ENE\"}},\"TotalLiquid\":{\"Value\":0.5,\"Unit\":\"mm\",\"UnitType\":3},\"Rain\":{\"Value\":0.5,\"Unit\":\"mm\",\"UnitType\":3},\"Snow\":{\"Value\":0.0,\"Unit\":\"cm\",\"UnitType\":4},\"Ice\":{\"Value\":0.0,\"Unit\":\"mm\",\"UnitType\":3},\"HoursOfPrecipitation\":0.5,\"HoursOfRain\":0.5,\"HoursOfSnow\":0.0,\"HoursOfIce\":0.0,\"CloudCover\":52},\"Sources\":[\"AccuWeather\"],\"MobileLink\":\"http://m.accuweather.com/en/za/athlone/298833/daily-weather-forecast/298833?day=2&unit=c&lang=en-us\",\"Link\":\"http://www.accuweather.com/en/za/athlone/298833/daily-weather-forecast/298833?day=2&unit=c&lang=en-us\"},{\"Date\":\"2020-01-18T07:00:00+02:00\",\"EpochDate\":1579323600,\"Sun\":{\"Rise\":\"2020-01-18T05:12:00+02:00\",\"EpochRise\":1579317120,\"Set\":\"2020-01-18T19:00:00+02:00\",\"EpochSet\":1579366800},\"Moon\":{\"Rise\":null,\"EpochRise\":null,\"Set\":\"2020-01-18T13:05:00+02:00\",\"EpochSet\":1579345500,\"Phase\":\"WaningCrescent\",\"Age\":23},\"Temperature\":{\"Minimum\":{\"Value\":17.2,\"Unit\":\"C\",\"UnitType\":17},\"Maximum\":{\"Value\":25.7,\"Unit\":\"C\",\"UnitType\":17}},\"RealFeelTemperature\":{\"Minimum\":{\"Value\":16.5,\"Unit\":\"C\",\"UnitType\":17},\"Maximum\":{\"Value\":29.5,\"Unit\":\"C\",\"UnitType\":17}},\"RealFeelTemperatureShade\":{\"Minimum\":{\"Value\":16.5,\"Unit\":\"C\",\"UnitType\":17},\"Maximum\":{\"Value\":25.8,\"Unit\":\"C\",\"UnitType\":17}},\"HoursOfSun\":2.1,\"DegreeDaySummary\":{\"Heating\":{\"Value\":0.0,\"Unit\":\"C\",\"UnitType\":17},\"Cooling\":{\"Value\":3.0,\"Unit\":\"C\",\"UnitType\":17}},\"AirAndPollen\":[{\"Name\":\"AirQuality\",\"Value\":0,\"Category\":\"Good\",\"CategoryValue\":1,\"Type\":\"Ozone\"},{\"Name\":\"Grass\",\"Value\":0,\"Category\":\"Low\",\"CategoryValue\":1},{\"Name\":\"Mold\",\"Value\":0,\"Category\":\"Low\",\"CategoryValue\":1},{\"Name\":\"Ragweed\",\"Value\":0,\"Category\":\"Low\",\"CategoryValue\":1},{\"Name\":\"Tree\",\"Value\":0,\"Category\":\"Low\",\"CategoryValue\":1},{\"Name\":\"UVIndex\",\"Value\":9,\"Category\":\"Very High\",\"CategoryValue\":4}],\"Day\":{\"Icon\":6,\"IconPhrase\":\"Mostly cloudy\",\"HasPrecipitation\":true,\"PrecipitationType\":\"Rain\",\"PrecipitationIntensity\":\"Light\",\"ShortPhrase\":\"A t-storm around in the a.m.\",\"LongPhrase\":\"A thunderstorm in spots in the morning; otherwise, mostly cloudy and not as warm\",\"PrecipitationProbability\":40,\"ThunderstormProbability\":60,\"RainProbability\":40,\"SnowProbability\":0,\"IceProbability\":0,\"Wind\":{\"Speed\":{\"Value\":20.4,\"Unit\":\"km/h\",\"UnitType\":7},\"Direction\":{\"Degrees\":191,\"Localized\":\"S\",\"English\":\"S\"}},\"WindGust\":{\"Speed\":{\"Value\":29.6,\"Unit\":\"km/h\",\"UnitType\":7},\"Direction\":{\"Degrees\":185,\"Localized\":\"S\",\"English\":\"S\"}},\"TotalLiquid\":{\"Value\":1.0,\"Unit\":\"mm\",\"UnitType\":3},\"Rain\":{\"Value\":1.0,\"Unit\":\"mm\",\"UnitType\":3},\"Snow\":{\"Value\":0.0,\"Unit\":\"cm\",\"UnitType\":4},\"Ice\":{\"Value\":0.0,\"Unit\":\"mm\",\"UnitType\":3},\"HoursOfPrecipitation\":0.5,\"HoursOfRain\":0.5,\"HoursOfSnow\":0.0,\"HoursOfIce\":0.0,\"CloudCover\":89},\"Night\":{\"Icon\":12,\"IconPhrase\":\"Showers\",\"HasPrecipitation\":true,\"PrecipitationType\":\"Rain\",\"PrecipitationIntensity\":\"Light\",\"ShortPhrase\":\"A little rain early; cloudy\",\"LongPhrase\":\"A little rain in the evening; otherwise, considerable cloudiness\",\"PrecipitationProbability\":57,\"ThunderstormProbability\":0,\"RainProbability\":57,\"SnowProbability\":0,\"IceProbability\":0,\"Wind\":{\"Speed\":{\"Value\":11.1,\"Unit\":\"km/h\",\"UnitType\":7},\"Direction\":{\"Degrees\":207,\"Localized\":\"SSW\",\"English\":\"SSW\"}},\"WindGust\":{\"Speed\":{\"Value\":24.1,\"Unit\":\"km/h\",\"UnitType\":7},\"Direction\":{\"Degrees\":194,\"Localized\":\"SSW\",\"English\":\"SSW\"}},\"TotalLiquid\":{\"Value\":0.9,\"Unit\":\"mm\",\"UnitType\":3},\"Rain\":{\"Value\":0.9,\"Unit\":\"mm\",\"UnitType\":3},\"Snow\":{\"Value\":0.0,\"Unit\":\"cm\",\"UnitType\":4},\"Ice\":{\"Value\":0.0,\"Unit\":\"mm\",\"UnitType\":3},\"HoursOfPrecipitation\":1.0,\"HoursOfRain\":1.0,\"HoursOfSnow\":0.0,\"HoursOfIce\":0.0,\"CloudCover\":98},\"Sources\":[\"AccuWeather\"],\"MobileLink\":\"http://m.accuweather.com/en/za/athlone/298833/daily-weather-forecast/298833?day=3&unit=c&lang=en-us\",\"Link\":\"http://www.accuweather.com/en/za/athlone/298833/daily-weather-forecast/298833?day=3&unit=c&lang=en-us\"},{\"Date\":\"2020-01-19T07:00:00+02:00\",\"EpochDate\":1579410000,\"Sun\":{\"Rise\":\"2020-01-19T05:13:00+02:00\",\"EpochRise\":1579403580,\"Set\":\"2020-01-19T19:00:00+02:00\",\"EpochSet\":1579453200},\"Moon\":{\"Rise\":\"2020-01-19T00:36:00+02:00\",\"EpochRise\":1579386960,\"Set\":\"2020-01-19T14:08:00+02:00\",\"EpochSet\":1579435680,\"Phase\":\"WaningCrescent\",\"Age\":24},\"Temperature\":{\"Minimum\":{\"Value\":16.3,\"Unit\":\"C\",\"UnitType\":17},\"Maximum\":{\"Value\":18.9,\"Unit\":\"C\",\"UnitType\":17}},\"RealFeelTemperature\":{\"Minimum\":{\"Value\":15.0,\"Unit\":\"C\",\"UnitType\":17},\"Maximum\":{\"Value\":19.7,\"Unit\":\"C\",\"UnitType\":17}},\"RealFeelTemperatureShade\":{\"Minimum\":{\"Value\":15.0,\"Unit\":\"C\",\"UnitType\":17},\"Maximum\":{\"Value\":17.7,\"Unit\":\"C\",\"UnitType\":17}},\"HoursOfSun\":1.1,\"DegreeDaySummary\":{\"Heating\":{\"Value\":0.0,\"Unit\":\"C\",\"UnitType\":17},\"Cooling\":{\"Value\":0.0,\"Unit\":\"C\",\"UnitType\":17}},\"AirAndPollen\":[{\"Name\":\"AirQuality\",\"Value\":0,\"Category\":\"Good\",\"CategoryValue\":1,\"Type\":\"Ozone\"},{\"Name\":\"Grass\",\"Value\":0,\"Category\":\"Low\",\"CategoryValue\":1},{\"Name\":\"Mold\",\"Value\":0,\"Category\":\"Low\",\"CategoryValue\":1},{\"Name\":\"Ragweed\",\"Value\":0,\"Category\":\"Low\",\"CategoryValue\":1},{\"Name\":\"Tree\",\"Value\":0,\"Category\":\"Low\",\"CategoryValue\":1},{\"Name\":\"UVIndex\",\"Value\":3,\"Category\":\"Moderate\",\"CategoryValue\":2}],\"Day\":{\"Icon\":7,\"IconPhrase\":\"Cloudy\",\"HasPrecipitation\":true,\"PrecipitationType\":\"Rain\",\"PrecipitationIntensity\":\"Light\",\"ShortPhrase\":\"Cloudy, a shower in the p.m.\",\"LongPhrase\":\"A thick cloud cover with a passing shower in the afternoon\",\"PrecipitationProbability\":56,\"ThunderstormProbability\":20,\"RainProbability\":56,\"SnowProbability\":0,\"IceProbability\":0,\"Wind\":{\"Speed\":{\"Value\":13.0,\"Unit\":\"km/h\",\"UnitType\":7},\"Direction\":{\"Degrees\":191,\"Localized\":\"S\",\"English\":\"S\"}},\"WindGust\":{\"Speed\":{\"Value\":20.4,\"Unit\":\"km/h\",\"UnitType\":7},\"Direction\":{\"Degrees\":183,\"Localized\":\"S\",\"English\":\"S\"}},\"TotalLiquid\":{\"Value\":0.9,\"Unit\":\"mm\",\"UnitType\":3},\"Rain\":{\"Value\":0.9,\"Unit\":\"mm\",\"UnitType\":3},\"Snow\":{\"Value\":0.0,\"Unit\":\"cm\",\"UnitType\":4},\"Ice\":{\"Value\":0.0,\"Unit\":\"mm\",\"UnitType\":3},\"HoursOfPrecipitation\":0.5,\"HoursOfRain\":0.5,\"HoursOfSnow\":0.0,\"HoursOfIce\":0.0,\"CloudCover\":97},\"Night\":{\"Icon\":12,\"IconPhrase\":\"Showers\",\"HasPrecipitation\":true,\"PrecipitationType\":\"Rain\",\"PrecipitationIntensity\":\"Light\",\"ShortPhrase\":\"Cloudy with a little rain\",\"LongPhrase\":\"Considerable cloudiness with a little rain\",\"PrecipitationProbability\":60,\"ThunderstormProbability\":0,\"RainProbability\":60,\"SnowProbability\":0,\"IceProbability\":0,\"Wind\":{\"Speed\":{\"Value\":7.4,\"Unit\":\"km/h\",\"UnitType\":7},\"Direction\":{\"Degrees\":201,\"Localized\":\"SSW\",\"English\":\"SSW\"}},\"WindGust\":{\"Speed\":{\"Value\":18.5,\"Unit\":\"km/h\",\"UnitType\":7},\"Direction\":{\"Degrees\":192,\"Localized\":\"SSW\",\"English\":\"SSW\"}},\"TotalLiquid\":{\"Value\":3.4,\"Unit\":\"mm\",\"UnitType\":3},\"Rain\":{\"Value\":3.4,\"Unit\":\"mm\",\"UnitType\":3},\"Snow\":{\"Value\":0.0,\"Unit\":\"cm\",\"UnitType\":4},\"Ice\":{\"Value\":0.0,\"Unit\":\"mm\",\"UnitType\":3},\"HoursOfPrecipitation\":3.0,\"HoursOfRain\":3.0,\"HoursOfSnow\":0.0,\"HoursOfIce\":0.0,\"CloudCover\":97},\"Sources\":[\"AccuWeather\"],\"MobileLink\":\"http://m.accuweather.com/en/za/athlone/298833/daily-weather-forecast/298833?day=4&unit=c&lang=en-us\",\"Link\":\"http://www.accuweather.com/en/za/athlone/298833/daily-weather-forecast/298833?day=4&unit=c&lang=en-us\"},{\"Date\":\"2020-01-20T07:00:00+02:00\",\"EpochDate\":1579496400,\"Sun\":{\"Rise\":\"2020-01-20T05:14:00+02:00\",\"EpochRise\":1579490040,\"Set\":\"2020-01-20T19:00:00+02:00\",\"EpochSet\":1579539600},\"Moon\":{\"Rise\":\"2020-01-20T01:16:00+02:00\",\"EpochRise\":1579475760,\"Set\":\"2020-01-20T15:10:00+02:00\",\"EpochSet\":1579525800,\"Phase\":\"WaningCrescent\",\"Age\":25},\"Temperature\":{\"Minimum\":{\"Value\":14.9,\"Unit\":\"C\",\"UnitType\":17},\"Maximum\":{\"Value\":19.0,\"Unit\":\"C\",\"UnitType\":17}},\"RealFeelTemperature\":{\"Minimum\":{\"Value\":15.6,\"Unit\":\"C\",\"UnitType\":17},\"Maximum\":{\"Value\":20.3,\"Unit\":\"C\",\"UnitType\":17}},\"RealFeelTemperatureShade\":{\"Minimum\":{\"Value\":15.6,\"Unit\":\"C\",\"UnitType\":17},\"Maximum\":{\"Value\":18.8,\"Unit\":\"C\",\"UnitType\":17}},\"HoursOfSun\":1.8,\"DegreeDaySummary\":{\"Heating\":{\"Value\":1.0,\"Unit\":\"C\",\"UnitType\":17},\"Cooling\":{\"Value\":0.0,\"Unit\":\"C\",\"UnitType\":17}},\"AirAndPollen\":[{\"Name\":\"AirQuality\",\"Value\":0,\"Category\":\"Good\",\"CategoryValue\":1,\"Type\":\"Ozone\"},{\"Name\":\"Grass\",\"Value\":0,\"Category\":\"Low\",\"CategoryValue\":1},{\"Name\":\"Mold\",\"Value\":0,\"Category\":\"Low\",\"CategoryValue\":1},{\"Name\":\"Ragweed\",\"Value\":0,\"Category\":\"Low\",\"CategoryValue\":1},{\"Name\":\"Tree\",\"Value\":0,\"Category\":\"Low\",\"CategoryValue\":1},{\"Name\":\"UVIndex\",\"Value\":3,\"Category\":\"Moderate\",\"CategoryValue\":2}],\"Day\":{\"Icon\":12,\"IconPhrase\":\"Showers\",\"HasPrecipitation\":true,\"PrecipitationType\":\"Rain\",\"PrecipitationIntensity\":\"Light\",\"ShortPhrase\":\"A little morning rain; cloudy\",\"LongPhrase\":\"A little rain in the morning; otherwise, considerable cloudiness\",\"PrecipitationProbability\":55,\"ThunderstormProbability\":0,\"RainProbability\":55,\"SnowProbability\":0,\"IceProbability\":0,\"Wind\":{\"Speed\":{\"Value\":9.3,\"Unit\":\"km/h\",\"UnitType\":7},\"Direction\":{\"Degrees\":147,\"Localized\":\"SSE\",\"English\":\"SSE\"}},\"WindGust\":{\"Speed\":{\"Value\":16.7,\"Unit\":\"km/h\",\"UnitType\":7},\"Direction\":{\"Degrees\":125,\"Localized\":\"SE\",\"English\":\"SE\"}},\"TotalLiquid\":{\"Value\":0.5,\"Unit\":\"mm\",\"UnitType\":3},\"Rain\":{\"Value\":0.5,\"Unit\":\"mm\",\"UnitType\":3},\"Snow\":{\"Value\":0.0,\"Unit\":\"cm\",\"UnitType\":4},\"Ice\":{\"Value\":0.0,\"Unit\":\"mm\",\"UnitType\":3},\"HoursOfPrecipitation\":0.5,\"HoursOfRain\":0.5,\"HoursOfSnow\":0.0,\"HoursOfIce\":0.0,\"CloudCover\":95},\"Night\":{\"Icon\":7,\"IconPhrase\":\"Cloudy\",\"HasPrecipitation\":false,\"ShortPhrase\":\"Mainly cloudy\",\"LongPhrase\":\"Low clouds, then perhaps some clearing\",\"PrecipitationProbability\":25,\"ThunderstormProbability\":0,\"RainProbability\":25,\"SnowProbability\":0,\"IceProbability\":0,\"Wind\":{\"Speed\":{\"Value\":3.7,\"Unit\":\"km/h\",\"UnitType\":7},\"Direction\":{\"Degrees\":21,\"Localized\":\"NNE\",\"English\":\"NNE\"}},\"WindGust\":{\"Speed\":{\"Value\":9.3,\"Unit\":\"km/h\",\"UnitType\":7},\"Direction\":{\"Degrees\":342,\"Localized\":\"NNW\",\"English\":\"NNW\"}},\"TotalLiquid\":{\"Value\":0.0,\"Unit\":\"mm\",\"UnitType\":3},\"Rain\":{\"Value\":0.0,\"Unit\":\"mm\",\"UnitType\":3},\"Snow\":{\"Value\":0.0,\"Unit\":\"cm\",\"UnitType\":4},\"Ice\":{\"Value\":0.0,\"Unit\":\"mm\",\"UnitType\":3},\"HoursOfPrecipitation\":0.0,\"HoursOfRain\":0.0,\"HoursOfSnow\":0.0,\"HoursOfIce\":0.0,\"CloudCover\":87},\"Sources\":[\"AccuWeather\"],\"MobileLink\":\"http://m.accuweather.com/en/za/athlone/298833/daily-weather-forecast/298833?day=5&unit=c&lang=en-us\",\"Link\":\"http://www.accuweather.com/en/za/athlone/298833/daily-weather-forecast/298833?day=5&unit=c&lang=en-us\"}]}";

                if (CurrentConditionsResult != null) {
                    try {
                        JSONObject temp = new JSONObject(CurrentConditionsResult);
                        JSONArray jsonArrayresult = temp.getJSONArray("DailyForecasts");
                        JSONObject jsonObject;
                        for (int i = 0; i <5; i++){
                            jsonObject = jsonArrayresult.getJSONObject(i);
                            if (i == 0){
                                String temp1 = jsonObject.getJSONObject("Sun").getString("Rise");
                                String [] arr1 = temp1.split("T");
                                String [] arr2 = arr1[1].split("\\+");
                                weatherCurrent.setSunrise(Time.valueOf(arr2[0]));
                                temp1 = jsonObject.getJSONObject("Sun").getString("Set");
                                arr1 = temp1.split("T");
                                arr2 = arr1[1].split("\\+");
                                weatherCurrent.setSunset(Time.valueOf(arr2[0]));
                            }
                            forecast.add(new Weather(
                                    jsonObject.getString("Date"),
                                    jsonObject.getJSONObject("Temperature").getJSONObject("Maximum").getDouble("Value"),
                                    jsonObject.getJSONObject("Temperature").getJSONObject("Minimum").getDouble("Value"),
                                    jsonObject.getJSONObject("Day").getInt("PrecipitationProbability"),
                                    jsonObject.getJSONObject("Day").getInt("Icon")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            return "";
        }
        @Override
        protected void onPostExecute(String aVoid) {
            _CurrentTemp.setText(MessageFormat.format("{0}°C", weatherCurrent.getCurrentTemp()));
            _HighTemp.setText(MessageFormat.format("High {0}°C", weatherCurrent.getHighTemp()));
            _LowTemp.setText(MessageFormat.format("Low {0}°C", weatherCurrent.getLowTemp()));
            _RealFeel.setText(MessageFormat.format("Real Feel {0}°C", weatherCurrent.getRealFeelTemp()));
            _Precipitation.setText(MessageFormat.format("{0}% Chance of Rain", weatherCurrent.getPrecipitation()));
            _Humidity.setText(MessageFormat.format("Humidity - {0}%", weatherCurrent.getHumidity()));
            _DewPoint.setText(MessageFormat.format("Dew Point - {0}°C", weatherCurrent.getDewPoint()));
            _Pressure.setText(MessageFormat.format("Pressure - {0}mBar", weatherCurrent.getPressure()));
            _UVIndex.setText(MessageFormat.format("UV Index - {0}, {1}", weatherCurrent.getUVIndexText(), weatherCurrent.getUVIndexNumber()));
            _Visibility.setText(MessageFormat.format("Visibility - {0}Km", weatherCurrent.getVisibility()));
            _WindSpeed.setText(String.valueOf(weatherCurrent.getWindSpeed()));
            _WindDirectionDegrees.setRotation(weatherCurrent.getWindDirectionDegrees() -270);
            _WindDirectionText.setText(MessageFormat.format("From {0}", weatherCurrent.getWindDirectionText()));
            _Sunrise.setText(String.valueOf(weatherCurrent.getSunrise()).substring(0,5));
            _Sunset.setText(String.valueOf(weatherCurrent.getSunset()).substring(0,5));

            if (sky > 0 && sky < 6) {
                _Skies.setImageResource(R.drawable.sun);
            } else if (sky > 5 && sky < 12) {
                _Skies.setImageResource(R.drawable.overcast);
            } else if ( sky > 11 && sky < 19) {
                _Skies.setImageResource(R.drawable.rain);
            }

            forecast.remove(0);
            RecyclerViewAdapter adapter = new RecyclerViewAdapter(ScrollingActivity.this, forecast);
            recyclerView.setAdapter(adapter);

            blurScreen(40, 0);

            super.onPostExecute(aVoid);
        }
    }


    public void blurScreen(int start, int end){
        long animationDuration = 1000;
        ValueAnimator animator = ValueAnimator.ofFloat((float) start, (float) end);
        animator.setDuration(animationDuration);
        animator.addUpdateListener(valueAnimator -> {
            float animatedValue = (float) valueAnimator.getAnimatedValue();
            blurBottom.setBlurRadius(animatedValue);
        });
        animator.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
