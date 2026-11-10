package com.example.tiempocity.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

// Open-Meteo Raw API DTOs (Fully Null-Safe for Gson + @Keep for R8)
@Keep
data class OpenMeteoResponse(
    @SerializedName("current_weather") val currentWeather: RawCurrentWeather? = null,
    @SerializedName("hourly") val hourly: RawHourlyData? = null,
    @SerializedName("daily") val daily: RawDailyData? = null
)

@Keep
data class RawCurrentWeather(
    @SerializedName("temperature") val temperature: Double? = null,
    @SerializedName("windspeed") val windspeed: Double? = null,
    @SerializedName("winddirection") val winddirection: Double? = null,
    @SerializedName("weathercode") val weathercode: Int? = null,
    @SerializedName("is_day") val is_day: Int? = null,
    @SerializedName("time") val time: String? = null
)

@Keep
data class RawHourlyData(
    @SerializedName("time") val time: List<String>? = null,
    @SerializedName("temperature_2m") val temperature: List<Double>? = null,
    @SerializedName("relative_humidity_2m") val relativeHumidity: List<Int>? = null,
    @SerializedName("precipitation_probability") val precipitationProbability: List<Int>? = null,
    @SerializedName("weather_code") val weatherCode: List<Int>? = null,
    @SerializedName("wind_speed_10m") val windSpeed: List<Double>? = null
)

@Keep
data class RawDailyData(
    @SerializedName("time") val time: List<String>? = null,
    @SerializedName("weather_code") val weatherCode: List<Int>? = null,
    @SerializedName("temperature_2m_max") val tempMax: List<Double>? = null,
    @SerializedName("temperature_2m_min") val tempMin: List<Double>? = null,
    @SerializedName("precipitation_probability_max") val precipProbMax: List<Int>? = null
)

// Geocoding Search DTOs for Spain
@Keep
data class GeocodingSearchResponse(
    @SerializedName("results") val results: List<GeocodingCityResult>? = null
)

@Keep
data class GeocodingCityResult(
    @SerializedName("name") val name: String? = null,
    @SerializedName("latitude") val latitude: Double? = null,
    @SerializedName("longitude") val longitude: Double? = null,
    @SerializedName("admin1") val admin1: String? = null,
    @SerializedName("admin2") val admin2: String? = null,
    @SerializedName("postcodes") val postcodes: List<String>? = null
)

// AEMET Official Risk Levels
@Keep
enum class AemetRiskLevel(val label: String, val hexColor: Long) {
    VERDE("Sin riesgo (Verde)", 0xFF2E7D32),
    AMARILLO("Riesgo (Amarillo)", 0xFFFBC02D),
    NARANJA("Riesgo Importante (Naranja)", 0xFFF57C00),
    ROJO("Riesgo Extremo (Rojo)", 0xFFD32F2F)
}

@Keep
data class AemetAlert(
    val riskLevel: AemetRiskLevel,
    val title: String,
    val description: String,
    val phenomenon: String
)

// Clean Domain UI Models
@Keep
data class WeatherInfo(
    val city: City,
    val currentTemperature: Int,
    val weatherCondition: String,
    val weatherCode: Int,
    val humidity: Int,
    val windSpeed: Int,
    val rainProbability: Int,
    val isDay: Boolean,
    val hourlyForecast: List<HourlyForecast>,
    val dailyForecast: List<DailyForecast>,
    val pollenLevel: String = "Bajo (Olivo/Gramíneas)",
    val airQuality: String = "Excelente (AQI 18)",
    val aemetAlert: AemetAlert? = null,
    val activeAlert: AlertInfo? = null,
    val lastUpdated: String = ""
)

@Keep
data class HourlyForecast(
    val time: String,
    val temperature: Int,
    val rainProbability: Int,
    val weatherCode: Int,
    val isDay: Boolean
)

@Keep
data class DailyForecast(
    val dayName: String,
    val maxTemp: Int,
    val minTemp: Int,
    val rainProbability: Int,
    val weatherCode: Int
)

@Keep
enum class AlertSeverity {
    INFO, ADVISORY, DANGER
}

@Keep
data class AlertInfo(
    val title: String,
    val description: String,
    val severity: AlertSeverity,
    val isRainAlert: Boolean = false
)

object WeatherCodeMapper {
    fun getConditionName(code: Int, rainProb: Int = 0): String {
        val droplets = getRainDropletsCount(code, rainProb)
        return when {
            droplets == 1 -> "Lluvia de poca intensidad"
            droplets == 2 -> "Lluvia moderada"
            droplets == 3 -> "Lluvia fuerte o tormenta"
            code == 0 -> "Cielo despejado"
            code in 1..2 -> "Parcialmente nublado"
            code == 3 -> "Nublado"
            code in 45..48 -> "Niebla"
            code in 71..77 -> "Nieve"
            else -> "Tiempo variable"
        }
    }

    fun isRainCode(code: Int): Boolean {
        return code in listOf(51, 53, 55, 61, 63, 65, 66, 67, 80, 81, 82, 95, 96, 99)
    }

    fun isHazardCode(code: Int): Boolean {
        return code in listOf(65, 67, 75, 82, 95, 96, 99)
    }

    fun getRainDropletsCount(code: Int, rainProb: Int): Int {
        if (!isRainCode(code) && rainProb < 30) return 0
        return when {
            code in listOf(65, 67, 75, 82, 95, 96, 99) || rainProb >= 70 -> 3 // 3 gotas (Fuerte)
            code in listOf(55, 63, 81) || rainProb >= 50 -> 2                 // 2 gotas (Moderada)
            code in listOf(51, 53, 61, 80) || rainProb >= 30 -> 1                 // 1 gota (Poca intensidad)
            else -> 0
        }
    }

    fun calculateAemetRisk(code: Int, rainProb: Int, windSpeed: Int): AemetRiskLevel {
        return when {
            code in listOf(96, 99) || windSpeed >= 90 || rainProb >= 90 -> AemetRiskLevel.ROJO
            code in listOf(65, 75, 82, 95) || windSpeed >= 70 || rainProb >= 75 -> AemetRiskLevel.NARANJA
            code in listOf(55, 63, 73, 81) || windSpeed >= 50 || rainProb >= 50 -> AemetRiskLevel.AMARILLO
            else -> AemetRiskLevel.VERDE
        }
    }
}
