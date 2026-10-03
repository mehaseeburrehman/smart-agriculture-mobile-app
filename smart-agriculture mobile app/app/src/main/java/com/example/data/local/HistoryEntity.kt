package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.DiagnosisResult
import com.example.data.model.DiagnosisStatus

@Entity(tableName = "scan_records")
data class ScanRecordEntity(
    @PrimaryKey val id: String,
    val cropId: String,
    val cropName: String,
    val scientificName: String,
    val status: String,
    val diseaseName: String,
    val scientificPathogen: String,
    val confidencePct: Int,
    val confidenceLevel: String,
    val symptoms: String,
    val causes: String,
    val organicManagement: String,
    val chemicalIntervention: String,
    val prevention: String,
    val safetyWarning: String,
    val imageUri: String?,
    val fieldLocation: String,
    val timestamp: Long
) {
    fun toDiagnosisResult(): DiagnosisResult {
        return DiagnosisResult(
            id = id,
            cropId = cropId,
            cropName = cropName,
            scientificName = scientificName,
            status = try { DiagnosisStatus.valueOf(status) } catch (e: Exception) { DiagnosisStatus.DISEASE_DETECTED },
            diseaseName = diseaseName,
            scientificPathogen = scientificPathogen,
            confidence = confidencePct,
            confidenceLevel = confidenceLevel,
            symptoms = symptoms,
            causes = causes,
            organicManagement = organicManagement,
            chemicalIntervention = chemicalIntervention,
            prevention = prevention,
            safetyWarning = safetyWarning,
            imageUri = imageUri,
            fieldLocation = fieldLocation,
            timestamp = timestamp
        )
    }

    companion object {
        fun fromDiagnosisResult(result: DiagnosisResult): ScanRecordEntity {
            return ScanRecordEntity(
                id = result.id,
                cropId = result.cropId,
                cropName = result.cropName,
                scientificName = result.scientificName,
                status = result.status.name,
                diseaseName = result.diseaseName,
                scientificPathogen = result.scientificPathogen,
                confidencePct = result.confidence,
                confidenceLevel = result.confidenceLevel,
                symptoms = result.symptoms,
                causes = result.causes,
                organicManagement = result.organicManagement,
                chemicalIntervention = result.chemicalIntervention,
                prevention = result.prevention,
                safetyWarning = result.safetyWarning,
                imageUri = result.imageUri,
                fieldLocation = result.fieldLocation,
                timestamp = result.timestamp
            )
        }
    }
}

@Entity(tableName = "soil_records")
data class SoilRecordEntity(
    @PrimaryKey val id: String,
    val cropId: String,
    val cropName: String,
    val fieldName: String,
    val overallRating: String,
    val ph: Double,
    val moisturePct: Double,
    val nitrogen: Double,
    val phosphorus: Double,
    val potassium: Double,
    val fertilizerPlanSummary: String,
    val organicSummary: String,
    val timestamp: Long
)
