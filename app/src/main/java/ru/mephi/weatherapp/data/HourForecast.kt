package ru.mephi.weatherapp.data

data class HourForecast(
    val time: String,
    val weatherPic: String,
    val temperature: String,
    val chanceOfRain: String
)