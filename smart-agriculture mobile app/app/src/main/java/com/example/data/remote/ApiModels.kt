package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiCropSummary(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "disease_model_ready") val diseaseModelReady: Boolean = false
)

@JsonClass(generateAdapter = true)
data class ApiCropDetails(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "scientific_name") val scientificName: String = "",
    @Json(name = "description") val description: String = "",
    @Json(name = "soil") val soil: ApiSoilRanges? = null
)

@JsonClass(generateAdapter = true)
data class ApiSoilRanges(
    @Json(name = "ph_range") val phRange: List<Double> = emptyList()
)

@JsonClass(generateAdapter = true)
data class ApiModelStatus(
    @Json(name = "crop_id") val cropId: String,
    @Json(name = "ready") val ready: Boolean,
    @Json(name = "runtime") val runtime: String? = null,
    @Json(name = "message") val message: String = ""
)

@JsonClass(generateAdapter = true)
data class ApiDiseaseGuidance(
    @Json(name = "summary") val summary: String = "",
    @Json(name = "symptoms") val symptoms: List<String> = emptyList(),
    @Json(name = "possible_causes") val possibleCauses: List<String> = emptyList(),
    @Json(name = "organic_management") val organicManagement: List<String> = emptyList(),
    @Json(name = "chemical_management") val chemicalManagement: List<String> = emptyList(),
    @Json(name = "prevention") val prevention: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class ApiPredictionResponse(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "crop_id") val cropId: String,
    @Json(name = "status") val status: String,
    @Json(name = "predicted_label") val predictedLabel: String,
    @Json(name = "disease_name") val diseaseName: String,
    @Json(name = "confidence") val confidence: Double,
    @Json(name = "threshold") val threshold: Double,
    @Json(name = "guidance") val guidance: ApiDiseaseGuidance? = null,
    @Json(name = "message") val message: String = ""
)

@JsonClass(generateAdapter = true)
data class ApiSoilAnalysisRequest(
    @Json(name = "crop_id") val cropId: String,
    @Json(name = "nitrogen") val nitrogen: Double,
    @Json(name = "phosphorus") val phosphorus: Double,
    @Json(name = "potassium") val potassium: Double,
    @Json(name = "ph") val ph: Double,
    @Json(name = "moisture") val moisture: Double,
    @Json(name = "npk_unit") val npkUnit: String = "mg/kg"
)

@JsonClass(generateAdapter = true)
data class ApiAdvisoryItem(
    @Json(name = "level") val level: String,
    @Json(name = "title") val title: String,
    @Json(name = "message") val message: String
)

@JsonClass(generateAdapter = true)
data class ApiSoilAnalysisResponse(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "crop_id") val cropId: String,
    @Json(name = "suitability") val suitability: String,
    @Json(name = "advisories") val advisories: List<ApiAdvisoryItem> = emptyList(),
    @Json(name = "disclaimer") val disclaimer: String = ""
)

@JsonClass(generateAdapter = true)
data class ApiLiveWeatherRequest(
    @Json(name = "crop_id") val cropId: String,
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
    @Json(name = "purpose") val purpose: String
)

@JsonClass(generateAdapter = true)
data class ApiWeatherSnapshot(
    @Json(name = "temperature_c") val temperatureC: Double,
    @Json(name = "relative_humidity_percent") val humidityPercent: Double,
    @Json(name = "precipitation_probability_percent") val rainProbabilityPercent: Double,
    @Json(name = "precipitation_mm") val precipitationMm: Double,
    @Json(name = "wind_speed_kmh") val windSpeedKmh: Double
)

@JsonClass(generateAdapter = true)
data class ApiWeatherAdvisoryResponse(
    @Json(name = "crop_id") val cropId: String,
    @Json(name = "purpose") val purpose: String,
    @Json(name = "suitability") val suitability: String,
    @Json(name = "weather") val weather: ApiWeatherSnapshot,
    @Json(name = "advisories") val advisories: List<ApiAdvisoryItem> = emptyList(),
    @Json(name = "disclaimer") val disclaimer: String = ""
)
