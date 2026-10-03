package com.example.ui.screens.soil

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.SoilRecordEntity
import com.example.data.model.Crop
import com.example.data.model.SoilAnalysisRequest
import com.example.data.model.SoilAnalysisResult
import com.example.data.repository.RepositoryProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SoilUiState(
    val isLoading: Boolean = false,
    val crops: List<Crop> = emptyList(),
    val selectedCropId: String = "wheat",
    val nitrogenInput: String = "115",
    val nitrogenUnit: String = "kg/ha",
    val phosphorusInput: String = "24",
    val phosphorusUnit: String = "kg/ha",
    val potassiumInput: String = "195",
    val potassiumUnit: String = "kg/ha",
    val phInput: String = "7.8",
    val moistureInput: String = "22",
    val fieldPlotName: String = "Multan Sector 2B",
    val result: SoilAnalysisResult? = null,
    val error: String? = null
)

class SoilViewModel(application: Application) : AndroidViewModel(application) {
    private val repoProvider = RepositoryProvider.getInstance(application)
    private val soilRepo = repoProvider.getSoilRepository()
    private val cropRepo = repoProvider.getCropRepository()
    private val historyRepo = repoProvider.historyRepository
    val preferences = repoProvider.preferences

    private val _uiState = MutableStateFlow(SoilUiState(selectedCropId = preferences.getSelectedCropId()))
    val uiState: StateFlow<SoilUiState> = _uiState.asStateFlow()

    init {
        loadCrops()
    }

    private fun loadCrops() {
        viewModelScope.launch {
            val crops = cropRepo.getCrops().getOrDefault(emptyList())
            _uiState.value = _uiState.value.copy(
                crops = crops,
                selectedCropId = preferences.getSelectedCropId()
            )
        }
    }

    fun setCrop(cropId: String) {
        _uiState.value = _uiState.value.copy(selectedCropId = cropId)
    }

    fun setNitrogen(value: String) { _uiState.value = _uiState.value.copy(nitrogenInput = value) }
    fun setPhosphorus(value: String) { _uiState.value = _uiState.value.copy(phosphorusInput = value) }
    fun setPotassium(value: String) { _uiState.value = _uiState.value.copy(potassiumInput = value) }
    fun setPh(value: String) { _uiState.value = _uiState.value.copy(phInput = value) }
    fun setMoisture(value: String) { _uiState.value = _uiState.value.copy(moistureInput = value) }
    fun setFieldPlotName(value: String) { _uiState.value = _uiState.value.copy(fieldPlotName = value) }

    fun submitSoilAnalysis(onSuccess: (SoilAnalysisResult) -> Unit) {
        val state = _uiState.value

        val n = state.nitrogenInput.toDoubleOrNull() ?: 0.0
        val p = state.phosphorusInput.toDoubleOrNull() ?: 0.0
        val k = state.potassiumInput.toDoubleOrNull() ?: 0.0
        val ph = state.phInput.toDoubleOrNull() ?: 7.0
        val moisture = state.moistureInput.toDoubleOrNull() ?: 20.0

        if (n <= 0 || p <= 0 || k <= 0) {
            _uiState.value = _uiState.value.copy(error = "Please enter valid nutrient indices greater than 0")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val request = SoilAnalysisRequest(
                cropId = state.selectedCropId,
                nitrogen = n,
                nitrogenUnit = state.nitrogenUnit,
                phosphorus = p,
                phosphorusUnit = state.phosphorusUnit,
                potassium = k,
                potassiumUnit = state.potassiumUnit,
                ph = ph,
                moisturePct = moisture,
                fieldName = state.fieldPlotName.ifBlank { "Field Plot 4" }
            )

            val result = soilRepo.analyzeSoil(request)
            result.onSuccess { analysis ->
                _uiState.value = _uiState.value.copy(isLoading = false, result = analysis)

                // Save to local room history
                historyRepo.saveSoilRecord(
                    SoilRecordEntity(
                        id = analysis.id,
                        cropId = analysis.cropId,
                        cropName = analysis.cropName,
                        fieldName = analysis.fieldName,
                        overallRating = analysis.overallRating,
                        ph = ph,
                        moisturePct = moisture,
                        nitrogen = n,
                        phosphorus = p,
                        potassium = k,
                        fertilizerPlanSummary = analysis.fertilizerPlan.firstOrNull() ?: "Balanced application",
                        organicSummary = analysis.organicAmendments.take(60),
                        timestamp = analysis.timestamp
                    )
                )

                onSuccess(analysis)
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = err.localizedMessage ?: "Analysis failed"
                )
            }
        }
    }
}
