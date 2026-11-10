package com.example.tiempocity.widget

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.tiempocity.MainActivity
import com.example.tiempocity.data.model.City
import com.example.tiempocity.data.model.DailyForecast
import com.example.tiempocity.data.model.HourlyForecast
import com.example.tiempocity.data.model.WeatherCodeMapper
import com.example.tiempocity.data.repository.WeatherRepository
import com.example.tiempocity.sound.SoundFeedbackHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TiempoGlanceWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = WeatherRepository(context)
        val cached = repository.getCachedWeather(City.SEVILLA.name)

        val cityName = cached?.city?.name ?: "Sevilla"
        val tempStr = cached?.let { "${it.currentTemperature}°" } ?: "--°"
        val rainProb = cached?.rainProbability ?: 0
        val weatherCode = cached?.weatherCode ?: 0
        val condition = WeatherCodeMapper.getConditionName(weatherCode, rainProb)
        val windStr = cached?.let { "${it.windSpeed} km/h" } ?: "10 km/h"
        val hourlyPreview = cached?.hourlyForecast?.take(5) ?: emptyList()
        val dailyPreview = cached?.dailyForecast?.take(4) ?: emptyList()

        val dropletsCount = WeatherCodeMapper.getRainDropletsCount(weatherCode, rainProb)
        val rainSymbol = when (dropletsCount) {
            1 -> "💧"
            2 -> "🌧️"
            3 -> "🌩️"
            else -> if (WeatherCodeMapper.isRainCode(weatherCode)) "🌧️" else ""
        }

        provideContent {
            GlanceTheme {
                WidgetContent(
                    cityName = cityName,
                    tempStr = tempStr,
                    condition = condition,
                    rainSymbol = rainSymbol,
                    rainProbStr = "$rainProb%",
                    windStr = windStr,
                    hourlyPreview = hourlyPreview,
                    dailyPreview = dailyPreview
                )
            }
        }
    }

    @Composable
    private fun WidgetContent(
        cityName: String,
        tempStr: String,
        condition: String,
        rainSymbol: String,
        rainProbStr: String,
        windStr: String,
        hourlyPreview: List<HourlyForecast>,
        dailyPreview: List<DailyForecast>
    ) {
        val bgProvider = ColorProvider(day = Color(0xFF0F2027), night = Color(0xFF0F2027))
        val cardBgProvider = ColorProvider(day = Color(0xFF19324B), night = Color(0xFF19324B))
        val dailyBgProvider = ColorProvider(day = Color(0xFF13273A), night = Color(0xFF13273A))
        val whiteProvider = ColorProvider(day = Color.White, night = Color.White)
        val subtitleProvider = ColorProvider(day = Color(0xFFB0BEC5), night = Color(0xFFB0BEC5))
        val accentProvider = ColorProvider(day = Color(0xFF81D4FA), night = Color(0xFF81D4FA))

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .cornerRadius(18.dp)
                .background(bgProvider)
                .padding(8.dp)
                .clickable(actionStartActivity<MainActivity>()),
            verticalAlignment = Alignment.Vertical.Top,
            horizontalAlignment = Alignment.Horizontal.Start
        ) {
            // Header Row: City, Condition, Main Temp & Refresh
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.Vertical.CenterVertically
            ) {
                Column(modifier = GlanceModifier.defaultWeight()) {
                    Text(
                        text = cityName.uppercase(),
                        style = TextStyle(
                            color = whiteProvider,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "$condition $rainSymbol • 🌧️ $rainProbStr",
                        style = TextStyle(
                            color = accentProvider,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(
                        text = "💨 Viento $windStr",
                        style = TextStyle(
                            color = subtitleProvider,
                            fontSize = 8.sp
                        )
                    )
                }
                Text(
                    text = tempStr,
                    style = TextStyle(
                        color = whiteProvider,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = " 🔄",
                    style = TextStyle(fontSize = 18.sp),
                    modifier = GlanceModifier.clickable(actionRunCallback<RefreshWidgetAction>())
                )
            }

            Spacer(modifier = GlanceModifier.height(3.dp))

            // Hourly Preview Row
            if (hourlyPreview.isNotEmpty()) {
                Text(
                    text = "PREVISIÓN POR HORAS",
                    style = TextStyle(
                        color = accentProvider,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = GlanceModifier.height(2.dp))
                Row(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .cornerRadius(10.dp)
                        .background(cardBgProvider)
                        .padding(vertical = 3.dp, horizontal = 2.dp),
                    horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                    verticalAlignment = Alignment.Vertical.CenterVertically
                ) {
                    hourlyPreview.forEach { hour ->
                        Column(
                            modifier = GlanceModifier.defaultWeight(),
                            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                        ) {
                            Text(
                                text = hour.time,
                                style = TextStyle(
                                    color = subtitleProvider,
                                    fontSize = 8.sp
                                )
                            )
                            Text(
                                text = getWidgetSymbol(hour.weatherCode, hour.rainProbability),
                                style = TextStyle(
                                    fontSize = 11.sp
                                )
                            )
                            Text(
                                text = "${hour.temperature}°",
                                style = TextStyle(
                                    color = whiteProvider,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            if (hour.rainProbability > 0) {
                                Text(
                                    text = "${hour.rainProbability}%",
                                    style = TextStyle(
                                        color = accentProvider,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = GlanceModifier.height(3.dp))

            // Daily Forecast Row
            if (dailyPreview.isNotEmpty()) {
                Text(
                    text = "PRÓXIMOS DÍAS",
                    style = TextStyle(
                        color = accentProvider,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = GlanceModifier.height(2.dp))
                Row(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .background(dailyBgProvider)
                        .padding(vertical = 3.dp, horizontal = 2.dp),
                    horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                    verticalAlignment = Alignment.Vertical.CenterVertically
                ) {
                    dailyPreview.forEach { day ->
                        val symbol = getWidgetSymbol(day.weatherCode, day.rainProbability)
                        Column(
                            modifier = GlanceModifier.defaultWeight(),
                            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                        ) {
                            Text(
                                text = "${day.dayName} $symbol",
                                style = TextStyle(
                                    color = whiteProvider,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "${day.maxTemp}° / ${day.minTemp}°",
                                style = TextStyle(
                                    color = subtitleProvider,
                                    fontSize = 8.sp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = GlanceModifier.height(2.dp))

            // Footer Instruction Tip
            Text(
                text = "💡 Pulsa 🔄 para el último pronóstico en directo",
                style = TextStyle(
                    color = subtitleProvider,
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Normal
                )
            )
        }
    }

    private fun getWidgetSymbol(code: Int, rainProb: Int): String {
        val droplets = WeatherCodeMapper.getRainDropletsCount(code, rainProb)
        return when {
            droplets == 1 -> "💧"
            droplets == 2 -> "🌧️"
            droplets == 3 -> "🌩️"
            code == 0 -> "☀️"
            code in listOf(1, 2) -> "⛅"
            code == 3 -> "☁️"
            code in listOf(71, 73, 75, 77) -> "❄️"
            code in listOf(45, 48) -> "🌫️"
            else -> "☁️"
        }
    }
}

class RefreshWidgetAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "¡Actualizando datos en directo...!", Toast.LENGTH_SHORT).show()
        }
        SoundFeedbackHelper.playAcknowledgeBeep(context)
        val repository = WeatherRepository(context)
        repository.getWeatherForCity(City.SEVILLA)
        TiempoGlanceWidget().update(context, glanceId)
    }
}
