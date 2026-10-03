package com.example.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AdvisoryPurpose
import com.example.data.model.Crop
import com.example.data.model.DiagnosisResult
import com.example.data.model.WeatherAdvisory
import com.example.data.repository.RepositoryProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val selectedCrop: Crop? = null,
    val weatherAdvisory: WeatherAdvisory? = null,
    val recentScans: List<DiagnosisResult> = emptyList(),
    val error: String? = null
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repoProvider = RepositoryProvider.getInstance(application)
    private val cropRepo = repoProvider.getCropRepository()
    private val weatherRepo = repoProvider.getWeatherRepository()
    private val historyRepo = repoProvider.historyRepository
    val preferences = repoProvider.preferences

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
        observeRecentScans()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val cropId = preferences.getSelectedCropId()

            val cropResult = cropRepo.getCropById(cropId)
            val crop = cropResult.getOrNull()

            val weatherResult = weatherRepo.getWeatherAdvisory(
                cropId = cropId,
                purpose = AdvisoryPurpose.SPRAYING
            )
            val weather = weatherResult.getOrNull()

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                selectedCrop = crop,
                weatherAdvisory = weather
            )
        }
    }

    private fun observeRecentScans() {
        viewModelScope.launch {
            historyRepo.getScanRecords().collect { scans ->
                _uiState.value = _uiState.value.copy(
                    recentScans = scans.take(3)
                )
            }
        }
    }
}
