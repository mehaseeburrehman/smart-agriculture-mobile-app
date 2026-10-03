package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

enum class AdvisoryPurpose {
    GROWING,
    SOWING,
    SPRAYING
}

enum class AdvisoryStatus {
    SUITABLE,
    CAUTION,
    UNSUITABLE
}

@JsonClass(generateAdapter = true)
data class RiskFactor(
    @Json(name = "title") val title: String,
    @Json(name = "severity") val severity: String, // "Critical", "Caution", "Safe"
    @Json(name = "risk_tag") val riskTag: String, // "High Risk Alert", "Drift & Wash-off Risk", "Optimal Condition"
    @Json(name = "description") val description: String,
    @Json(name = "next_action") val nextAction: String
)

@JsonClass(generateAdapter = true)
data class WeatherAdvisory(
    @Json(name = "crop_id") val cropId: String,
    @Json(name = "crop_name") val cropName: String,
    @Json(name = "location_name") val locationName: String,
    @Json(name = "elevation_m") val elevationMeters: Int = 122,
    @Json(name = "stage_name") val stageName: String = "Tillering Stage",
    @Json(name = "purpose") val purpose: AdvisoryPurpose,
    @Json(name = "temperature_c") val temperatureC: Int,
    @Json(name = "humidity_pct") val humidityPct: Int,
    @Json(name = "rain_probability_pct") val rainProbabilityPct: Int,
    @Json(name = "precipitation_mm") val precipitationMm: Int,
    @Json(name = "wind_speed_kmh") val windSpeedKmh: Int,
    @Json(name = "status") val status: AdvisoryStatus,
    @Json(name = "verdict_title") val verdictTitle: String,
    @Json(name = "verdict_subtitle") val verdictSubtitle: String,
    @Json(name = "recommended_window") val recommendedWindow: String,
    @Json(name = "risk_factors") val riskFactors: List<RiskFactor>,
    @Json(name = "attribution") val attribution: String = "Weather data provided by Open-Meteo API",
    @Json(name = "last_updated_time") val lastUpdatedTime: String = "12 mins ago",
    @Json(name = "timestamp") val timestamp: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
data class WeatherAdvisoryRequest(
    @Json(name = "crop_id") val cropId: String,
    @Json(name = "latitude") val latitude: Double = 30.1575,
    @Json(name = "longitude") val longitude: Double = 71.5249,
    @Json(name = "purpose") val purpose: String = "SPRAYING"
)
