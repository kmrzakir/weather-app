package com.example.myweather

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface retrofitInterface {
    @GET("/data/2.5/weather")

    fun getweatherData(
  @Query("q")cityName:String,
   @Query("appid")api_key:String,
  @Query("units")tt:String
    ):Call<weatherData>

}