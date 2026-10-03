package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Crop(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "local_name") val localName: String,
    @Json(name = "scientific_name") val scientificName: String,
    @Json(name = "variety") val variety: String = "",
    @Json(name = "model_active") val isModelActive: Boolean = true,
    @Json(name = "model_version") val modelVersion: String = "Leaf Model v2.4",
    @Json(name = "growth_stage") val defaultGrowthStage: String = "Tillering Stage",
    @Json(name = "supported_diseases") val supportedDiseases: List<String> = emptyList(),
    @Json(name = "description") val description: String = "",
    @Json(name = "optimal_temp") val optimalTempRange: String = "20-30°C",
    @Json(name = "optimal_ph") val optimalPhRange: String = "6.0-7.5"
)
