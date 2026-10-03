package com.example.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppPreferences
import com.example.data.repository.RepositoryProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ConnectionTestState {
    object Idle : ConnectionTestState()
    object Testing : ConnectionTestState()
    data class Success(val message: String, val activeModels: Int) : ConnectionTestState()
    data class Error(val error: String) : ConnectionTestState()
}

data class SettingsUiState(
    val apiUrl: String = AppPreferences.DEFAULT_BASE_URL,
    val isMockMode: Boolean = true,
    val selectedLanguage: String = "en",
    val connectionTestState: ConnectionTestState = ConnectionTestState.Idle
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repoProvider = RepositoryProvider.getInstance(application)
    val preferences = repoProvider.preferences

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            apiUrl = preferences.getApiBaseUrl(),
            isMockMode = preferences.getIsMockMode(),
            selectedLanguage = preferences.getSelectedLanguage()
        )
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun setApiUrl(url: String) {
        _uiState.value = _uiState.value.copy(apiUrl = url)
    }

    fun saveApiUrl() {
        preferences.setApiBaseUrl(_uiState.value.apiUrl)
    }

    fun setMockMode(enabled: Boolean) {
        preferences.setIsMockMode(enabled)
        _uiState.value = _uiState.value.copy(isMockMode = enabled)
    }

    fun setLanguage(lang: String) {
        preferences.setSelectedLanguage(lang)
        _uiState.value = _uiState.value.copy(selectedLanguage = lang)
    }

    fun testBackendConnection() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(connectionTestState = ConnectionTestState.Testing)
            val result = repoProvider.testConnection(_uiState.value.apiUrl)
            result.onSuccess { health ->
                _uiState.value = _uiState.value.copy(
                    connectionTestState = ConnectionTestState.Success(
                        message = "${health.service} (${health.version}) is online",
                        activeModels = health.activeModels
                    )
                )
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    connectionTestState = ConnectionTestState.Error(
                        err.localizedMessage ?: "Failed to reach backend at ${_uiState.value.apiUrl}"
                    )
                )
            }
        }
    }
}
