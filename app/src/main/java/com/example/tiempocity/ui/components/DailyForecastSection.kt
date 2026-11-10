package com.example.tiempocity.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tiempocity.data.model.DailyForecast

@Composable
fun DailyForecastSection(
    dailyList: List<DailyForecast>,
    onDayClick: (DailyForecast) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "PRÓXIMOS DÍAS (TOCA CUALQUIER DÍA PARA EL DESGLOSE)",
            style = MaterialTheme.typography.labelMedium.copy(
                color = Color(0xFF546E7A),
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                fontSize = 10.sp
            ),
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
        )

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1E3C72)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp)
            ) {
                dailyList.forEachIndexed { index, item ->
                    DailyRowItem(
                        daily = item,
                        onClick = { onDayClick(item) }
                    )
                    if (index < dailyList.size - 1) {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0x1FA0A0A0))
                                .padding(vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DailyRowItem(
    daily: DailyForecast,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Day Name
        Text(
            text = daily.dayName,
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.width(95.dp)
        )

        // Weather Icon & Rain Prob
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(90.dp)
        ) {
            AnimatedWeatherIcon(
                weatherCode = daily.weatherCode,
                rainProbability = daily.rainProbability,
                iconSize = 22.dp
            )
            if (daily.rainProbability > 0) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${daily.rainProbability}%",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF81D4FA),
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        // Max & Min Temps
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${daily.maxTemp}°",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "${daily.minTemp}°",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color(0xFFB0BEC5),
                    fontWeight = FontWeight.Normal
                )
            )
        }
    }
}
