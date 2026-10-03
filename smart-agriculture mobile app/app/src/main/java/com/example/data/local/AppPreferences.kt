package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _apiBaseUrl = MutableStateFlow(getApiBaseUrl())
    val apiBaseUrl: StateFlow<String> = _apiBaseUrl.asStateFlow()

    private val _isMockMode = MutableStateFlow(getIsMockMode())
    val isMockMode: StateFlow<Boolean> = _isMockMode.asStateFlow()

    private val _selectedLanguage = MutableStateFlow(getSelectedLanguage())
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _selectedCropId = MutableStateFlow(getSelectedCropId())
    val selectedCropId: StateFlow<String> = _selectedCropId.asStateFlow()

    fun getApiBaseUrl(): String {
        return prefs.getString(KEY_API_BASE_URL, DEFAULT_BASE_URL) ?: DEFAULT_BASE_URL
    }

    fun setApiBaseUrl(url: String) {
        val cleanUrl = if (!url.endsWith("/")) "$url/" else url
        prefs.edit().putString(KEY_API_BASE_URL, cleanUrl).apply()
        _apiBaseUrl.value = cleanUrl
    }

    fun getIsMockMode(): Boolean {
        return prefs.getBoolean(KEY_IS_MOCK_MODE, false)
    }

    fun setIsMockMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_IS_MOCK_MODE, enabled).apply()
        _isMockMode.value = enabled
    }

    fun getSelectedLanguage(): String {
        return prefs.getString(KEY_LANGUAGE, "en") ?: "en"
    }

    fun setSelectedLanguage(lang: String) {
        prefs.edit().putString(KEY_LANGUAGE, lang).apply()
        _selectedLanguage.value = lang
    }

    fun getSelectedCropId(): String {
        return prefs.getString(KEY_SELECTED_CROP_ID, "corn") ?: "corn"
    }

    fun setSelectedCropId(cropId: String) {
        prefs.edit().putString(KEY_SELECTED_CROP_ID, cropId).apply()
        _selectedCropId.value = cropId
    }

    companion object {
        private const val PREFS_NAME = "smart_agri_prefs"
        private const val KEY_API_BASE_URL = "key_api_base_url"
        private const val KEY_IS_MOCK_MODE = "key_is_mock_mode"
        private const val KEY_LANGUAGE = "key_language"
        private const val KEY_SELECTED_CROP_ID = "key_selected_crop_id"

        const val DEFAULT_BASE_URL = "http://10.0.2.2:8000/"
    }
}
