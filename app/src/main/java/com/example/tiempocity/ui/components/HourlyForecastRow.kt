package com.example.tiempocity.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tiempocity.data.model.HourlyForecast

@Composable
fun HourlyForecastRow(
    hourlyList: List<HourlyForecast>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = "PREVISIÓN POR HORAS",
            style = MaterialTheme.typography.labelMedium.copy(
                color = Color(0xFF546E7A),
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(hourlyList) { hourItem ->
                HourlyCardItem(hourItem = hourItem)
            }
        }
    }
}

@Composable
private fun HourlyCardItem(hourItem: HourlyForecast) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E3C72)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.width(72.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 14.dp, horizontal = 8.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = hourItem.time,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFFB0BEC5),
                    fontWeight = FontWeight.Medium
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedWeatherIcon(
                weatherCode = hourItem.weatherCode,
                isDay = hourItem.isDay,
                rainProbability = hourItem.rainProbability,
                iconSize = 28.dp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${hourItem.temperature}°",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )

            if (hourItem.rainProbability > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${hourItem.rainProbability}%",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF81D4FA),
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}
