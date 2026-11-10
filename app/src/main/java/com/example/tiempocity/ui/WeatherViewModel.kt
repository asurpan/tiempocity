package com.example.tiempocity.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiempocity.data.model.City
import com.example.tiempocity.data.model.WeatherInfo
import com.example.tiempocity.data.repository.WeatherRepository
import com.example.tiempocity.notification.WeatherAlertWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface WeatherUiState {
    object Loading : WeatherUiState
    data class Success(val weatherInfo: WeatherInfo) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WeatherRepository(application)
    private val prefs = application.getSharedPreferences("tiempocity_onboarding", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val _selectedCity = MutableStateFlow(City.SEVILLA)
    val selectedCity: StateFlow<City> = _selectedCity.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow(City.SPANISH_CITIES)
    val searchResults: StateFlow<List<City>> = _searchResults.asStateFlow()

    private val _showOnboarding = MutableStateFlow(!prefs.getBoolean("is_onboarding_completed", false))
    val showOnboarding: StateFlow<Boolean> = _showOnboarding.asStateFlow()

    init {
        loadWeather(City.SEVILLA)
        WeatherAlertWorker.schedulePeriodicCheck(application)
    }

    fun completeOnboarding() {
        prefs.edit().putBoolean("is_onboarding_completed", true).apply()
        _showOnboarding.value = false
    }

    fun loadWeather(city: City = _selectedCity.value) {
        _selectedCity.value = city
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading

            val cached = repository.getCachedWeather(city.name)
            if (cached != null) {
                _uiState.value = WeatherUiState.Success(cached)
            }

            val result = repository.getWeatherForCity(city)
            result.onSuccess { weatherInfo ->
                _uiState.value = WeatherUiState.Success(weatherInfo)
            }.onFailure { error ->
                if (cached == null) {
                    _uiState.value = WeatherUiState.Error(
                        error.message ?: "No se pudo conectar a la red meteorológica"
                    )
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            _searchResults.value = repository.searchCitiesInSpain(query)
        }
    }

    fun selectCity(city: City) {
        loadWeather(city)
    }
}
