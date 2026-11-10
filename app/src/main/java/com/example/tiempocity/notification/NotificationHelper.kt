package com.example.tiempocity.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.tiempocity.MainActivity
import com.example.tiempocity.R
import com.example.tiempocity.data.model.AlertInfo

object NotificationHelper {
    private const val RAIN_CHANNEL_ID = "tiempocity_rain_channel"
    private const val HAZARD_CHANNEL_ID = "tiempocity_hazard_channel"

    private const val RAIN_NOTIFICATION_ID = 1001
    private const val HAZARD_NOTIFICATION_ID = 1002

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                .build()

            // 1. Canal de Lluvia (Tono suave / doble vibración corta)
            val rainSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val rainChannel = NotificationChannel(
                RAIN_CHANNEL_ID,
                "Avisos de Lluvia Inminente",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Sonido característico cuando está a punto de llover en tu zona."
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 200, 100, 200) // di-di
                setSound(rainSound, audioAttributes)
            }

            // 2. Canal de Peligro / Alerta Severa AEMET (Tono de Alarma fuerte / triple vibración larga)
            val hazardSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val hazardChannel = NotificationChannel(
                HAZARD_CHANNEL_ID,
                "Alertas de Peligro Meteorológico",
                NotificationManager.IMPORTANCE_MAX
            ).apply {
                description = "Tono de alarma potente para avisos de riesgo importante (AEMET)."
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 600, 200, 600, 200, 600) // DA-DA-DA
                setSound(hazardSound, audioAttributes)
            }

            notificationManager.createNotificationChannel(rainChannel)
            notificationManager.createNotificationChannel(hazardChannel)
        }
    }

    fun showWeatherNotification(context: Context, alert: AlertInfo) {
        createNotificationChannels(context)

        val isHazard = !alert.isRainAlert

        val channelId = if (isHazard) HAZARD_CHANNEL_ID else RAIN_CHANNEL_ID
        val notificationId = if (isHazard) HAZARD_NOTIFICATION_ID else RAIN_NOTIFICATION_ID

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = if (isHazard) {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        } else {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }

        val vibratePattern = if (isHazard) {
            longArrayOf(0, 600, 200, 600, 200, 600)
        } else {
            longArrayOf(0, 200, 100, 200)
        }

        val prefix = if (isHazard) "⚠️ ALERTA: " else "🌧️ AVISO: "

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(prefix + alert.title)
            .setContentText(alert.description)
            .setStyle(NotificationCompat.BigTextStyle().bigText(alert.description))
            .setPriority(if (isHazard) NotificationCompat.PRIORITY_MAX else NotificationCompat.PRIORITY_HIGH)
            .setSound(soundUri)
            .setVibrate(vibratePattern)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
    }
}
