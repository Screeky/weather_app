package ru.mephi.weatherapp.data

data class HourForecast(
    val time: String,
    val weatherPic: String,
    val temperature: Int,
    val chanceOfPrecipitation: String
)