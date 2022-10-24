package ru.mephi.weatherapp

import androidx.lifecycle.ViewModel
import ru.mephi.weatherapp.data.DayForecast
import ru.mephi.weatherapp.data.HourForecast
import ru.mephi.weatherapp.data.NowForecast

class WeatherViewModel: ViewModel() {
    lateinit var nowForecast: NowForecast
    lateinit var hourForecast: MutableList<HourForecast>
    lateinit var dayForecast: MutableList<DayForecast>
    var city = ""
    var place = ""
    var newCity = ""
}