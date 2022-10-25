package ru.mephi.weatherapp.data

data class NowForecast(
    val temperature: String,
    val isDay: Int,
    val conditionCode: Int,
    val currentWeatherPic: String,
    val feelsTemperature: String,
    val indexUV: String,
    val windSpeed: String,
    val humidity: String
    )