package com.example.tiempocity.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.tiempocity.data.model.WeatherCodeMapper
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun AnimatedWeatherIcon(
    weatherCode: Int,
    isDay: Boolean = true,
    rainProbability: Int = 0,
    iconSize: Dp = 42.dp,
    tint: Color = Color.White,
    modifier: Modifier = Modifier
) {
    val dropletCount = WeatherCodeMapper.getRainDropletsCount(weatherCode, rainProbability)

    if (dropletCount > 0) {
        var isHighlighted by remember { mutableStateOf(true) }

        LaunchedEffect(Unit) {
            while (isActive) {
                delay(650)
                isHighlighted = !isHighlighted
            }
        }

        val currentAlpha = if (isHighlighted) 1.0f else 0.4f
        val dropSize = iconSize * 0.32f

        // Intuitive Cloud Color Scale based on Rain Intensity
        val cloudColor = when (dropletCount) {
            1 -> Color(0xFF90A4AE) // Gris Suave (Lluvia Ligera)
            2 -> Color(0xFF546E7A) // Gris Plomizo (Lluvia Moderada)
            3 -> Color(0xFF37474F) // Gris Oscuro Tormenta (Lluvia Fuerte)
            else -> tint
        }

        // Google Weather Style: Cloud with Droplets overlapping at bottom-right
        Box(
            contentAlignment = Alignment.TopStart,
            modifier = modifier.size(iconSize * 1.25f)
        ) {
            // Subtle light outline behind dark/grey storm clouds for high contrast
            if (dropletCount >= 2) {
                Icon(
                    imageVector = Icons.Default.Cloud,
                    contentDescription = null,
                    tint = Color(0x66ECEFF1),
                    modifier = Modifier
                        .size(iconSize + 2.dp)
                        .offset(x = (-1).dp, y = (-1).dp)
                        .align(Alignment.TopStart)
                )
            }

            // Main Cloud
            Icon(
                imageVector = Icons.Default.Cloud,
                contentDescription = "Nube",
                tint = cloudColor,
                modifier = Modifier
                    .size(iconSize)
                    .align(Alignment.TopStart)
            )

            // Droplets overlapping at bottom-right corner of the cloud
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-2).dp, y = (2).dp)
            ) {
                when (dropletCount) {
                    1 -> {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = "1 gota",
                            tint = Color(0xFF81D4FA),
                            modifier = Modifier
                                .size(dropSize)
                                .alpha(currentAlpha)
                        )
                    }

                    2 -> {
                        Row {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = "Gota 1",
                                tint = Color(0xFF81D4FA),
                                modifier = Modifier
                                    .size(dropSize)
                                    .alpha(currentAlpha)
                            )
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = "Gota 2",
                                tint = Color(0xFF81D4FA),
                                modifier = Modifier
                                    .size(dropSize)
                                    .alpha(currentAlpha)
                            )
                        }
                    }

                    3 -> {
                        // 3 Droplets: 2 on top, 1 centered underneath
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row {
                                Icon(
                                    imageVector = Icons.Default.WaterDrop,
                                    contentDescription = "Gota 1",
                                    tint = Color(0xFF81D4FA),
                                    modifier = Modifier
                                        .size(dropSize)
                                        .alpha(currentAlpha)
                                )
                                Icon(
                                    imageVector = Icons.Default.WaterDrop,
                                    contentDescription = "Gota 2",
                                    tint = Color(0xFF81D4FA),
                                    modifier = Modifier
                                        .size(dropSize)
                                        .alpha(currentAlpha)
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = "Gota 3",
                                tint = Color(0xFF81D4FA),
                                modifier = Modifier
                                    .size(dropSize)
                                    .alpha(currentAlpha)
                            )
                        }
                    }
                }
            }
        }
    } else {
        Icon(
            imageVector = getWeatherIconStatic(weatherCode, isDay),
            contentDescription = null,
            tint = tint,
            modifier = modifier.size(iconSize)
        )
    }
}

fun getWeatherIconStatic(code: Int, isDay: Boolean = true): ImageVector {
    return when (code) {
        0 -> if (isDay) Icons.Default.WbSunny else Icons.Default.WbCloudy
        1, 2 -> Icons.Default.WbCloudy
        3 -> Icons.Default.Cloud
        51, 53, 55, 61, 63, 65, 80, 81, 82 -> Icons.Default.WaterDrop
        95, 96, 99 -> Icons.Default.Thunderstorm
        else -> Icons.Default.Cloud
    }
}
