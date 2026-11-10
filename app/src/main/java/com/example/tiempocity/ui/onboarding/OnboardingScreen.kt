package com.example.tiempocity.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class OnboardingPage(
    val title: String,
    val subtitle: String,
    val description: String,
    val icon: ImageVector,
    val iconTint: Color
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit
) {
    val pages = remember {
        listOf(
            OnboardingPage(
                title = "Bienvenido a TIEMPOCITY",
                subtitle = "El tiempo más exacto de Sevilla y España",
                description = "Tarjetas azules elegantes con iconos blancos nítidos y previsión por horas calculada al minuto con modelos AEMET Harmonie y ECMWF.",
                icon = Icons.Default.WbSunny,
                iconTint = Color(0xFFFFD54F)
            ),
            OnboardingPage(
                title = "Avisos Sonoros Inteligentes",
                subtitle = "Sabe si va a llover sin mirar el móvil",
                description = "Tono suave 'di-di' para aviso de lluvia inminente y tono de alarma 'DA-DA-DA' para alertas de peligro de la AEMET.",
                icon = Icons.AutoMirrored.Filled.VolumeUp,
                iconTint = Color(0xFF81D4FA)
            ),
            OnboardingPage(
                title = "Radar en Directo y Barrios",
                subtitle = "Seguimiento de nubes y microlocalización",
                description = "Mapa de radar de lluvia en vivo y buscador inteligente por municipios, códigos postales y barrios (Triana, Nervión, Macarena, Aljarafe...).",
                icon = Icons.Default.Map,
                iconTint = Color(0xFF80CBC4)
            ),
            OnboardingPage(
                title = "Widget Azul & 0% Gasto Extra",
                subtitle = "Máxima información sin agotar la batería",
                description = "Widget Glance de tarjeta azul para tu pantalla de inicio.\n\n👉 Para añadirlo: Mantén pulsada la pantalla de inicio ➔ Toca en 'Widgets' ➔ Elige TIEMPOCITY y arrástralo.",
                icon = Icons.Default.BatterySaver,
                iconTint = Color(0xFF81C784)
            )
        )
    }

    var currentPage by remember { mutableIntStateOf(0) }

    val bgGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF09141B),
            Color(0xFF0F2027),
            Color(0xFF203A43)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // Skip Button top right
        if (currentPage < pages.size - 1) {
            TextButton(
                onClick = onFinishOnboarding,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Text(
                    text = "Omitir",
                    color = Color(0xFF90A4AE),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        // Center Content Slide
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(bottom = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedContent(
                targetState = currentPage,
                transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(400)) },
                label = "OnboardingSlide"
            ) { pageIdx ->
                val page = pages[pageIdx]
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .background(Color(0x1F26A69A), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = page.icon,
                            contentDescription = null,
                            tint = page.iconTint,
                            modifier = Modifier.size(56.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = page.title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = page.subtitle,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color(0xFF4FC3F7),
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = page.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFFCFD8DC),
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    )
                }
            }
        }

        // Bottom Bar: Dots & Next/Start Button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            // Dots indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                pages.indices.forEach { index ->
                    val isSelected = index == currentPage
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(8.dp)
                            .width(if (isSelected) 24.dp else 8.dp)
                            .background(
                                color = if (isSelected) Color(0xFF4FC3F7) else Color(0x4490A4AE),
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Action Button
            Button(
                onClick = {
                    if (currentPage < pages.size - 1) {
                        currentPage++
                    } else {
                        onFinishOnboarding()
                    }
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0288D1),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = if (currentPage < pages.size - 1) "Siguiente" else "¡Comenzar ahora!",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Políticas de Privacidad de Google Play Store • Datos Protegidos",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color(0xFF78909C),
                    fontSize = 9.sp,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
