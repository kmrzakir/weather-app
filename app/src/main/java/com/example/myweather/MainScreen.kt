package com.example.myweather



import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.myweather.databinding.ActivityMainScreenBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.Locale


class MainScreen : AppCompatActivity() {

    private var BASE_URL = "https://api.openweathermap.org"
    private var api_key = "bccbec36665b3ba3dd3d302c3476d8aa"
    private lateinit var weatherService: retrofitInterface
    private lateinit var binding: ActivityMainScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainScreenBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        var retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        weatherService = retrofit.create(retrofitInterface::class.java)
        //SETTING UP HERE DAY OF WEEK
        binding.textView.text = getDayOfWeek()
        setWeatherData("kulgam")

        //SETTING LISTENER ON SEARCH VIEW
        var searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query != null){
                    setWeatherData(query)
                    //HIDING/DISAPPERING TEH KEYBOARD AFTER CLICKING ON SEARCH ICON OF A KEYBOARD
                    val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(searchView.windowToken,0)
                }
                return true
            }
            override fun onQueryTextChange(query: String?): Boolean {
           if(query != null){
            //Toast.makeText(application, "Enter city name", Toast.LENGTH_SHORT).show()
           }
                return true
            }
        })
    }

    private fun setWeatherData(city:String) {
        binding.locationTextView.text = city
        var call = weatherService.getweatherData(city, api_key,"metric")
        call.enqueue(object : Callback<weatherData> {
            override fun onResponse(p0: Call<weatherData>, responce: Response<weatherData>) {
                var weather_data = responce.body()
                Log.d("yyy", "weather data is ======== $weather_data")
                if(weather_data != null){
                    setTemperaure(weather_data)
                }else{
                    //
                    Toast.makeText(application,"Data is NULL CHECK SPELLING",Toast.LENGTH_SHORT).show()
                }

            }
            override fun onFailure(p0: Call<weatherData>, p1: Throwable) {
                Log.d("yyy", "ERROR HAS  OCCCUER =================== ${p1.message}")
            }

        })
    }

    fun setTemperaure(weather_data: weatherData){


        var temp = weather_data.main.temp.toString()
        var mexTemp = weather_data.main.temp_max.toString()
        var minTemp = weather_data.main.temp_min.toString()
        var sunSet = weather_data.sys.sunset.toLong()
        var sunRise = weather_data.sys.sunrise.toLong()
        var weatherCondition = weather_data.weather[0].main.toString()
        var windSpeed = weather_data.wind.speed
        var humidity = weather_data.main.humidity
        var sea = weather_data.main.sea_level.toString()
        binding.temperatureTextView.text = "${temp} °C"
        binding.todayWeatherConditionTextView.text = weatherCondition
        binding.sunset.text = "${time(sunSet)}"
        binding.sunrise.text = "${time(sunRise)}"
        binding.maxTempTextView.text = "Max Temp: $mexTemp°C"
        binding.minTempTextView.text = "Max Temp: $minTemp °C"
        binding.condition.text = weatherCondition
        binding.windspeed.text = "${windSpeed} m/s"
        binding.humidity.text = "${humidity} %"
        binding.sea.text = "${sea} hPa"


        setLottieAnimation(weatherCondition)

    }

    private fun setLottieAnimation(weatherCondition: String) {
        var lottieView = binding.mainImageView
         when(weatherCondition){
             "Clear","Sunny" -> lottieView.setAnimation(R.raw.sunny_anim)
             "Clouds" -> lottieView.setAnimation(R.raw.cloudy_anim)
             "Rain" -> lottieView.setAnimation(R.raw.rainy_anim)
             "Snow" -> lottieView.setAnimation(R.raw.snowy_anim)
             "Storm" ->lottieView.setAnimation(R.raw.stormy_anim)
             else -> lottieView.setAnimation(R.raw.default_anim2)
         }
    }

    fun time(timeStamp:Long):String{
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timeStamp*1000))
    }

    fun getDayOfWeek():String{
        var calender = Calendar.getInstance()
        var dayOfWeek = calender.get(Calendar.DAY_OF_WEEK)
        // Convert the integer day of the week to a more readable format
        val dayOfWeekString = when (dayOfWeek) {
            Calendar.SUNDAY -> "Sunday"
            Calendar.MONDAY -> "Monday"
            Calendar.TUESDAY -> "Tuesday"
            Calendar.WEDNESDAY -> "Wednesday"
            Calendar.THURSDAY -> "Thursday"
            Calendar.FRIDAY -> "Friday"
            Calendar.SATURDAY -> "Saturday"
            else -> throw IllegalArgumentException("Unexpected value: $dayOfWeek")
        }
        return dayOfWeekString
    }

}
