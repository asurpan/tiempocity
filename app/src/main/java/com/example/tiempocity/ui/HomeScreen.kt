package com.example.tiempocity.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tiempocity.data.model.DailyForecast
import com.example.tiempocity.ui.components.AemetAlertBanner
import com.example.tiempocity.ui.components.BlueCardHeader
import com.example.tiempocity.ui.components.CitySearchDialog
import com.example.tiempocity.ui.components.DailyForecastSection
import com.example.tiempocity.ui.components.DayDetailDialog
import com.example.tiempocity.ui.components.HourlyForecastRow
import com.example.tiempocity.ui.components.RainRadarCard
import com.example.tiempocity.ui.components.SettingsDialog
import com.example.tiempocity.ui.components.WidgetBannerCard

@Composable
fun HomeScreen(viewModel: WeatherViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    var showSearchDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var selectedDayForDetail by remember { mutableStateOf<DailyForecast?>(null) }

    Scaffold(
        containerColor = Color(0xFF0B171F)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF0B171F))
        ) {
            when (val state = uiState) {
                is WeatherUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF4FC3F7))
                    }
                }

                is WeatherUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
                        )
                    }
                }

                is WeatherUiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        BlueCardHeader(
                            weatherInfo = state.weatherInfo,
                            onSearchClick = { showSearchDialog = true },
                            onRefreshClick = { viewModel.loadWeather() },
                            onSettingsClick = { showSettingsDialog = true }
                        )

                        state.weatherInfo.aemetAlert?.let { aemet ->
                            AemetAlertBanner(aemetAlert = aemet)
                        }

                        HourlyForecastRow(hourlyList = state.weatherInfo.hourlyForecast)

                        DailyForecastSection(
                            dailyList = state.weatherInfo.dailyForecast,
                            onDayClick = { day -> selectedDayForDetail = day }
                        )

                        com.example.tiempocity.ui.components.AdBannerCard()

                        RainRadarCard(city = state.weatherInfo.city)

                        WidgetBannerCard()

                        Spacer(modifier = Modifier.height(28.dp))
                    }

                    selectedDayForDetail?.let { day ->
                        DayDetailDialog(
                            dailyForecast = day,
                            hourlyList = state.weatherInfo.hourlyForecast,
                            onDismiss = { selectedDayForDetail = null }
                        )
                    }
                }
            }

            if (showSearchDialog) {
                CitySearchDialog(
                    searchQuery = searchQuery,
                    searchResults = searchResults,
                    onQueryChange = { viewModel.onSearchQueryChanged(it) },
                    onCitySelected = { city ->
                        viewModel.selectCity(city)
                        showSearchDialog = false
                    },
                    onDismiss = { showSearchDialog = false }
                )
            }

            if (showSettingsDialog) {
                val currentInfo = (uiState as? WeatherUiState.Success)?.weatherInfo
                SettingsDialog(
                    weatherInfo = currentInfo,
                    onDismiss = { showSettingsDialog = false }
                )
            }
        }
    }
}
