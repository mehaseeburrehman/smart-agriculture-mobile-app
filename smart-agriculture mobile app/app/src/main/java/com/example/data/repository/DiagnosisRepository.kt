package com.example.data.repository

import android.content.Context
import android.net.Uri
import com.example.data.model.DiagnosisResult
import com.example.data.model.DiagnosisStatus
import com.example.data.remote.SmartAgricultureApi
import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

interface DiagnosisRepository {
    suspend fun diagnoseLeaf(
        imageUri: String,
        cropId: String,
        fieldLocation: String = "Field Block 3",
        simulateLowConfidence: Boolean = false
    ): Result<DiagnosisResult>
}

class MockDiagnosisRepository : DiagnosisRepository {
    override suspend fun diagnoseLeaf(
        imageUri: String,
        cropId: String,
        fieldLocation: String,
        simulateLowConfidence: Boolean
    ): Result<DiagnosisResult> {
        // Simulate realistic network / model inference latency
        delay(1600)

        if (simulateLowConfidence) {
            return Result.success(
                DiagnosisResult(
                    id = UUID.randomUUID().toString(),
                    cropId = cropId,
                    cropName = cropId.replaceFirstChar { it.uppercase() },
                    scientificName = "Botanical specimen",
                    status = DiagnosisStatus.UNCERTAIN,
                    diseaseName = "Unidentified Visual Lesion Pattern",
                    scientificPathogen = "Inconclusive field sample",
                    confidence = 44,
                    confidenceLevel = "Low Match",
                    symptoms = "Diffuse chlorosis with non-specific spotting. Pattern does not cleanly align with single primary pathogen diagnostic models.",
                    causes = "Possible mechanical abrasion, nutritional burn, or early stage dual infection.",
                    organicManagement = "Avoid foliar spray until conclusive identification. Maintain balanced soil moisture.",
                    chemicalIntervention = "No chemical intervention recommended at this confidence level.",
                    prevention = "Resample leaf from newly emerged upper foliage under clear morning light.",
                    safetyWarning = "Do not apply systemic fungicides without confirming pathogen presence.",
                    imageUri = imageUri,
                    fieldLocation = fieldLocation,
                    timestamp = System.currentTimeMillis()
                )
            )
        }

        val result = when (cropId.lowercase()) {
            "wheat" -> DiagnosisResult(
                id = UUID.randomUUID().toString(),
                cropId = "wheat",
                cropName = "Wheat",
                scientificName = "Triticum aestivum",
                status = DiagnosisStatus.DISEASE_DETECTED,
                diseaseName = "Wheat Stripe Rust",
                scientificPathogen = "(Yellow Rust) / Puccinia striiformis",
                confidence = 89,
                confidenceLevel = "High Match",
                symptoms = "Yellow to orange-yellow pustules arranged in linear stripes on leaf surfaces, stunted growth, and premature chlorosis of foliage in dense canopies.",
                causes = "Prolonged leaf moisture, cool night temperatures (10-15°C) followed by humid overcast days with restricted airflow.",
                organicManagement = "Destroy volunteer plants, remove early infected foci immediately, and apply botanical neem oil extract formulations during early symptom stages.",
                chemicalIntervention = "Recommended triazole-based fungicides (Propiconazole 25% EC @ 200ml/acre or Tebuconazole). Follow manufacturer dosing precisely and monitor withholding periods.",
                prevention = "Plant rust-resistant certified seed varieties (e.g. Akbar-19, Dilkash-20). Maintain balanced nitrogen and avoid excessive dense planting.",
                safetyWarning = "Always check locally approved chemical labels, wear personal protective equipment (PPE), and consult your local agricultural extension officer before application.",
                imageUri = imageUri,
                fieldLocation = fieldLocation,
                timestamp = System.currentTimeMillis()
            )
            "cotton" -> DiagnosisResult(
                id = UUID.randomUUID().toString(),
                cropId = "cotton",
                cropName = "Cotton",
                scientificName = "Gossypium hirsutum",
                status = DiagnosisStatus.DISEASE_DETECTED,
                diseaseName = "Bacterial Blight",
                scientificPathogen = "Angular Leaf Spot / Xanthomonas citri pv. malvacearum",
                confidence = 88,
                confidenceLevel = "High Match",
                symptoms = "Angular, water-soaked lesions bounded by leaf veinlets on underside of leaves, turning dark reddish-brown to black, with occasional seedling damping-off.",
                causes = "Warm ambient temperatures (28-34°C) with persistent high humidity and wind-driven rainfall splashing bacteria.",
                organicManagement = "Seed treatment with hot water (56°C for 10 min), crop residue shredding post-harvest, and spray of copper hydroxide bio-formulations.",
                chemicalIntervention = "Foliar spray with Copper Oxychloride 50% WP (2.5 g/L) mixed with Streptomycin sulphate (100 ppm) at early symptom emergence.",
                prevention = "Acid-delint seed before sowing; rotate fields away from previous malvaceous crop residues.",
                safetyWarning = "Copper sprays can cause phytotoxicity if applied during hot midday sun. Apply strictly in calm early morning hours.",
                imageUri = imageUri,
                fieldLocation = fieldLocation,
                timestamp = System.currentTimeMillis()
            )
            "rice" -> DiagnosisResult(
                id = UUID.randomUUID().toString(),
                cropId = "rice",
                cropName = "Rice",
                scientificName = "Oryza sativa",
                status = DiagnosisStatus.DISEASE_DETECTED,
                diseaseName = "Rice Blast",
                scientificPathogen = "Magnaporthe oryzae",
                confidence = 87,
                confidenceLevel = "High Match",
                symptoms = "Spindle-shaped lesions with grayish-white centers and distinct reddish-brown borders appearing on leaf blades and leaf collars.",
                causes = "High relative humidity (>90%), extended dew periods, and heavy split applications of chemical nitrogen fertilizer.",
                organicManagement = "Spray diluted fermented cow urine or Pseudomonas fluorescens bio-agent. Maintain continuous shallow water flooding.",
                chemicalIntervention = "Apply Tricyclazole 75% WP @ 120 g/acre or Isoprothiolane 40% EC @ 300 ml/acre at symptom initiation.",
                prevention = "Avoid excessive urea applications; use silicon soil amendments and certified blast-resistant seed lots.",
                safetyWarning = "Ensure 21-day pre-harvest interval (PHI) for all systemic blast fungicides.",
                imageUri = imageUri,
                fieldLocation = fieldLocation,
                timestamp = System.currentTimeMillis()
            )
            else -> DiagnosisResult(
                id = UUID.randomUUID().toString(),
                cropId = cropId,
                cropName = cropId.replaceFirstChar { it.uppercase() },
                scientificName = "Cultivated Flora",
                status = DiagnosisStatus.HEALTHY,
                diseaseName = "Healthy Foliage",
                scientificPathogen = "No active pathogen detected",
                confidence = 96,
                confidenceLevel = "High Match",
                symptoms = "Uniform vibrant leaf coloration, intact cuticle, and normal vein expansion without visible chlorotic lesions or fungal pustules.",
                causes = "Optimal photosynthetic balance, balanced macro-nutrients, and adequate microclimate ventilation.",
                organicManagement = "Continue standard compost mulching and scheduled irrigation cycles.",
                chemicalIntervention = "No chemical intervention needed. Avoid prophylactic chemical sprays.",
                prevention = "Continue routine weekly field monitoring and check under-canopy leaf surfaces after rainfall.",
                safetyWarning = "Maintain scouting records to detect any emerging pest vector populations early.",
                imageUri = imageUri,
                fieldLocation = fieldLocation,
                timestamp = System.currentTimeMillis()
            )
        }

        return Result.success(result)
    }
}

