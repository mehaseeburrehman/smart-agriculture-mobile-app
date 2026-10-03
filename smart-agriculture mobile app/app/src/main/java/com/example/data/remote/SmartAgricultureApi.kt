package com.example.data.remote

import com.example.data.model.HealthResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface SmartAgricultureApi {

    @GET("health")
    suspend fun checkHealth(): HealthResponse

    @GET("crops")
    suspend fun getCrops(): List<ApiCropSummary>

    @GET("crops/{crop_id}")
    suspend fun getCropDetails(@Path("crop_id") cropId: String): ApiCropDetails

    @GET("models/{crop_id}/status")
    suspend fun getModelStatus(@Path("crop_id") cropId: String): ApiModelStatus

    @Multipart
    @POST("predictions")
    suspend fun predictDisease(
        @Part image: MultipartBody.Part,
        @Part("crop_id") cropId: RequestBody,
        @Part("field_name") fieldName: RequestBody? = null
    ): ApiPredictionResponse

    @POST("soil/analyze")
    suspend fun analyzeSoil(@Body request: ApiSoilAnalysisRequest): ApiSoilAnalysisResponse

    @POST("weather/live-advisory")
    suspend fun getLiveWeatherAdvisory(@Body request: ApiLiveWeatherRequest): ApiWeatherAdvisoryResponse
}
