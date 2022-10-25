package ru.mephi.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.*
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject
import ru.mephi.weatherapp.data.DayForecast
import ru.mephi.weatherapp.data.HourForecast
import ru.mephi.weatherapp.data.NowForecast
import ru.mephi.weatherapp.data.Storage
import ru.mephi.weatherapp.recyclerViewAdapters.DayRVAdapter
import ru.mephi.weatherapp.recyclerViewAdapters.HourRVAdapter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class WeatherFragment : Fragment() {

    private lateinit var progressBar: ProgressBar
    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var parentCL: ConstraintLayout
    private lateinit var scrollView: NestedScrollView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var temperature: TextView
    private lateinit var city: TextView
    private lateinit var maxMinTemperature: TextView
    private lateinit var feelsTemperature: TextView
    private lateinit var connectionTV: TextView
    private lateinit var sunriseTV: TextView
    private lateinit var sunsetTV: TextView
    private lateinit var uvIndexTV: TextView
    private lateinit var windSpeedTV: TextView
    private lateinit var humidityTV: TextView

    private lateinit var inputCity: EditText


    private lateinit var search: ImageView
    private lateinit var currentWeatherPicture: ImageView


    private lateinit var hourRecyclerView: RecyclerView
    private lateinit var dayRecyclerView: RecyclerView
    private lateinit var hourRVAdapter: HourRVAdapter
    private lateinit var dayRVAdapter: DayRVAdapter

    private var cityName = ""

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (it.containsValue(false)) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
                checkPermissions()
            } else {
                defineCity()
            }
        }

    private val weatherViewModel: WeatherViewModel by lazy {
        ViewModelProvider(this, defaultViewModelProviderFactory)[WeatherViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.weather_fragment, container, false)
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        scrollView = view.findViewById(R.id.scroll_view)
        connectionTV = view.findViewById(R.id.internet_connection)
        progressBar = view.findViewById(R.id.progress_bar)
        constraintLayout = view.findViewById(R.id.constraint_layout)
        parentCL = view.findViewById(R.id.parent_c_l)
        temperature = view.findViewById(R.id.temperature)
        currentWeatherPicture = view.findViewById(R.id.current_weather_pic)
        city = view.findViewById(R.id.city)
        search = view.findViewById(R.id.search)
        inputCity = view.findViewById(R.id.input_city)
        maxMinTemperature = view.findViewById(R.id.max_min_temperature)
        feelsTemperature = view.findViewById(R.id.feels_temperature)
        hourRecyclerView = view.findViewById(R.id.hourly_forecast)
        dayRecyclerView = view.findViewById(R.id.daily_forecast)
        sunriseTV = view.findViewById(R.id.sunrise_time)
        sunsetTV = view.findViewById(R.id.sunset_time)
        uvIndexTV = view.findViewById(R.id.uv_index)
        windSpeedTV = view.findViewById(R.id.wind_speed)
        humidityTV = view.findViewById(R.id.humidity)
        dynamicMargin(parentCL)
        constraintLayout.visibility = View.GONE
        if (!checkNetworkState())
            connectionTV.visibility = View.VISIBLE
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (weatherViewModel.city != "") {
            updateUI(weatherViewModel)
        } else {
            if (checkNetworkState())
            checkPermissions()
        }

    }

    override fun onStart() {
        super.onStart()

        swipeRefreshLayout.setOnRefreshListener {
            if (checkNetworkState()) {
                connectionTV.visibility = View.GONE
                if (weatherViewModel.city != "") {
                    getWeatherData(weatherViewModel.city)
                } else {
                    checkPermissions()
                }
            } else {
                connectionTV.visibility = View.VISIBLE
                swipeRefreshLayout.isRefreshing = false
                Toast.makeText(
                    requireContext(),
                    getString(R.string.network_status),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val cityWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                sequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                weatherViewModel.newCity = sequence.toString()
            }

            override fun afterTextChanged(sequence: Editable?) {}
        }

        inputCity.addTextChangedListener(cityWatcher)

        inputCity.setOnKeyListener { view, keyCode, keyEvent ->
            if ((keyEvent.action == KeyEvent.ACTION_DOWN) &&
                (keyCode == KeyEvent.KEYCODE_ENTER)
            ) {
                if (!checkNetworkState()) {
                    connectionTV.visibility = View.VISIBLE
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.network_status),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else {
                    connectionTV.visibility = View.GONE
                    if (weatherViewModel.newCity != "") {
                        getWeatherData(weatherViewModel.newCity)
                        weatherViewModel.newCity = ""
                    }
                }
                true
            } else
                false
        }

        search.setOnClickListener {
            if (!checkNetworkState()) {
                connectionTV.visibility = View.VISIBLE
                Toast.makeText(
                    requireContext(),
                    getString(R.string.network_status),
                    Toast.LENGTH_SHORT
                ).show()
            }
            else {
                connectionTV.visibility = View.GONE
                if (weatherViewModel.newCity != "") {
                    getWeatherData(weatherViewModel.newCity)
                    weatherViewModel.newCity = ""
                }
            }

        }
    }


    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE
        )
        val p1 = ContextCompat.checkSelfPermission(
            requireContext(), permissions[0]
        ) == PackageManager.PERMISSION_GRANTED
        val p2 = ContextCompat.checkSelfPermission(
            requireContext(), permissions[1]
        ) == PackageManager.PERMISSION_GRANTED
        val p3 = ContextCompat.checkSelfPermission(
            requireContext(), permissions[2]
        ) == PackageManager.PERMISSION_GRANTED
        val p4 = ContextCompat.checkSelfPermission(
            requireContext(), permissions[3]
        ) == PackageManager.PERMISSION_GRANTED

        if (!p1 || !p2 || !p3 || !p4) {
            requestPermissionLauncher.launch(permissions)
        } else {
            defineCity()
        }
    }


    @SuppressLint("MissingPermission")
    private fun defineCity() {
        val locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        //determine the current location using Network
        networkLocation(locationManager)

        //determine the current location using GPS
        if (cityName == "") {
            gpsLocation(locationManager)
        }
        Toast.makeText(context, cityName, Toast.LENGTH_SHORT).show()
        getWeatherData(cityName)
    }


    private fun getWeatherData(cityName: String) {
        constraintLayout.visibility = View.INVISIBLE
        progressBar.visibility = View.VISIBLE
        val url =
            "https://api.weatherapi.com/v1/forecast.json?key=6c46e19bec754e2da2074909222010&q=$cityName&days=7&aqi=yes&alerts=no"

        val requestQueue = Volley.newRequestQueue(context)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                val forecastDayArray =
                    response.getJSONObject("forecast").getJSONArray("forecastday")
                val country = response.getJSONObject("location").getString("country")
                val city = response.getJSONObject("location").getString("name")
                val place = "$city, $country"

                //Now forecast
                val nowForecast = getNowForecast(response)

                //Hour forecast
                val hourForecast = getHourForecast(forecastDayArray)

                //Day forecast
                val dayForecast = getDayForecast(forecastDayArray)

                weatherViewModel.apply {
                    this.nowForecast = nowForecast
                    this.hourForecast = hourForecast
                    this.dayForecast = dayForecast
                    this.city = city
                    this.place = place
                }
                updateUI(weatherViewModel)
            },
            { error ->
                swipeRefreshLayout.isRefreshing = false
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), getString(R.string.error), Toast.LENGTH_SHORT)
                    .show()
                if (weatherViewModel.city != "")
                    updateUI(weatherViewModel)
            }
        )
        requestQueue.add(jsonObjectRequest)
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(weatherViewModel: WeatherViewModel) {
        inputCity.setText(weatherViewModel.newCity)
        city.text = weatherViewModel.place
        temperature.text = weatherViewModel.nowForecast.temperature
        feelsTemperature.text = weatherViewModel.nowForecast.feelsTemperature
        Picasso.get().load(weatherViewModel.nowForecast.currentWeatherPic)
            .into(currentWeatherPicture)
        maxMinTemperature.text =
            weatherViewModel.dayForecast[0].maxTemperature + " / " + weatherViewModel.dayForecast[0].minTemperature
        sunriseTV.text = weatherViewModel.dayForecast[0].sunrise
        sunsetTV.text = weatherViewModel.dayForecast[0].sunset
        uvIndexTV.text = weatherViewModel.nowForecast.indexUV
        windSpeedTV.text = weatherViewModel.nowForecast.windSpeed
        humidityTV.text = weatherViewModel.nowForecast.humidity

        hourRVAdapter = HourRVAdapter(requireContext(), weatherViewModel.hourForecast)
        hourRecyclerView.adapter = hourRVAdapter
        dayRVAdapter = DayRVAdapter(requireContext(), weatherViewModel.dayForecast)
        dayRecyclerView.adapter = dayRVAdapter

        progressBar.visibility = View.GONE
        constraintLayout.visibility = View.VISIBLE
        if (weatherViewModel.nowForecast.isDay == 1)
            scrollView.setBackgroundResource(R.drawable.day)
        else
            scrollView.setBackgroundResource(R.drawable.night)
        swipeRefreshLayout.isRefreshing = false
    }

    private fun getNowForecast(response: JSONObject): NowForecast {
        val temperature =
            response.getJSONObject("current").getDouble("temp_c").toInt().toString() + "°"
        val isDay = response.getJSONObject("current").getInt("is_day")
        val conditionCode =
            response.getJSONObject("current").getJSONObject("condition").getInt("code")
        val currentWeatherPic = "https:" +
                response.getJSONObject("current").getJSONObject("condition").getString("icon")
        val feelsTemperature =
            response.getJSONObject("current").getDouble("feelslike_c").toInt().toString() + "°"
        val indexUV = when (response.getJSONObject("current").getDouble("uv").toInt()) {
            in 0..2 -> getString(R.string.low)
            in 3..5 -> getString(R.string.medium)
            in 6..7 -> getString(R.string.high)
            in 8..10 -> getString(R.string.very_high)
            else -> getString(R.string.extremely_high)
        }
        val windSpeed = response.getJSONObject("current").getDouble("wind_kph").toInt()
            .toString() + " " + getString(R.string.km_h)
        val humidity = response.getJSONObject("current").getInt("humidity").toString() + "%"
        return NowForecast(
            temperature,
            isDay,
            conditionCode,
            currentWeatherPic,
            feelsTemperature,
            indexUV,
            windSpeed,
            humidity
        )
    }

    private fun getHourForecast(forecastDayArray: JSONArray): MutableList<HourForecast> {
        val hourForecastList = mutableListOf<HourForecast>()
        val localTimeHour = SimpleDateFormat("HH", Locale.getDefault()).format(Date()).toInt()
        if (localTimeHour == 23) {
            val currentDay = forecastDayArray.getJSONObject(1)
            val hours = currentDay.getJSONArray("hour")
            for (i in 0 until hours.length()) {
                val hour = hours.getJSONObject(i)
                val hourForecast = HourForecast(
                    changeDateFormat1(hour.getString("time")),
                    "https:" + hour.getJSONObject("condition").getString("icon"),
                    hour.getDouble("temp_c").toInt().toString() + "°",
                    hour.getInt("chance_of_rain").toString() + "%"
                )
                hourForecastList.add(hourForecast)
            }
            return hourForecastList
        } else {
            var currentDay = forecastDayArray.getJSONObject(0)
            var hours = currentDay.getJSONArray("hour")
            for (i in localTimeHour + 1 until hours.length()) {
                val hour = hours.getJSONObject(i)
                val hourForecast = HourForecast(
                    changeDateFormat1(hour.getString("time")),
                    "https:" + hour.getJSONObject("condition").getString("icon"),
                    hour.getDouble("temp_c").toInt().toString() + "°",
                    hour.getInt("chance_of_rain").toString() + "%"
                )
                hourForecastList.add(hourForecast)
            }
            currentDay = forecastDayArray.getJSONObject(1)
            hours = currentDay.getJSONArray("hour")
            for (i in 0..localTimeHour) {
                val hour = hours.getJSONObject(i)
                val hourForecast = HourForecast(
                    changeDateFormat1(hour.getString("time")),
                    "https:" + hour.getJSONObject("condition").getString("icon"),
                    hour.getDouble("temp_c").toInt().toString() + "°",
                    hour.getInt("chance_of_rain").toString() + "%"
                )
                hourForecastList.add(hourForecast)
            }
            return hourForecastList
        }
    }

    private fun getDayForecast(forecastDayArray: JSONArray): MutableList<DayForecast> {
        val dayForecastList = mutableListOf<DayForecast>()
        for (i in 0 until forecastDayArray.length()) {
            val date = if (i == 0) getString(R.string.today) else changeDateFormat2(
                forecastDayArray.getJSONObject(i).getString("date")
            )
            val astro = forecastDayArray.getJSONObject(i).getJSONObject("astro")
            val sunrise = astro.getString("sunrise")
            val sunset = astro.getString("sunset")
            val day = forecastDayArray.getJSONObject(i).getJSONObject("day")
            val code = day.getJSONObject("condition").getInt("code")
            val dayWeatherPic = Storage.getInfo(requireContext(), 1).getValue(code).picture
            val nightWeatherPic =
                Storage.getInfo(requireContext(), 0).getValue(code).picture
            val maxTemperature = day.getDouble("maxtemp_c").toInt().toString() + "°"
            val minTemperature = day.getDouble("mintemp_c").toInt().toString() + "°"
            val chanceOfRain = day.getInt("daily_chance_of_rain").toString() + "%"
            val dayForecast = DayForecast(
                date,
                dayWeatherPic,
                nightWeatherPic,
                maxTemperature,
                minTemperature,
                chanceOfRain,
                changeDateFormat3(sunrise),
                changeDateFormat3(sunset)
            )
            dayForecastList.add(dayForecast)
        }
        return dayForecastList
    }

    @SuppressLint("SimpleDateFormat")
    private fun changeDateFormat1(date: String): String {
        val input = SimpleDateFormat("yyyy-MM-dd HH:mm")
        return (SimpleDateFormat("HH:mm", Locale.getDefault()).format(input.parse(date)!!))
    }

    @SuppressLint("SimpleDateFormat")
    private fun changeDateFormat2(date: String): String {
        val input = SimpleDateFormat("yyyy-MM-dd")
        return (SimpleDateFormat("EEEE", Locale.getDefault()).format(input.parse(date)!!)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() })
    }

    @SuppressLint("SimpleDateFormat")
    private fun changeDateFormat3(date: String): String {
        val input = SimpleDateFormat("hh:mm a")
        return (SimpleDateFormat("HH:mm", Locale.getDefault()).format(input.parse(date)!!))
    }

    private fun checkNetworkState(): Boolean {
        //true -> internet connection
        //false -> no connection
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = connectivityManager.activeNetworkInfo
        return (netInfo != null)
    }

    private fun dynamicMargin(constraintLayout: ConstraintLayout) {
        ViewCompat.setOnApplyWindowInsetsListener(constraintLayout) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.top
                bottomMargin = insets.bottom
                leftMargin = insets.left
                rightMargin = insets.right
            }
            WindowInsetsCompat.CONSUMED
        }
    }

    @SuppressLint("MissingPermission")
    private fun networkLocation(locationManager: LocationManager) {
        val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        if (location != null) {
            Log.d("location", location.toString())
            val geocoder = Geocoder(context, Locale.getDefault())
            try {
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 5)
                addresses.forEach {
                    if (it.locality != null && it.locality != "") {
                        cityName = it.locality
                    }
                }
            } catch (e: IOException) {
                Log.d("tag", "Geocoder Error")
            }
        } else {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                locationManager.getCurrentLocation(
                    LocationManager.NETWORK_PROVIDER,
                    null,
                    requireActivity().mainExecutor
                ) {
                    if (it != null) {
                        Log.d("current location", it.toString())
                        val geocoder = Geocoder(context, Locale.getDefault())
                        cityName =
                            geocoder.getFromLocation(it.latitude, it.longitude, 1)[0].locality
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun gpsLocation(locationManager: LocationManager) {
        val locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        Log.d("location", locationGPS.toString())
        if (locationGPS != null) {
            Log.d("locationGPS", locationGPS.toString())
            val geocoder = Geocoder(context, Locale.getDefault())
            try {
                val addresses =
                    geocoder.getFromLocation(locationGPS.latitude, locationGPS.longitude, 5)
                addresses.forEach {
                    if (it.locality != null && it.locality != "") {
                        cityName = it.locality
                    }
                }
            } catch (e: IOException) {
                Log.d("tag", "Geocoder Error")
            }
        } else {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                locationManager.getCurrentLocation(
                    LocationManager.GPS_PROVIDER,
                    null,
                    requireActivity().mainExecutor
                ) {
                    if (it != null) {
                        Log.d("current location", it.toString())
                        val geocoder = Geocoder(context, Locale.getDefault())
                        cityName =
                            geocoder.getFromLocation(it.latitude, it.longitude, 1)[0].locality
                    }
                }
            }
        }
    }
}