package ru.mephi.weatherapp.data

data class DayForecast (
    val date: String,
    val dayWeatherPic: String,
    val nightWeatherPic: String,
    val maxTemperature: Int,
    val minTemperature: Int,
    val chanceOfPrecipitation: String,
    val sunrise: String,
    val sunset: String
        )