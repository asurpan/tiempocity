package com.example.tiempocity.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tiempocity.data.model.DailyForecast
import com.example.tiempocity.data.model.HourlyForecast

@Composable
fun DayDetailDialog(
    dailyForecast: DailyForecast,
    hourlyList: List<HourlyForecast>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF12222E),
        titleContentColor = Color.White,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = dailyForecast.dayName,
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Máx: ${dailyForecast.maxTemp}° • Mín: ${dailyForecast.minTemp}° • Lluvia: ${dailyForecast.rainProbability}%",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF81D4FA)
                        )
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "DESGLOSE POR HORAS PARA ${dailyForecast.dayName.uppercase()}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF90A4AE),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(hourlyList) { hourItem ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E3545)),
                            modifier = Modifier.width(68.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(vertical = 12.dp, horizontal = 6.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = hourItem.time,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFFB0BEC5)
                                    )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                AnimatedWeatherIcon(
                                    weatherCode = hourItem.weatherCode,
                                    isDay = hourItem.isDay,
                                    iconSize = 24.dp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "${hourItem.temperature}°",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                if (hourItem.rainProbability > 0) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${hourItem.rainProbability}%",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF81D4FA),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar", color = Color(0xFF81D4FA), fontWeight = FontWeight.Bold)
            }
        }
    )
}
