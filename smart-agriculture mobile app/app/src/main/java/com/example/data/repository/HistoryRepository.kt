package com.example.data.repository

import com.example.data.local.HistoryDao
import com.example.data.local.ScanRecordEntity
import com.example.data.local.SoilRecordEntity
import com.example.data.model.DiagnosisResult
import com.example.data.model.DiagnosisStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

interface HistoryRepository {
    fun getScanRecords(): Flow<List<DiagnosisResult>>
    fun getSoilRecords(): Flow<List<SoilRecordEntity>>
    suspend fun saveScanRecord(result: DiagnosisResult)
    suspend fun deleteScanRecord(id: String)
    suspend fun saveSoilRecord(record: SoilRecordEntity)
    suspend fun deleteSoilRecord(record: SoilRecordEntity)
    suspend fun seedInitialDataIfEmpty()
    suspend fun clearAll()
}

class HistoryRepositoryImpl(private val dao: HistoryDao) : HistoryRepository {
    override fun getScanRecords(): Flow<List<DiagnosisResult>> {
        return dao.getAllScanRecords().map { entities ->
            entities.map { it.toDiagnosisResult() }
        }
    }

    override fun getSoilRecords(): Flow<List<SoilRecordEntity>> {
        return dao.getAllSoilRecords()
    }

    override suspend fun saveScanRecord(result: DiagnosisResult) {
        dao.insertScanRecord(ScanRecordEntity.fromDiagnosisResult(result))
    }

    override suspend fun deleteScanRecord(id: String) {
        val record = dao.getScanRecordById(id)
        if (record != null) {
            dao.deleteScanRecord(record)
        }
    }

    override suspend fun saveSoilRecord(record: SoilRecordEntity) {
        dao.insertSoilRecord(record)
    }

    override suspend fun deleteSoilRecord(record: SoilRecordEntity) {
        dao.deleteSoilRecord(record)
    }

    override suspend fun clearAll() {
        dao.clearScanRecords()
        dao.clearSoilRecords()
    }

    override suspend fun seedInitialDataIfEmpty() {
        val now = System.currentTimeMillis()
        val twoHoursAgo = now - 2 * 3600 * 1000
        val oneDayAgo = now - 24 * 3600 * 1000
        val threeDaysAgo = now - 3 * 24 * 3600 * 1000

        // Seed 3 realistic historical scans matching Stitch Home UI
        val scan1 = ScanRecordEntity(
            id = "seed-scan-1",
            cropId = "wheat",
            cropName = "Wheat",
            scientificName = "Triticum aestivum",
            status = DiagnosisStatus.DISEASE_DETECTED.name,
            diseaseName = "Yellow Rust (Stripe Rust)",
            scientificPathogen = "Puccinia striiformis",
            confidencePct = 94,
            confidenceLevel = "High Match",
            symptoms = "Yellow to orange-yellow pustules arranged in linear stripes on leaf surfaces.",
            causes = "Prolonged leaf moisture and cool night temperatures (10-15°C).",
            organicManagement = "Remove early infected foci and apply botanical neem oil formulation.",
            chemicalIntervention = "Propiconazole 25% EC @ 200ml/acre or Tebuconazole within 24h.",
            prevention = "Use certified rust-tolerant varieties and balanced nitrogen.",
            safetyWarning = "Wear PPE and verify withholding intervals before spraying.",
            imageUri = null,
            fieldLocation = "Wheat Field A",
            timestamp = twoHoursAgo
        )

        val scan2 = ScanRecordEntity(
            id = "seed-scan-2",
            cropId = "cotton",
            cropName = "Cotton",
            scientificName = "Gossypium hirsutum",
            status = DiagnosisStatus.DISEASE_DETECTED.name,
            diseaseName = "Bacterial Blight",
            scientificPathogen = "Xanthomonas citri pv. malvacearum",
            confidencePct = 88,
            confidenceLevel = "High Match",
            symptoms = "Angular water-soaked brown leaf spots bounded by veinlets.",
            causes = "Warm ambient temperatures with persistent high humidity and wind-splashing.",
            organicManagement = "Crop residue shredding and spray of copper hydroxide bio-formula.",
            chemicalIntervention = "Copper oxychloride preventive suggested (2.5 g/L).",
            prevention = "Acid-delint seed before sowing; rotate fields away from previous malvaceous crop residues.",
            safetyWarning = "Avoid spraying copper in hot midday sun.",
            imageUri = null,
            fieldLocation = "Cotton Plot 4",
            timestamp = oneDayAgo
        )

        val scan3 = ScanRecordEntity(
            id = "seed-scan-3",
            cropId = "tomato",
            cropName = "Tomato",
            scientificName = "Solanum lycopersicum",
            status = DiagnosisStatus.HEALTHY.name,
            diseaseName = "Healthy Foliage",
            scientificPathogen = "No active pathogen detected",
            confidencePct = 98,
            confidenceLevel = "High Match",
            symptoms = "Normal vegetative growth cycle observed. Intact cuticle.",
            causes = "Adequate morning sun and balanced micro-irrigation.",
            organicManagement = "Maintain standard organic mulch.",
            chemicalIntervention = "No chemical intervention needed.",
            prevention = "Continue weekly scouting.",
            safetyWarning = "Maintain scouting logs.",
            imageUri = null,
            fieldLocation = "Tomato Greenhouse",
            timestamp = threeDaysAgo
        )

        dao.insertScanRecord(scan1)
        dao.insertScanRecord(scan2)
        dao.insertScanRecord(scan3)

        // Seed 1 initial soil record
        val soil1 = SoilRecordEntity(
            id = "seed-soil-1",
            cropId = "wheat",
            cropName = "Wheat",
            fieldName = "Multan Sector 2B",
            overallRating = "Moderate Nutrient Deficiency",
            ph = 7.8,
            moisturePct = 22.0,
            nitrogen = 110.0,
            phosphorus = 22.0,
            potassium = 210.0,
            fertilizerPlanSummary = "1 bag Urea + 1 bag DAP recommended at 1st watering",
            organicSummary = "Add 3 trolley loads Farmyard Manure post harvest",
            timestamp = twoHoursAgo
        )
        dao.insertSoilRecord(soil1)
    }
}
