package com.example.data.repository

import com.example.data.model.NutrientRating
import com.example.data.model.SoilAnalysisRequest
import com.example.data.model.SoilAnalysisResult
import com.example.data.remote.ApiSoilAnalysisRequest
import com.example.data.remote.SmartAgricultureApi
import kotlinx.coroutines.delay
import java.util.UUID

interface SoilRepository {
    suspend fun analyzeSoil(request: SoilAnalysisRequest): Result<SoilAnalysisResult>
}

class MockSoilRepository : SoilRepository {
    override suspend fun analyzeSoil(request: SoilAnalysisRequest): Result<SoilAnalysisResult> {
        delay(800)

        // Evaluate Nitrogen
        val nStatus = when {
            request.nitrogen < 120.0 -> "Low / Deficient"
            request.nitrogen > 280.0 -> "Excessive"
            else -> "Optimal"
        }
        val nAdvice = when (nStatus) {
            "Low / Deficient" -> "Split dose of Urea (46% N) @ 50 kg/acre at first irrigation."
            "Excessive" -> "Withhold nitrogen applications to avoid vegetative lodging and high pest susceptibility."
            else -> "Nitrogen level is well-balanced for the current vegetative tillering phase."
        }

        // Evaluate Phosphorus
        val pStatus = when {
            request.phosphorus < 25.0 -> "Low / Deficient"
            request.phosphorus > 65.0 -> "High"
            else -> "Optimal"
        }
        val pAdvice = when (pStatus) {
            "Low / Deficient" -> "Apply DAP (Di-Ammonium Phosphate) @ 1 bag (50kg)/acre to stimulate strong root development."
            "High" -> "Adequate residual phosphorus; no additional phosphatic fertilizer needed this season."
            else -> "Root-zone phosphorus concentration is in the ideal productive bracket."
        }

        // Evaluate Potassium
        val kStatus = when {
            request.potassium < 150.0 -> "Low / Deficient"
            request.potassium > 300.0 -> "High"
            else -> "Optimal"
        }
        val kAdvice = when (kStatus) {
            "Low / Deficient" -> "Top-dress with SOP (Sulphate of Potash) @ 25 kg/acre to boost disease resistance and grain filling."
            else -> "Potassium reserves support cell wall turgor and drought resilience."
        }

        // pH Evaluation
        val phRating = when {
            request.ph < 6.0 -> "Acidic (pH ${request.ph})"
            request.ph > 7.8 -> "Moderately Alkaline (pH ${request.ph})"
            else -> "Optimal Field pH (${request.ph})"
        }
        val phAdvice = when {
            request.ph < 6.0 -> "Apply agricultural lime (calcium carbonate) @ 1.5 tons/acre to neutralize acidity."
            request.ph > 7.8 -> "Incorporate agricultural gypsum or elemental sulfur to decrease alkalinity and improve sodium displacement."
            else -> "Soil reaction is optimal; macro and micro-nutrients are readily bio-available."
        }

        // Moisture Evaluation
        val moistureRating = when {
            request.moisturePct < 15.0 -> "Low Moisture (${request.moisturePct}%)"
            request.moisturePct > 45.0 -> "Excess Moisture / Waterlogged (${request.moisturePct}%)"
            else -> "Adequate Field Capacity (${request.moisturePct}%)"
        }
        val moistureAdvice = when {
            request.moisturePct < 15.0 -> "Critical: Schedule irrigation within 24-48 hours to avert permanent wilting point."
            request.moisturePct > 45.0 -> "Improve field drainage furrows to prevent root hypoxia and phytophthora root rot."
            else -> "Soil moisture supports active nutrient mass flow and microbial activity."
        }

        val overallRating = when {
            nStatus == "Low / Deficient" || pStatus == "Low / Deficient" -> "Moderate Nutrient Deficiency"
            request.ph > 8.2 || request.ph < 5.5 -> "Severe pH Imbalance"
            else -> "Healthy & Fertile Soil"
        }

        val fertilizerPlan = mutableListOf<String>()
        if (nStatus == "Low / Deficient") fertilizerPlan.add("Urea (46-0-0): 50 kg/acre during next watering")
        if (pStatus == "Low / Deficient") fertilizerPlan.add("DAP (18-46-0): 50 kg/acre banded 5cm below seed furrow")
        if (kStatus == "Low / Deficient") fertilizerPlan.add("SOP (0-0-50): 25 kg/acre split at tillering")
        if (fertilizerPlan.isEmpty()) fertilizerPlan.add("Maintenance dose: 15 kg Urea + Micronutrient chelated zinc spray (Zabardast Zinc)")

        val organicAmendments = "Incorporate 3-4 trolley loads of well-decomposed Farmyard Manure (FYM) or green manure (Sesbania/Jantar) during post-harvest deep tillage to improve organic matter and cation exchange capacity."

        val cropName = request.cropId.replaceFirstChar { it.uppercase() }

        val result = SoilAnalysisResult(
            id = UUID.randomUUID().toString(),
            cropId = request.cropId,
            cropName = cropName,
            fieldName = request.fieldName.ifBlank { "Field Plot 4" },
            overallRating = overallRating,
            phRating = phRating,
            phAdvice = phAdvice,
            moistureRating = moistureRating,
            moistureAdvice = moistureAdvice,
            nutrients = listOf(
                NutrientRating(
                    nutrient = "Nitrogen (N)",
                    value = request.nitrogen,
                    unit = request.nitrogenUnit,
                    status = nStatus,
                    targetRange = "150 - 250 kg/ha",
                    advice = nAdvice
                ),
                NutrientRating(
                    nutrient = "Phosphorus (P)",
                    value = request.phosphorus,
                    unit = request.phosphorusUnit,
                    status = pStatus,
                    targetRange = "30 - 60 kg/ha",
                    advice = pAdvice
                ),
                NutrientRating(
                    nutrient = "Potassium (K)",
                    value = request.potassium,
                    unit = request.potassiumUnit,
                    status = kStatus,
                    targetRange = "160 - 280 kg/ha",
                    advice = kAdvice
                )
            ),
            fertilizerPlan = fertilizerPlan,
            organicAmendments = organicAmendments
        )

        return Result.success(result)
    }
}

