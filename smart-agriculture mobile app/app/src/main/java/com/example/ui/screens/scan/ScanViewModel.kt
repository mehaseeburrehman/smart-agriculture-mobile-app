package com.example.ui.screens.scan

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Crop
import com.example.data.model.DiagnosisResult
import com.example.data.model.DiagnosisStatus
import com.example.data.repository.RepositoryProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AnalysisState {
    object Idle : AnalysisState()
    data class Analyzing(val step: Int, val stepMessage: String) : AnalysisState()
    data class Success(val result: DiagnosisResult) : AnalysisState()
    data class LowConfidence(val result: DiagnosisResult) : AnalysisState()
    data class Error(val message: String) : AnalysisState()
}

class ScanViewModel(application: Application) : AndroidViewModel(application) {
    private val repoProvider = RepositoryProvider.getInstance(application)
    private val diagnosisRepo = repoProvider.getDiagnosisRepository()
    private val cropRepo = repoProvider.getCropRepository()
    private val historyRepo = repoProvider.historyRepository
    val preferences = repoProvider.preferences

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri.asStateFlow()

    private val _currentCrop = MutableStateFlow<Crop?>(null)
    val currentCrop: StateFlow<Crop?> = _currentCrop.asStateFlow()

    private val _availableCrops = MutableStateFlow<List<Crop>>(emptyList())
    val availableCrops: StateFlow<List<Crop>> = _availableCrops.asStateFlow()

    private val _isFlashOn = MutableStateFlow(false)
    val isFlashOn: StateFlow<Boolean> = _isFlashOn.asStateFlow()

    private val _isGridOn = MutableStateFlow(false)
    val isGridOn: StateFlow<Boolean> = _isGridOn.asStateFlow()

    private val _analysisState = MutableStateFlow<AnalysisState>(AnalysisState.Idle)
    val analysisState: StateFlow<AnalysisState> = _analysisState.asStateFlow()

    private val _fieldBlockName = MutableStateFlow("Field Block 3")
    val fieldBlockName: StateFlow<String> = _fieldBlockName.asStateFlow()

    private val _simulateLowConfidence = MutableStateFlow(false)
    val simulateLowConfidence: StateFlow<Boolean> = _simulateLowConfidence.asStateFlow()

    private val _latestResult = MutableStateFlow<DiagnosisResult?>(null)
    val latestResult: StateFlow<DiagnosisResult?> = _latestResult.asStateFlow()

    init {
        loadCrops()
    }

    fun loadCrops() {
        viewModelScope.launch {
            val crops = cropRepo.getCrops().getOrDefault(emptyList())
            _availableCrops.value = crops

            val savedCropId = preferences.getSelectedCropId()
            val activeCrop = crops.find { it.id.equals(savedCropId, ignoreCase = true) && it.isModelActive }
                ?: crops.firstOrNull { it.isModelActive }
                ?: crops.firstOrNull()
            _currentCrop.value = activeCrop
        }
    }

    fun selectCrop(crop: Crop) {
        preferences.setSelectedCropId(crop.id)
        _currentCrop.value = crop
    }

    fun toggleFlash() {
        _isFlashOn.value = !_isFlashOn.value
    }

    fun toggleGrid() {
        _isGridOn.value = !_isGridOn.value
    }

    fun toggleSimulateLowConfidence() {
        _simulateLowConfidence.value = !_simulateLowConfidence.value
    }

    fun setImageUri(uri: Uri?) {
        _selectedImageUri.value = uri
    }

    fun setFieldBlockName(name: String) {
        _fieldBlockName.value = name
    }

    fun analyzeImage(onSuccess: (DiagnosisResult) -> Unit, onLowConfidence: (DiagnosisResult) -> Unit) {
        val uri = _selectedImageUri.value
        val crop = _currentCrop.value

        viewModelScope.launch {
            _analysisState.value = AnalysisState.Analyzing(1, "Uploading leaf image to diagnostic service...")
            delay(600)

            _analysisState.value = AnalysisState.Analyzing(2, "Extracting leaf morphology and lesion patterns...")
            delay(700)

            _analysisState.value = AnalysisState.Analyzing(3, "Evaluating regional crop pathology models...")

            val cropId = crop?.id ?: "corn"
            val uriString = uri?.toString() ?: "content://simulated/leaf.jpg"

            val repo = repoProvider.getDiagnosisRepository()
            val result = repo.diagnoseLeaf(
                imageUri = uriString,
                cropId = cropId,
                fieldLocation = _fieldBlockName.value,
                simulateLowConfidence = _simulateLowConfidence.value
            )

            result.onSuccess { diagResult ->
                _latestResult.value = diagResult
                // Automatically save to local history database
                historyRepo.saveScanRecord(diagResult)

                if (diagResult.status == DiagnosisStatus.UNCERTAIN || diagResult.confidence < 60) {
                    _analysisState.value = AnalysisState.LowConfidence(diagResult)
                    onLowConfidence(diagResult)
                } else {
                    _analysisState.value = AnalysisState.Success(diagResult)
                    onSuccess(diagResult)
                }
            }.onFailure { err ->
                _analysisState.value = AnalysisState.Error(err.localizedMessage ?: "Analysis failed")
            }
        }
    }

    fun resetAnalysis() {
        _analysisState.value = AnalysisState.Idle
        _selectedImageUri.value = null
    }

    fun saveToFieldRecord(result: DiagnosisResult) {
        viewModelScope.launch {
            historyRepo.saveScanRecord(result)
        }
    }
}
