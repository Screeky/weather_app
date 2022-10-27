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
import ru.mephi.weatherapp.data.HourForecast

class HourRVAdapter(context: Context, private val hours: List<HourForecast>) :
    RecyclerView.Adapter<HourRVAdapter.HourViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourViewHolder {
        return HourViewHolder(inflater.inflate(R.layout.hour_rv_item, parent, false))
    }

    override fun onBindViewHolder(holder: HourViewHolder, position: Int) {
        val hourForecast = hours[position]
        holder.bind(hourForecast)
    }

    override fun getItemCount(): Int = hours.size

    inner class HourViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val time: TextView = itemView.findViewById(R.id.time)
        private val weatherPic: ImageView = itemView.findViewById(R.id.weather_pic)
        private val temperature: TextView = itemView.findViewById(R.id.temperature)
        private val chanceOfPrecipitation: TextView = itemView.findViewById(R.id.chance_of_precip)
        private val precipitationPic: ImageView = itemView.findViewById(R.id.precip_pic)

        @SuppressLint("SetTextI18n")
        fun bind(hourForecast: HourForecast) {
            time.text = hourForecast.time
            Picasso.get().load(hourForecast.weatherPic).into(weatherPic)
            temperature.text = hourForecast.temperature.toString() + "°"
            chanceOfPrecipitation.text = hourForecast.chanceOfPrecipitation
            if (hourForecast.temperature < 0)
                precipitationPic.setImageResource(R.drawable.snowflake)
            else
                precipitationPic.setImageResource(R.drawable.drop)


        }
    }
}