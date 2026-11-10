package com.example.tiempocity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.lifecycleScope
import com.example.tiempocity.ui.HomeScreen
import com.example.tiempocity.ui.WeatherViewModel
import com.example.tiempocity.ui.onboarding.OnboardingScreen
import com.example.tiempocity.ui.theme.TIEMPOCITYTheme
import com.example.tiempocity.widget.TiempoGlanceWidget
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: WeatherViewModel by viewModels()

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        try {
            MobileAds.initialize(this) {}
        } catch (e: Exception) {
            e.printStackTrace()
        }

        requestNotificationPermission()

        setContent {
            TIEMPOCITYTheme {
                val showOnboarding by viewModel.showOnboarding.collectAsState()
                if (showOnboarding) {
                    OnboardingScreen(
                        onFinishOnboarding = { viewModel.completeOnboarding() }
                    )
                } else {
                    HomeScreen(viewModel = viewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadWeather()
        lifecycleScope.launch {
            try {
                TiempoGlanceWidget().updateAll(applicationContext)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
