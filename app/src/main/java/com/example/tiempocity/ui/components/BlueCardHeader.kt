package com.example.tiempocity.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tiempocity.data.model.AlertSeverity
import com.example.tiempocity.data.model.WeatherInfo
import com.example.tiempocity.sound.SoundFeedbackHelper

@Composable
fun BlueCardHeader(
    weatherInfo: WeatherInfo,
    onSearchClick: () -> Unit,
    onRefreshClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val blueGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0F2027),
            Color(0xFF203A43),
            Color(0xFF2C5364)
        )
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 3.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .background(blueGradient)
                .padding(12.dp)
        ) {
            Column {
                // Top Bar: City Name & Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                SoundFeedbackHelper.playClickSound(context)
                                onSearchClick()
                            }
                            .padding(end = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Cambiar Ciudad",
                            tint = Color(0xFF81D4FA),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = weatherInfo.city.displayName,
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Action Icons Row: Search, Refresh, Settings ⚙️
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Color(0x22FFFFFF))
                                .clickable {
                                    SoundFeedbackHelper.playClickSound(context)
                                    onSearchClick()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Buscar Ciudad",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Color(0x22FFFFFF))
                                .clickable {
                                    SoundFeedbackHelper.playAcknowledgeBeep(context)
                                    onRefreshClick()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Actualizar Tiempo",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Color(0x440288D1))
                                .clickable {
                                    SoundFeedbackHelper.playClickSound(context)
                                    onSettingsClick()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Ajustes de Notificaciones",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Active Alert Banner
                weatherInfo.activeAlert?.let { alert ->
                    val alertBg = when (alert.severity) {
                        AlertSeverity.DANGER -> Color(0xFFD32F2F)
                        AlertSeverity.ADVISORY -> Color(0xFFF57C00)
                        AlertSeverity.INFO -> Color(0xFF0288D1)
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(alertBg, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "⚠️ ${alert.title}\n${alert.description}",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp,
                                lineHeight = 14.sp
                            )
                        )
                    }
                }

                // Center: Temp & Icon (With Weight to avoid overlapping)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 12.dp)
                    ) {
                        Text(
                            text = "${weatherInfo.currentTemperature}°",
                            style = MaterialTheme.typography.displaySmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 38.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = weatherInfo.weatherCondition,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFECEFF1),
                                fontWeight = FontWeight.Medium
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    AnimatedWeatherIcon(
                        weatherCode = weatherInfo.weatherCode,
                        isDay = weatherInfo.isDay,
                        rainProbability = weatherInfo.rainProbability,
                        iconSize = 42.dp
                    )
                }

                // Bottom Stats Row: Rain, Humidity, Wind
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x2BFFFFFF), RoundedCornerShape(10.dp))
                        .padding(vertical = 4.dp, horizontal = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    WeatherStatItem(
                        icon = Icons.Default.WaterDrop,
                        value = "${weatherInfo.rainProbability}%",
                        label = "Lluvia"
                    )
                    WeatherStatItem(
                        icon = Icons.Default.Cloud,
                        value = "${weatherInfo.humidity}%",
                        label = "Humedad"
                    )
                    WeatherStatItem(
                        icon = Icons.Default.Air,
                        value = "${weatherInfo.windSpeed} km/h",
                        label = "Viento"
                    )
                }
            }
        }
    }
}

@Composable
private fun WeatherStatItem(
    icon: ImageVector,
    value: String,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.White,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = "$label $value",
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

fun getWeatherIcon(code: Int, isDay: Boolean = true): ImageVector {
    return when (code) {
        0 -> if (isDay) Icons.Default.WbSunny else Icons.Default.WbCloudy
        1, 2 -> Icons.Default.WbCloudy
        3 -> Icons.Default.Cloud
        51, 53, 55, 61, 63, 65, 80, 81, 82 -> Icons.Default.WaterDrop
        95, 96, 99 -> Icons.Default.Thunderstorm
        else -> Icons.Default.Cloud
    }
}
