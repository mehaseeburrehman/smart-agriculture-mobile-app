package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

enum class DiagnosisStatus {
    HEALTHY,
    DISEASE_DETECTED,
    UNCERTAIN
}

@JsonClass(generateAdapter = true)
data class DiagnosisResult(
    @Json(name = "id") val id: String,
    @Json(name = "crop_id") val cropId: String,
    @Json(name = "crop_name") val cropName: String,
    @Json(name = "scientific_name") val scientificName: String,
    @Json(name = "status") val status: DiagnosisStatus,
    @Json(name = "disease_name") val diseaseName: String,
    @Json(name = "scientific_pathogen") val scientificPathogen: String = "",
    @Json(name = "confidence_pct") val confidence: Int,
    @Json(name = "confidence_level") val confidenceLevel: String, // "High Match", "Moderate Match", "Low Match"
    @Json(name = "symptoms") val symptoms: String,
    @Json(name = "causes") val causes: String,
    @Json(name = "organic_management") val organicManagement: String,
    @Json(name = "chemical_intervention") val chemicalIntervention: String,
    @Json(name = "prevention") val prevention: String,
    @Json(name = "safety_warning") val safetyWarning: String,
    @Json(name = "image_uri") val imageUri: String? = null,
    @Json(name = "field_location") val fieldLocation: String = "Field Block 3",
    @Json(name = "timestamp") val timestamp: Long = System.currentTimeMillis()
)
