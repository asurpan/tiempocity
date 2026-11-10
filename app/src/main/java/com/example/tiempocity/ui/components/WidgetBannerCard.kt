package com.example.tiempocity.ui.components

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tiempocity.widget.TiempoWidgetReceiver

@Composable
fun WidgetBannerCard(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    val bannerGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF19324B),
            Color(0xFF1E3C72),
            Color(0xFF2A5298)
        )
    )

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .background(bannerGradient)
                .padding(14.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Widgets,
                        contentDescription = "Widget",
                        tint = Color(0xFF81D4FA),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "¡Añade el Widget Azul a tu Inicio!",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Consulta las nubes por horas y los días de la semana de un vistazo con 0% gasto extra de batería.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFFECEFF1),
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        tryPinWidget(context)
                        showDialog = true
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0288D1),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Añadir Widget a la Pantalla de Inicio",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = Color(0xFF162A38),
            titleContentColor = Color.White,
            title = {
                Text(
                    text = "📱 Cómo colocar la Tarjeta Azul",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "En móviles Xiaomi / Android, sigue estos sencillos pasos para tener tu tarjeta azul en la pantalla de inicio:",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFB0BEC5))
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "1️⃣ Ve a tu pantalla de inicio y mantén pulsado un hueco libre.\n2️⃣ Toca en 'Widgets'.\n3️⃣ Elige TIEMPOCITY y arrastra la tarjeta azul.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 22.sp
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        goToHomeScreen(context)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1))
                ) {
                    Icon(imageVector = Icons.Default.Home, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ir a Pantalla de Inicio", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cerrar", color = Color(0xFF81D4FA))
                }
            }
        )
    }
}

private fun tryPinWidget(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        try {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val myProvider = ComponentName(context, TiempoWidgetReceiver::class.java)

            if (appWidgetManager.isRequestPinAppWidgetSupported) {
                val successCallback = PendingIntent.getBroadcast(
                    context,
                    0,
                    Intent(context, TiempoWidgetReceiver::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                appWidgetManager.requestPinAppWidget(myProvider, null, successCallback)
            }
        } catch (_: Exception) { }
    }
}

private fun goToHomeScreen(context: Context) {
    try {
        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(homeIntent)
    } catch (_: Exception) { }
}
