package com.example.tiempocity.data.network

import com.example.tiempocity.data.model.GeocodingSearchResponse
import com.example.tiempocity.data.model.OpenMeteoResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface WeatherApiService {
    @GET("v1/forecast")
    suspend fun getWeatherForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current_weather") currentWeather: Boolean = true,
        @Query("hourly") hourly: String = "temperature_2m,relative_humidity_2m,precipitation_probability,weather_code,wind_speed_10m",
        @Query("daily") daily: String = "weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max",
        @Query("timezone") timezone: String = "auto"
    ): Response<OpenMeteoResponse>

    @GET
    suspend fun searchSpanishCities(
        @Url url: String = "https://geocoding-api.open-meteo.com/v1/search",
        @Query("name") name: String,
        @Query("count") count: Int = 15,
        @Query("language") language: String = "es",
        @Query("format") format: String = "json",
        @Query("country_code") countryCode: String = "ES"
    ): Response<GeocodingSearchResponse>

    companion object {
        private const val BASE_URL = "https://api.open-meteo.com/"

        fun create(): WeatherApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WeatherApiService::class.java)
        }
    }
}
