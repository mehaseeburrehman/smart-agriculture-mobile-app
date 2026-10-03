package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.AppPreferences
import com.example.data.model.HealthResponse
import com.example.data.remote.ApiClient
import com.example.data.remote.SmartAgricultureApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RepositoryProvider private constructor(private val context: Context) {

    val preferences = AppPreferences(context)
    private val database = AppDatabase.getInstance(context)

    // Local History Repository (backed by Room DB)
    val historyRepository: HistoryRepository = HistoryRepositoryImpl(database.historyDao())

    // Mock Implementations
    val mockCropRepository = MockCropRepository()
    val mockDiagnosisRepository = MockDiagnosisRepository()
    val mockWeatherRepository = MockWeatherRepository()
    val mockSoilRepository = MockSoilRepository()

    // API Service cache
    private var cachedBaseUrl: String? = null
    private var cachedApi: SmartAgricultureApi? = null

    private fun getApiService(): SmartAgricultureApi {
        val currentUrl = preferences.getApiBaseUrl()
        if (cachedApi == null || cachedBaseUrl != currentUrl) {
            cachedBaseUrl = currentUrl
            cachedApi = ApiClient.createService(currentUrl)
        }
        return cachedApi!!
    }

    fun getCropRepository(): CropRepository {
        return if (preferences.getIsMockMode()) {
            mockCropRepository
        } else {
            ApiCropRepository(getApiService())
        }
    }

    fun getDiagnosisRepository(): DiagnosisRepository {
        return if (preferences.getIsMockMode()) {
            mockDiagnosisRepository
        } else {
            ApiDiagnosisRepository(getApiService(), context)
        }
    }

    fun getWeatherRepository(): WeatherRepository {
        return if (preferences.getIsMockMode()) {
            mockWeatherRepository
        } else {
            ApiWeatherRepository(getApiService())
        }
    }

    fun getSoilRepository(): SoilRepository {
        return if (preferences.getIsMockMode()) {
            mockSoilRepository
        } else {
            ApiSoilRepository(getApiService())
        }
    }

    suspend fun testConnection(url: String): Result<HealthResponse> = withContext(Dispatchers.IO) {
        try {
            val api = ApiClient.createService(url)
            val health = api.checkHealth()
            Result.success(health)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: RepositoryProvider? = null

        fun getInstance(context: Context): RepositoryProvider {
            return INSTANCE ?: synchronized(this) {
                val instance = RepositoryProvider(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