class ApiSoilRepository(private val api: SmartAgricultureApi) : SoilRepository {
    override suspend fun analyzeSoil(request: SoilAnalysisRequest): Result<SoilAnalysisResult> {
        return try {
            val response = api.analyzeSoil(
                ApiSoilAnalysisRequest(
                    cropId = request.cropId,
                    nitrogen = request.nitrogen,
                    phosphorus = request.phosphorus,
                    potassium = request.potassium,
                    ph = request.ph,
                    moisture = request.moisturePct
                )
            )
            fun adviceFor(term: String): String = response.advisories
                .filter { it.title.contains(term, ignoreCase = true) }
                .joinToString(" ") { it.message }
                .ifBlank { "No $term warning was generated." }
            fun nutrient(name: String, value: Double, unit: String) = NutrientRating(
                nutrient = name,
                value = value,
                unit = unit,
                status = if (response.advisories.any { it.title.contains("low ${name.substringBefore(' ')}", ignoreCase = true) }) "Low / Deficient" else "Within configured range",
                targetRange = "See selected crop profile",
                advice = adviceFor(name.substringBefore(' '))
            )
            val cropName = when (request.cropId.lowercase()) { "corn" -> "Corn / Maize"; "potato" -> "Potato"; "rice" -> "Rice"; else -> "Wheat" }
            Result.success(
                SoilAnalysisResult(
                    id = response.id?.toString() ?: UUID.randomUUID().toString(),
                    cropId = response.cropId,
                    cropName = cropName,
                    fieldName = request.fieldName,
                    overallRating = response.suitability.replace("_", " ").replaceFirstChar { it.uppercase() },
                    phRating = "Entered pH ${request.ph}",
                    phAdvice = adviceFor("pH").ifBlank { response.disclaimer },
                    moistureRating = "Entered moisture ${request.moisturePct}%",
                    moistureAdvice = adviceFor("moisture"),
                    nutrients = listOf(
                        nutrient("Nitrogen (N)", request.nitrogen, request.nitrogenUnit),
                        nutrient("Phosphorus (P)", request.phosphorus, request.phosphorusUnit),
                        nutrient("Potassium (K)", request.potassium, request.potassiumUnit)
                    ),
                    fertilizerPlan = response.advisories.filter { it.title.contains("low", ignoreCase = true) }.map { it.message }.ifEmpty { listOf("No nutrient deficiency was flagged. Confirm with a laboratory soil test.") },
                    organicAmendments = response.disclaimer
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
