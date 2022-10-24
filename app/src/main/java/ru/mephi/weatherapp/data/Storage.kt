package ru.mephi.weatherapp.data

import android.app.Application
import android.content.Context
import ru.mephi.weatherapp.R

object Storage {
    fun getInfo(context: Context, isDay: Int): Map<Int, WeatherConditions> {
        val link: String
        if (isDay == 1)
            link = "https://cdn.weatherapi.com/weather/64x64/day/"
        else
            link = "https://cdn.weatherapi.com/weather/64x64/night/"
        return mapOf(
            1000 to WeatherConditions(context.getString(R.string.clear), link+"113.png"),
            1003 to WeatherConditions(context.getString(R.string.partly_cloudy), link+"116.png"),
            1006 to WeatherConditions(context.getString(R.string.cloudy), link+"119.png"),
            1009 to WeatherConditions(context.getString(R.string.overcast), link+"122.png"),
            1030 to WeatherConditions(context.getString(R.string.mist), link+"143.png"),
            1063 to WeatherConditions(context.getString(R.string.patchy_rain_possible), link+"176.png"),
            1066 to WeatherConditions(context.getString(R.string.patchy_snow_possible), link+"179.png"),
            1069 to WeatherConditions(context.getString(R.string.patchy_sleet_possible), link+"182.png"),
            1072 to WeatherConditions(context.getString(R.string.patchy_freezing_drizzle_possible), link+"185.png"),
            1087 to WeatherConditions(context.getString(R.string.thundery_outbreaks_possible), link+"200.png"),
            1114 to WeatherConditions(context.getString(R.string.blowing_snow), link+"227.png"),
            1117 to WeatherConditions(context.getString(R.string.blizzard), link+"230.png"),
            1135 to WeatherConditions(context.getString(R.string.fog), link+"248.png"),
            1147 to WeatherConditions(context.getString(R.string.freezing_fog), link+"260.png"),
            1150 to WeatherConditions(context.getString(R.string.patchy_light_drizzle), link+"263.png"),
            1153 to WeatherConditions(context.getString(R.string.light_drizzle), link+"266.png"),
            1168 to WeatherConditions(context.getString(R.string.freezing_drizzle), link+"281.png"),
            1171 to WeatherConditions(context.getString(R.string.heavy_freezing_drizzle), link+"284.png"),
            1180 to WeatherConditions(context.getString(R.string.patchy_light_rain), link+"293.png"),
            1183 to WeatherConditions(context.getString(R.string.light_rain), link+"296.png"),
            1186 to WeatherConditions(context.getString(R.string.moderate_rain_at_times), link+"299.png"),
            1189 to WeatherConditions(context.getString(R.string.moderate_rain), link+"302.png"),
            1192 to WeatherConditions(context.getString(R.string.heavy_rain_at_times), link+"305.png"),
            1195 to WeatherConditions(context.getString(R.string.heavy_rain), link+"308.png"),
            1198 to WeatherConditions(context.getString(R.string.light_freezing_rain), link+"311.png"),
            1201 to WeatherConditions(context.getString(R.string.moderate_or_heavy_freezing_rain), link+"314.png"),
            1204 to WeatherConditions(context.getString(R.string.light_sleet), link+"317.png"),
            1207 to WeatherConditions(context.getString(R.string.moderate_or_heavy_sleet), link+"320.png"),
            1210 to WeatherConditions(context.getString(R.string.patchy_light_snow), link+"323.png"),
            1213 to WeatherConditions(context.getString(R.string.light_snow), link+"326.png"),
            1216 to WeatherConditions(context.getString(R.string.patchy_moderate_snow), link+"329.png"),
            1219 to WeatherConditions(context.getString(R.string.moderate_snow), link+"332.png"),
            1222 to WeatherConditions(context.getString(R.string.patchy_heavy_snow), link+"335.png"),
            1225 to WeatherConditions(context.getString(R.string.heavy_snow), link+"338.png"),
            1237 to WeatherConditions(context.getString(R.string.ice_pellets), link+"350.png"),
            1240 to WeatherConditions(context.getString(R.string.light_rain_shower), link+"353.png"),
            1243 to WeatherConditions(context.getString(R.string.moderate_or_heavy_rain_shower), link+"356.png"),
            1246 to WeatherConditions(context.getString(R.string.torrential_rain_shower), link+"359.png"),
            1249 to WeatherConditions(context.getString(R.string.light_sleet_showers), link+"362.png"),
            1252 to WeatherConditions(context.getString(R.string.moderate_or_heavy_sleet_showers), link+"365.png"),
            1255 to WeatherConditions(context.getString(R.string.light_snow_showers), link+"368.png"),
            1258 to WeatherConditions(context.getString(R.string.moderate_or_heavy_sleet_showers), link+"371.png"),
            1261 to WeatherConditions(context.getString(R.string.light_showers_of_ice_pellets), link+"374.png"),
            1264 to WeatherConditions(context.getString(R.string.moderate_or_heavy_showers_of_ice_pellets), link+"377.png"),
            1273 to WeatherConditions(context.getString(R.string.patchy_light_rain_with_thunder), link+"386.png"),
            1276 to WeatherConditions(context.getString(R.string.moderate_or_heavy_rain_with_thunder), link+"389.png"),
            1279 to WeatherConditions(context.getString(R.string.patchy_light_snow_with_thunder), link+"392.png"),
            1282 to WeatherConditions(context.getString(R.string.moderate_or_heavy_snow_with_thunder), link+"395.png")
        )

    }
}