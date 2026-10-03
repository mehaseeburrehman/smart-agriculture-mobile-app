package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HealthResponse(
    @Json(name = "status") val status: String = "ok",
    @Json(name = "service") val service: String = "Smart Agriculture API",
    @Json(name = "version") val version: String = "1.0.0",
    @Json(name = "active_models") val activeModels: Int = 5,
    @Json(name = "timestamp") val timestamp: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
data class ModelStatus(
    @Json(name = "crop_id") val cropId: String,
    @Json(name = "status") val status: String, // "active", "training", "offline"
    @Json(name = "model_version") val version: String,
    @Json(name = "validation_accuracy") val accuracy: Double,
    @Json(name = "supported_classes") val supportedClasses: List<String>
)
