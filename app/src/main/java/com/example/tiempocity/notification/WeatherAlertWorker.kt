package com.example.tiempocity.notification

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.tiempocity.data.model.City
import com.example.tiempocity.data.repository.WeatherRepository
import java.util.concurrent.TimeUnit

class WeatherAlertWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val repository = WeatherRepository(context)
        val defaultCity = City.SEVILLA

        val weatherResult = repository.getWeatherForCity(defaultCity)
        weatherResult.getOrNull()?.let { weatherInfo ->
            val alert = weatherInfo.activeAlert
            if (alert != null) {
                NotificationHelper.showWeatherNotification(context, alert)
            }
        }

        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "tiempocity_background_weather_check"

        fun schedulePeriodicCheck(context: Context) {
            try {
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()

                val workRequest = PeriodicWorkRequestBuilder<WeatherAlertWorker>(
                    2, TimeUnit.HOURS
                ).setConstraints(constraints).build()

                WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    workRequest
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
