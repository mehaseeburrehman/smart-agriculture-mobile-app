package com.example.ui.screens.weather

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AdvisoryPurpose
import com.example.data.model.Crop
import com.example.data.model.WeatherAdvisory
import com.example.data.repository.RepositoryProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WeatherUiState(
    val isLoading: Boolean = false,
    val selectedPurpose: AdvisoryPurpose = AdvisoryPurpose.SPRAYING,
    val advisory: WeatherAdvisory? = null,
    val availableCrops: List<Crop> = emptyList(),
    val selectedCrop: Crop? = null,
    val isRefreshing: Boolean = false,
    val error: String? = null
)

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    private val repoProvider = RepositoryProvider.getInstance(application)
    private val weatherRepo = repoProvider.getWeatherRepository()
    private val cropRepo = repoProvider.getCropRepository()
    val preferences = repoProvider.preferences

    private val _uiState = MutableStateFlow(WeatherUiState(isLoading = true))
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    init {
        loadWeatherAdvisory()
    }

    fun loadWeatherAdvisory(purpose: AdvisoryPurpose = _uiState.value.selectedPurpose) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, selectedPurpose = purpose)

            val crops = cropRepo.getCrops().getOrDefault(emptyList())
            val savedCropId = preferences.getSelectedCropId()
            val crop = crops.find { it.id.equals(savedCropId, ignoreCase = true) } ?: crops.firstOrNull()

            val result = weatherRepo.getWeatherAdvisory(
                cropId = crop?.id ?: "wheat",
                purpose = purpose
            )

            result.onSuccess { adv ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isRefreshing = false,
                    availableCrops = crops,
                    selectedCrop = crop,
                    advisory = adv
                )
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isRefreshing = false,
                    error = err.localizedMessage ?: "Failed to fetch weather advisory"
                )
            }
        }
    }

    fun setPurpose(purpose: AdvisoryPurpose) {
        loadWeatherAdvisory(purpose)
    }

    fun selectCrop(crop: Crop) {
        preferences.setSelectedCropId(crop.id)
        _uiState.value = _uiState.value.copy(selectedCrop = crop)
        loadWeatherAdvisory(_uiState.value.selectedPurpose)
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(isRefreshing = true)
        loadWeatherAdvisory(_uiState.value.selectedPurpose)
    }
}
