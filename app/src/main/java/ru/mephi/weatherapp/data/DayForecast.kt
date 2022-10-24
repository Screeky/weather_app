package ru.mephi.weatherapp.data

data class DayForecast (
    val date: String,
    val dayWeatherPic: String,
    val nightWeatherPic: String,
    val maxTemperature: String,
    val minTemperature: String,
    val chanceOfRain: String,
    val sunrise: String,
    val sunset: String
        )