class ApiDiagnosisRepository(
    private val api: SmartAgricultureApi,
    private val context: Context
) : DiagnosisRepository {
    override suspend fun diagnoseLeaf(
        imageUri: String,
        cropId: String,
        fieldLocation: String,
        simulateLowConfidence: Boolean
    ): Result<DiagnosisResult> {
        return try {
            val uri = Uri.parse(imageUri)
            val file = getFileFromUri(uri)

            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)
            val cropIdPart = cropId.toRequestBody("text/plain".toMediaTypeOrNull())
            val response = api.predictDisease(imagePart, cropIdPart)
            val confidencePct = (response.confidence * 100).toInt().coerceIn(0, 100)
            val guidance = response.guidance
            val status = when (response.status.lowercase()) {
                "healthy" -> DiagnosisStatus.HEALTHY
                "disease_detected" -> DiagnosisStatus.DISEASE_DETECTED
                else -> DiagnosisStatus.UNCERTAIN
            }
            val cropName = when (response.cropId.lowercase()) {
                "corn" -> "Corn / Maize"
                "potato" -> "Potato"
                "rice" -> "Rice"
                else -> "Wheat"
            }
            val scientificName = when (response.cropId.lowercase()) {
                "corn" -> "Zea mays"
                "potato" -> "Solanum tuberosum"
                "rice" -> "Oryza sativa"
                else -> "Triticum aestivum"
            }
            Result.success(
                DiagnosisResult(
                    id = response.id?.toString() ?: UUID.randomUUID().toString(),
                    cropId = response.cropId,
                    cropName = cropName,
                    scientificName = scientificName,
                    status = status,
                    diseaseName = response.diseaseName,
                    scientificPathogen = response.predictedLabel.replace("___", " / ").replace("_", " "),
                    confidence = confidencePct,
                    confidenceLevel = when {
                        confidencePct >= 85 -> "High Match"
                        confidencePct >= 70 -> "Moderate Match"
                        else -> "Low Match"
                    },
                    symptoms = guidance?.symptoms.toDisplayText("No supported symptoms were returned."),
                    causes = guidance?.possibleCauses.toDisplayText("The model result needs local confirmation."),
                    organicManagement = guidance?.organicManagement.toDisplayText("Consult a local agricultural expert."),
                    chemicalIntervention = guidance?.chemicalManagement.toDisplayText("No chemical recommendation is available for this result."),
                    prevention = guidance?.prevention.toDisplayText("Continue regular crop scouting."),
                    safetyWarning = response.message + " Always follow locally approved labels and extension advice.",
                    imageUri = imageUri,
                    fieldLocation = fieldLocation,
                    timestamp = System.currentTimeMillis()
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getFileFromUri(uri: Uri): File {
        val file = File(context.cacheDir, "upload_leaf_${System.currentTimeMillis()}.jpg")
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        return file
    }
}

private fun List<String>?.toDisplayText(fallback: String): String =
    if (this.isNullOrEmpty()) fallback else joinToString(separator = "\n• ", prefix = "• ")
