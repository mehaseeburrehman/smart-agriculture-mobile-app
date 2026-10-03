package com.example.ui.screens.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.SoilRecordEntity
import com.example.data.model.DiagnosisResult
import com.example.data.repository.RepositoryProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

enum class HistoryFilter {
    ALL,
    SCANS,
    SOIL,
    WEATHER
}

data class HistoryUiState(
    val selectedFilter: HistoryFilter = HistoryFilter.ALL,
    val searchQuery: String = "",
    val scanRecords: List<DiagnosisResult> = emptyList(),
    val soilRecords: List<SoilRecordEntity> = emptyList(),
    val isLoading: Boolean = false
)

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repoProvider = RepositoryProvider.getInstance(application)
    private val historyRepo = repoProvider.historyRepository

    private val _uiState = MutableStateFlow(HistoryUiState(isLoading = true))
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        observeHistory()
    }

    private fun observeHistory() {
        viewModelScope.launch {
            combine(
                historyRepo.getScanRecords(),
                historyRepo.getSoilRecords()
            ) { scans, soils ->
                Pair(scans, soils)
            }.collect { (scans, soils) ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    scanRecords = scans,
                    soilRecords = soils
                )
            }
        }
    }

    fun setFilter(filter: HistoryFilter) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun deleteScanRecord(id: String) {
        viewModelScope.launch {
            historyRepo.deleteScanRecord(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            historyRepo.clearAll()
        }
    }
}
