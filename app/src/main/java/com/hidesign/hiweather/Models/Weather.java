package com.hidesign.hiweather.Models;

import java.sql.Time;

public class Weather {
    private String WindDirectionText, UVIndexText, Date;
    private Double CurrentTemp, RealFeelTemp, LowTemp, HighTemp, WindSpeed, DewPoint, Visibility;
    private Integer Humidity, UVIndexNumber, Pressure, Precipitation, WindDirectionDegrees, Icon;
    private Time Sunrise, Sunset;

    public Weather (){

    }
    public Weather (String date, double high, double low, int rain, int icon){
        Date = date;
        HighTemp = high;
        LowTemp = low;
        Precipitation = rain;
        Icon = icon;
    }

    public Weather(String Date, String windDirectionText, String UVIndexText, Double currentTemp, Double realFealTemp, Double lowTemp, Double highTemp, Double windSpeed, Double dewPoint, Double visibility, Integer humidity, Integer UVIndexNumber, Integer pressure, Integer precipitation, Integer windDirectionDegrees) {
        this.Date = Date;
        WindDirectionText = windDirectionText;
        this.UVIndexText = UVIndexText;
        CurrentTemp = currentTemp;
        RealFeelTemp = realFealTemp;
        LowTemp = lowTemp;
        HighTemp = highTemp;
        WindSpeed = windSpeed;
        DewPoint = dewPoint;
        Visibility = visibility;
        Humidity = humidity;
        this.UVIndexNumber = UVIndexNumber;
        Pressure = pressure;
        Precipitation = precipitation;
        WindDirectionDegrees = windDirectionDegrees;
    }

    public String getDate() {
        return Date;
    }
    public String getWindDirectionText() {
        return WindDirectionText;
    }
    public String getUVIndexText() {
        return UVIndexText;
    }
    public Double getCurrentTemp() {
        return CurrentTemp;
    }
    public Double getRealFeelTemp() {
        return RealFeelTemp;
    }
    public Double getLowTemp() {
        return LowTemp;
    }
    public Double getHighTemp() {
        return HighTemp;
    }
    public Double getWindSpeed() {
        return WindSpeed;
    }
    public Double getDewPoint() {
        return DewPoint;
    }
    public Double getVisibility() {
        return Visibility;
    }
    public Integer getHumidity() {
        return Humidity;
    }
    public Integer getUVIndexNumber() {
        return UVIndexNumber;
    }
    public Integer getPressure() {
        return Pressure;
    }
    public Integer getPrecipitation() {
        return Precipitation;
    }
    public Integer getWindDirectionDegrees() {
        return WindDirectionDegrees;
    }
    public Time getSunrise() {
        return Sunrise;
    }
    public void setSunrise(Time sunrise) {
        Sunrise = sunrise;
    }
    public Time getSunset() {
        return Sunset;
    }
    public void setSunset(Time sunset) {
        Sunset = sunset;
    }
    public int getIcon() {
        return Icon;
    }
}
