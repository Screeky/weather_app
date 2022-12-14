package ru.mephi.weatherapp.recyclerViewAdapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ru.mephi.weatherapp.R
import ru.mephi.weatherapp.data.DayForecast


class DayRVAdapter(context: Context, private val hours: List<DayForecast>) :
    RecyclerView.Adapter<DayRVAdapter.DayViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        return DayViewHolder(inflater.inflate(R.layout.day_rv_item, parent, false))
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val hourForecast = hours[position]
        holder.bind(hourForecast)
    }

    override fun getItemCount(): Int = hours.size

    inner class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val date: TextView = itemView.findViewById(R.id.day_of_week)
        private val dayWeatherPic: ImageView = itemView.findViewById(R.id.day_weather_pic)
        private val nightWeatherPic: ImageView = itemView.findViewById(R.id.night_weather_pic)
        private val maxTemperature: TextView = itemView.findViewById(R.id.max_temperature)
        private val minTemperature: TextView = itemView.findViewById(R.id.min_temperature)
        private val chanceOfPrecipitation: TextView = itemView.findViewById(R.id.chance_of_precip)
        private val precipitationPic: ImageView = itemView.findViewById(R.id.precip_pic)

        @SuppressLint("SetTextI18n")
        fun bind(dayForecast: DayForecast) {
            date.text = dayForecast.date
            Picasso.get().load(dayForecast.dayWeatherPic).into(dayWeatherPic)
            Picasso.get().load(dayForecast.nightWeatherPic).into(nightWeatherPic)
            maxTemperature.text = dayForecast.maxTemperature.toString() + "??"
            minTemperature.text = dayForecast.minTemperature.toString() + "??"
            chanceOfPrecipitation.text = dayForecast.chanceOfPrecipitation
            if (dayForecast.minTemperature < 0)
                precipitationPic.setImageResource(R.drawable.snowflake)
            else
                precipitationPic.setImageResource(R.drawable.drop)
        }
    }
}