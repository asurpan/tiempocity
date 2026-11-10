package com.example.tiempocity.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.SatelliteAlt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tiempocity.data.model.WeatherInfo

@Composable
fun SettingsDialog(
    weatherInfo: WeatherInfo?,
    onDismiss: () -> Unit
) {
    var notifyRain by remember { mutableStateOf(true) }
    var notifyAemet by remember { mutableStateOf(true) }
    var notifyPollen by remember { mutableStateOf(true) }
    var quietHours by remember { mutableStateOf(true) } // 08:00 to 22:00
    var selectedInterval by remember { mutableStateOf("2 horas") }
    var activeTab by remember { mutableStateOf(0) } // 0: Ajustes, 1: ¿Por qué es la más exacta?

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
                Text(
                    text = "Ajustes de TIEMPOCITY",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(440.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Tab Selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E3545), RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    TabButton(
                        text = "Notificaciones",
                        isSelected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    TabButton(
                        text = "¿Por qué es mejor?",
                        isSelected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (activeTab == 0) {
                    // SECCIÓN 1: NOTIFICACIONES & ALERTAS
                    Text(
                        text = "QUÉ DESEAS NOTIFICAR",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF81D4FA), fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    SettingSwitchRow(
                        icon = Icons.Default.WaterDrop,
                        title = "Avisos de Lluvia Inminente",
                        subtitle = "Tono suave 'di-di' antes de llover",
                        checked = notifyRain,
                        onCheckedChange = { notifyRain = it }
                    )

                    SettingSwitchRow(
                        icon = Icons.Default.NotificationsActive,
                        title = "Alertas AEMET (Viento/Tormentas)",
                        subtitle = "Tono de alarma 'DA-DA-DA' para avisos",
                        checked = notifyAemet,
                        onCheckedChange = { notifyAemet = it }
                    )

                    SettingSwitchRow(
                        icon = Icons.Default.Grass,
                        title = "Alertas de Polen y Alérgenos",
                        subtitle = "Aviso cuando el índice suba en Sevilla",
                        checked = notifyPollen,
                        onCheckedChange = { notifyPollen = it }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "HORARIO DE AVISOS",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF81D4FA), fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    SettingSwitchRow(
                        icon = Icons.AutoMirrored.Filled.VolumeUp,
                        title = "Horario Silencioso Nocturno",
                        subtitle = "Solo sonar de 08:00 a 22:00 (evita molestias)",
                        checked = quietHours,
                        onCheckedChange = { quietHours = it }
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "FRECUENCIA EN SEGUNDO PLANO",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF81D4FA), fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Cada cuánto tiempo la app comprueba el tiempo sin abrirla (para actualizar el Widget de inicio y enviarte las alertas de lluvia a tiempo con 0% gasto de batería).",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFB0BEC5), fontSize = 10.sp, lineHeight = 13.sp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("1 hora", "2 horas", "4 horas").forEach { interval ->
                            val isSelected = selectedInterval == interval
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isSelected) Color(0xFF0288D1) else Color(0xFF1E3545),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedInterval = interval }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = interval,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    weatherInfo?.let {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "ESTADO AMBIENTAL ACTUAL",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF81D4FA), fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "🌾 Nivel de Polen: ${it.pollenLevel}\n🍃 Calidad del Aire: ${it.airQuality}",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                        )
                    }

                } else {
                    // SECCIÓN 2: ¿POR QUÉ ES LA MEJOR APP?
                    InfoCardItem(
                        icon = Icons.Default.SatelliteAlt,
                        title = "Modelos AEMET Harmonie (2.5 km) & ECMWF",
                        description = "Nos conectamos directamente a los modelos meteorológicos de máxima resolución de España, no a modelos globales americanos imprecisos."
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    InfoCardItem(
                        icon = Icons.Default.BatterySaver,
                        title = "0% Consumo Extra de Batería (Sin GPS en segundo plano)",
                        description = "Fijamos las coordenadas de Sevilla o tu municipio sin encender el chip GPS en segundo plano. Tu batería se mantiene intacta."
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    InfoCardItem(
                        icon = Icons.AutoMirrored.Filled.VolumeUp,
                        title = "Sonidos Diferenciados sin Mirar el Móvil",
                        description = "Reconoces si va a llover ('di-di') o si hay alerta severa ('DA-DA-DA') por el tono y el ritmo de vibración sin sacar el móvil."
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    InfoCardItem(
                        icon = Icons.Default.Speed,
                        title = "Velocidad Instantánea & Widget Azul",
                        description = "Carga en 0 milisegundos usando caché local y Widget Glance en la pantalla de inicio con actualización sin esperas."
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Guardar y Cerrar", color = Color(0xFF81D4FA), fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                if (isSelected) Color(0xFF0288D1) else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color.White,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        )
    }
}

@Composable
private fun SettingSwitchRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF81D4FA), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.bodySmall.copy(color = Color.White, fontWeight = FontWeight.SemiBold))
                Text(text = subtitle, style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFB0BEC5), fontSize = 10.sp))
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF0288D1),
                uncheckedThumbColor = Color(0xFF90A4AE),
                uncheckedTrackColor = Color(0xFF1E3545)
            )
        )
    }
}

@Composable
private fun InfoCardItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E3545), RoundedCornerShape(12.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF4FC3F7), modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = title, style = MaterialTheme.typography.titleSmall.copy(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp))
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = description, style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFECEFF1), fontSize = 11.sp))
        }
    }
}
