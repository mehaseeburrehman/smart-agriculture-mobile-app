package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SoilAnalysisRequest(
    @Json(name = "crop_id") val cropId: String,
    @Json(name = "nitrogen") val nitrogen: Double,
    @Json(name = "nitrogen_unit") val nitrogenUnit: String = "kg/ha",
    @Json(name = "phosphorus") val phosphorus: Double,
    @Json(name = "phosphorus_unit") val phosphorusUnit: String = "kg/ha",
    @Json(name = "potassium") val potassium: Double,
    @Json(name = "potassium_unit") val potassiumUnit: String = "kg/ha",
    @Json(name = "ph") val ph: Double,
    @Json(name = "moisture_pct") val moisturePct: Double,
    @Json(name = "field_name") val fieldName: String = "Plot Sector 4"
)

@JsonClass(generateAdapter = true)
data class NutrientRating(
    @Json(name = "nutrient") val nutrient: String,
    @Json(name = "value") val value: Double,
    @Json(name = "unit") val unit: String,
    @Json(name = "status") val status: String, // "Optimal", "Low / Deficient", "Excessive"
    @Json(name = "target_range") val targetRange: String,
    @Json(name = "advice") val advice: String
)

@JsonClass(generateAdapter = true)
data class SoilAnalysisResult(
    @Json(name = "id") val id: String,
    @Json(name = "crop_id") val cropId: String,
    @Json(name = "crop_name") val cropName: String,
    @Json(name = "field_name") val fieldName: String,
    @Json(name = "overall_rating") val overallRating: String, // "Healthy Soil", "Moderate Deficiency", "Severely Imbalanced"
    @Json(name = "ph_rating") val phRating: String, // "Optimal (6.8)", "Acidic", "Alkaline"
    @Json(name = "ph_advice") val phAdvice: String,
    @Json(name = "moisture_rating") val moistureRating: String, // "Adequate (22%)", "Dry", "Waterlogged"
    @Json(name = "moisture_advice") val moistureAdvice: String,
    @Json(name = "nutrients") val nutrients: List<NutrientRating>,
    @Json(name = "fertilizer_plan") val fertilizerPlan: List<String>,
    @Json(name = "organic_amendments") val organicAmendments: String,
    @Json(name = "timestamp") val timestamp: Long = System.currentTimeMillis()
)
