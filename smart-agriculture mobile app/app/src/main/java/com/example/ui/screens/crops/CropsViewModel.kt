package com.example.ui.screens.crops

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Crop
import com.example.data.repository.RepositoryProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CropsUiState(
    val isLoading: Boolean = false,
    val crops: List<Crop> = emptyList(),
    val selectedCropId: String = "wheat",
    val error: String? = null
)

class CropsViewModel(application: Application) : AndroidViewModel(application) {
    private val repoProvider = RepositoryProvider.getInstance(application)
    private val cropRepo = repoProvider.getCropRepository()
    val preferences = repoProvider.preferences

    private val _uiState = MutableStateFlow(
        CropsUiState(selectedCropId = preferences.getSelectedCropId())
    )
    val uiState: StateFlow<CropsUiState> = _uiState.asStateFlow()

    init {
        loadCrops()
    }

    fun loadCrops() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = cropRepo.getCrops()
            result.onSuccess { list ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    crops = list,
                    selectedCropId = preferences.getSelectedCropId()
                )
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = err.localizedMessage ?: "Failed to load crops"
                )
            }
        }
    }

    fun selectCrop(cropId: String) {
        preferences.setSelectedCropId(cropId)
        _uiState.value = _uiState.value.copy(selectedCropId = cropId)
    }
}
