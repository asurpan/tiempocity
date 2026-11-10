package com.example.tiempocity.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.tiempocity.data.model.AemetAlert
import com.example.tiempocity.data.model.AemetRiskLevel
import com.example.tiempocity.data.model.AlertInfo
import com.example.tiempocity.data.model.AlertSeverity
import com.example.tiempocity.data.model.City
import com.example.tiempocity.data.model.DailyForecast
import com.example.tiempocity.data.model.HourlyForecast
import com.example.tiempocity.data.model.OpenMeteoResponse
import com.example.tiempocity.data.model.WeatherCodeMapper
import com.example.tiempocity.data.model.WeatherInfo
import com.example.tiempocity.data.network.WeatherApiService
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherRepository(
    context: Context,
    private val apiService: WeatherApiService = WeatherApiService.create()
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("tiempocity_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    suspend fun getWeatherForCity(city: City): Result<WeatherInfo> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getWeatherForecast(
                latitude = city.latitude,
                longitude = city.longitude
            )
            val body = response.body()
            if (response.isSuccessful && body != null) {
                val weatherInfo = parseWeatherInfo(city, body)
                saveCache(city.name, weatherInfo)
                Result.success(weatherInfo)
            } else {
                getCachedWeather(city.name)?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Error de conexión al obtener el tiempo"))
            }
        } catch (e: Exception) {
            getCachedWeather(city.name)?.let {
                Result.success(it)
            } ?: Result.failure(e)
        }
    }

    suspend fun searchCitiesInSpain(query: String): List<City> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext City.SPANISH_CITIES

        val localFiltered = City.SPANISH_CITIES.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.province.contains(query, ignoreCase = true) ||
                    it.neighborhood.contains(query, ignoreCase = true) ||
                    it.postalCode.contains(query, ignoreCase = true)
        }.toMutableList()

        try {
            val response = apiService.searchSpanishCities(name = query)
            val results = response.body()?.results.orEmpty()

            val remoteCities = results.mapNotNull { result ->
                val name = result.name ?: return@mapNotNull null
                val lat = result.latitude ?: return@mapNotNull null
                val lon = result.longitude ?: return@mapNotNull null
                val province = result.admin1 ?: result.admin2 ?: "España"
                val postcode = result.postcodes?.firstOrNull() ?: ""

                City(
                    name = name,
                    province = province,
                    latitude = lat,
                    longitude = lon,
                    postalCode = postcode
                )
            }

            val combined = (localFiltered + remoteCities).distinctBy { "${it.name}_${it.province}" }
            combined
        } catch (_: Exception) {
            localFiltered
        }
    }

    private fun parseWeatherInfo(city: City, raw: OpenMeteoResponse): WeatherInfo {
        val currentRaw = raw.currentWeather
        val hourlyRaw = raw.hourly
        val dailyRaw = raw.daily

        val temp = currentRaw?.temperature?.toInt() ?: 20
        val weatherCode = currentRaw?.weathercode ?: 0
        val windSpeed = currentRaw?.windspeed?.toInt() ?: 10
        val isDay = (currentRaw?.is_day ?: 1) == 1

        val hourlyList = mutableListOf<HourlyForecast>()
        var humidityNow = 55
        var rainProbNow = 0

        val times = hourlyRaw?.time.orEmpty()
        val currentHourInt = SimpleDateFormat("HH", Locale.getDefault()).format(Date()).toIntOrNull() ?: 0

        // Find the index corresponding to the current hour of the day
        var startIndex = 0
        for (i in times.indices) {
            val hourInStr = times[i].substringAfter("T").substringBefore(":").toIntOrNull()
            if (hourInStr != null && hourInStr >= currentHourInt) {
                startIndex = i
                break
            }
        }

        val endIndex = (startIndex + 24).coerceAtMost(times.size)
        var isFirst = true

        for (i in startIndex until endIndex) {
            val timeStr = times.getOrNull(i) ?: ""
            val rawHour = timeStr.substringAfter("T").substringBefore(":")
            val hourFormatted = if (isFirst) "Ahora" else if (rawHour.length == 2) "$rawHour:00" else "$i:00"

            val hTemp = hourlyRaw?.temperature?.getOrNull(i)?.toInt() ?: temp
            val hRainProb = hourlyRaw?.precipitationProbability?.getOrNull(i) ?: 0
            val hCode = hourlyRaw?.weatherCode?.getOrNull(i) ?: weatherCode

            if (isFirst) {
                humidityNow = hourlyRaw?.relativeHumidity?.getOrNull(i) ?: 55
                rainProbNow = hRainProb
                isFirst = false
            }

            hourlyList.add(
                HourlyForecast(
                    time = hourFormatted,
                    temperature = hTemp,
                    rainProbability = hRainProb,
                    weatherCode = hCode,
                    isDay = isDay
                )
            )
        }

        val dailyList = mutableListOf<DailyForecast>()
        val dailyTimes = dailyRaw?.time.orEmpty()
        val dailyCount = dailyTimes.size.coerceAtMost(7)
        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())

        for (i in 0 until dailyCount) {
            val dateStr = dailyTimes.getOrNull(i) ?: ""
            val dayName = if (i == 0) "Hoy" else if (i == 1) "Mañana" else try {
                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr)
                date?.let { dayFormat.format(it).replaceFirstChar { char -> char.uppercase() } } ?: dateStr
            } catch (_: Exception) {
                dateStr
            }

            val maxT = dailyRaw?.tempMax?.getOrNull(i)?.toInt() ?: (temp + 3)
            val minT = dailyRaw?.tempMin?.getOrNull(i)?.toInt() ?: (temp - 4)
            val rainP = dailyRaw?.precipProbMax?.getOrNull(i) ?: 0
            val dCode = dailyRaw?.weatherCode?.getOrNull(i) ?: weatherCode

            dailyList.add(
                DailyForecast(
                    dayName = dayName,
                    maxTemp = maxT,
                    minTemp = minT,
                    rainProbability = rainP,
                    weatherCode = dCode
                )
            )
        }

        val aemetRiskLevel = WeatherCodeMapper.calculateAemetRisk(weatherCode, rainProbNow, windSpeed)
        val aemetAlert = if (aemetRiskLevel != AemetRiskLevel.VERDE) {
            AemetAlert(
                riskLevel = aemetRiskLevel,
                title = "Aviso AEMET MeteoAlarm en ${city.name}",
                description = "Nivel de riesgo ${aemetRiskLevel.label} por ${WeatherCodeMapper.getConditionName(weatherCode).lowercase()}.",
                phenomenon = WeatherCodeMapper.getConditionName(weatherCode)
            )
        } else null

        val alert = detectAlerts(city, weatherCode, rainProbNow, hourlyList)
        val currentTimeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        return WeatherInfo(
            city = city,
            currentTemperature = temp,
            weatherCondition = WeatherCodeMapper.getConditionName(weatherCode),
            weatherCode = weatherCode,
            humidity = humidityNow,
            windSpeed = windSpeed,
            rainProbability = rainProbNow,
            isDay = isDay,
            hourlyForecast = hourlyList,
            dailyForecast = dailyList,
            aemetAlert = aemetAlert,
            activeAlert = alert,
            lastUpdated = currentTimeStr
        )
    }

    private fun detectAlerts(
        city: City,
        currentCode: Int,
        currentRainProb: Int,
        hourly: List<HourlyForecast>
    ): AlertInfo? {
        if (WeatherCodeMapper.isHazardCode(currentCode)) {
            return AlertInfo(
                title = "¡Alerta Meteorológica Severa en ${city.name}!",
                description = "Fenómeno adverso detectado (${WeatherCodeMapper.getConditionName(currentCode)}). Toma precauciones.",
                severity = AlertSeverity.DANGER,
                isRainAlert = WeatherCodeMapper.isRainCode(currentCode)
            )
        }

        if (WeatherCodeMapper.isRainCode(currentCode) || currentRainProb >= 70) {
            return AlertInfo(
                title = "Alerta de Lluvia en ${city.name}",
                description = "Lluvia activa o inminente en ${city.name} ($currentRainProb% de probabilidad).",
                severity = AlertSeverity.ADVISORY,
                isRainAlert = true
            )
        }

        val nextRainHour = hourly.firstOrNull { it.rainProbability >= 65 || WeatherCodeMapper.isRainCode(it.weatherCode) }
        if (nextRainHour != null) {
            return AlertInfo(
                title = "Lluvia Prevista a las ${nextRainHour.time}",
                description = "Se prevé inicio de precipitación (${nextRainHour.rainProbability}%) a las ${nextRainHour.time} en ${city.name}.",
                severity = AlertSeverity.INFO,
                isRainAlert = true
            )
        }

        return null
    }

    private fun saveCache(cityName: String, weatherInfo: WeatherInfo) {
        try {
            val json = gson.toJson(weatherInfo)
            prefs.edit().putString("cache_$cityName", json).apply()
        } catch (_: Exception) { }
    }

    fun getCachedWeather(cityName: String): WeatherInfo? {
        val json = prefs.getString("cache_$cityName", null) ?: return null
        return try {
            gson.fromJson(json, WeatherInfo::class.java)
        } catch (_: Exception) {
            null
        }
    }
}
