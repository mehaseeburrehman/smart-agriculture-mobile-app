package com.example.data.repository

import com.example.data.model.Crop
import com.example.data.remote.ApiCropDetails
import com.example.data.remote.ApiCropSummary
import com.example.data.remote.SmartAgricultureApi

interface CropRepository {
    suspend fun getCrops(): Result<List<Crop>>
    suspend fun getCropById(cropId: String): Result<Crop?>
}

class MockCropRepository : CropRepository {
    private val crops = listOf(
        Crop(
            id = "wheat",
            name = "Wheat",
            localName = "Gandum (گندم)",
            scientificName = "Triticum aestivum",
            variety = "Faisalabad-08 / Akbar-19",
            isModelActive = true,
            modelVersion = "Leaf Model v2.4 Active",
            defaultGrowthStage = "Tillering Stage",
            supportedDiseases = listOf("Wheat Stripe Rust", "Powdery Mildew", "Leaf Blight", "Loose Smut"),
            description = "Major cereal grain crop cultivated extensively across Punjab and Sindh. Susceptible to stripe rust under cool, humid overcast conditions.",
            optimalTempRange = "15-25°C",
            optimalPhRange = "6.0-7.5"
        ),
        Crop(
            id = "cotton",
            name = "Cotton",
            localName = "Kapas (کپاس)",
            scientificName = "Gossypium hirsutum",
            variety = "FH-142 / BS-15",
            isModelActive = true,
            modelVersion = "Leaf Model v1.8 Active",
            defaultGrowthStage = "Flowering & Boll Formation",
            supportedDiseases = listOf("Bacterial Blight", "Cotton Leaf Curl Virus (CLCuV)", "Alternaria Leaf Spot"),
            description = "Silver fiber cash crop of Pakistan. Highly sensitive to whitefly transmission and waterlogging in early vegetative cycles.",
            optimalTempRange = "25-35°C",
            optimalPhRange = "6.5-8.0"
        ),
        Crop(
            id = "rice",
            name = "Rice",
            localName = "Dhan (دھان)",
            scientificName = "Oryza sativa",
            variety = "Super Basmati / Kainat 1121",
            isModelActive = true,
            modelVersion = "Leaf Model v2.1 Active",
            defaultGrowthStage = "Panicle Initiation",
            supportedDiseases = listOf("Rice Blast (Magnaporthe)", "Brown Spot", "Bacterial Leaf Blight", "Sheath Rot"),
            description = "Aromatic staple crop demanding consistent water coverage and balanced nitrogen to prevent lodging and blast outbreaks.",
            optimalTempRange = "22-32°C",
            optimalPhRange = "5.5-7.0"
        ),
        Crop(
            id = "maize",
            name = "Maize",
            localName = "Makai (مکئی)",
            scientificName = "Zea mays",
            variety = "Pioneer 30Y87 / DK-6789",
            isModelActive = true,
            modelVersion = "Leaf Model v1.5 Active",
            defaultGrowthStage = "Knee-High Stage",
            supportedDiseases = listOf("Northern Corn Leaf Blight", "Common Rust", "Gray Leaf Spot"),
            description = "Fast-growing fodder and grain crop with high potassium and phosphorus uptake requirements.",
            optimalTempRange = "20-30°C",
            optimalPhRange = "5.8-7.2"
        ),
        Crop(
            id = "tomato",
            name = "Tomato",
            localName = "Tamatar (ٹماٹر)",
            scientificName = "Solanum lycopersicum",
            variety = "Roma / Sahil Hybrid",
            isModelActive = true,
            modelVersion = "Leaf Model v2.0 Active",
            defaultGrowthStage = "Fruiting Stage",
            supportedDiseases = listOf("Early Blight (Alternaria)", "Late Blight", "Tomato Yellow Leaf Curl", "Septoria Spot"),
            description = "High-value horticultural vegetable crop requiring strict humidity management to deter fungal foliage pathogens.",
            optimalTempRange = "18-28°C",
            optimalPhRange = "6.0-6.8"
        ),
        Crop(
            id = "potato",
            name = "Potato",
            localName = "Aloo (آلو)",
            scientificName = "Solanum tuberosum",
            variety = "Mozika / Kuroda",
            isModelActive = false,
            modelVersion = "Training v0.9 (Coming Soon)",
            defaultGrowthStage = "Tuber Bulking",
            supportedDiseases = listOf("Late Blight (Phytophthora)", "Early Blight", "Black Scurf"),
            description = "Tuber vegetable crop of central Punjab. Model training in progress for late blight detection.",
            optimalTempRange = "15-22°C",
            optimalPhRange = "5.2-6.5"
        ),
        Crop(
            id = "sugarcane",
            name = "Sugarcane",
            localName = "Kamad (کماد)",
            scientificName = "Saccharum officinarum",
            variety = "CPF-249 / HSF-240",
            isModelActive = true,
            modelVersion = "Leaf Model v1.2 Active",
            defaultGrowthStage = "Tillering & Grand Growth",
            supportedDiseases = listOf("Red Rot", "Sugarcane Smut", "Pokkah Boeng"),
            description = "Perennial cash crop with lengthy vegetative duration and massive biomass production.",
            optimalTempRange = "26-38°C",
            optimalPhRange = "6.0-7.8"
        ),
        Crop(
            id = "chili",
            name = "Chili",
            localName = "Mirch (مرچ)",
            scientificName = "Capsicum annuum",
            variety = "Ghotki Local / Sanam",
            isModelActive = true,
            modelVersion = "Leaf Model v1.4 Active",
            defaultGrowthStage = "Fruit Setting",
            supportedDiseases = listOf("Anthracnose Fruit Rot", "Chili Leaf Curl", "Cercospora Leaf Spot"),
            description = "Pungent spice crop grown mainly in lower Sindh and central Punjab districts.",
            optimalTempRange = "20-30°C",
            optimalPhRange = "6.0-7.0"
        )
    )

    override suspend fun getCrops(): Result<List<Crop>> {
        return Result.success(crops)
    }

    override suspend fun getCropById(cropId: String): Result<Crop?> {
        val crop = crops.find { it.id.equals(cropId, ignoreCase = true) }
        return Result.success(crop)
    }
}

class ApiCropRepository(private val api: SmartAgricultureApi) : CropRepository {
    override suspend fun getCrops(): Result<List<Crop>> {
        return try {
            Result.success(api.getCrops().map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCropById(cropId: String): Result<Crop?> {
        return try {
            Result.success(api.getCropDetails(cropId).toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

private fun ApiCropSummary.toDomain(): Crop {
    val metadata = cropMetadata(id)
    return Crop(
        id = id,
        name = name,
        localName = metadata.localName,
        scientificName = metadata.scientificName,
        isModelActive = diseaseModelReady,
        modelVersion = if (diseaseModelReady) "Shared Leaf Model Active" else "Model unavailable",
        supportedDiseases = metadata.diseases,
        description = metadata.description,
        optimalTempRange = metadata.temperature,
        optimalPhRange = metadata.ph
    )
}

private fun ApiCropDetails.toDomain(): Crop {
    val metadata = cropMetadata(id)
    val phText = if (soil?.phRange?.size == 2) "${soil.phRange[0]}-${soil.phRange[1]}" else metadata.ph
    return Crop(
        id = id,
        name = name,
        localName = metadata.localName,
        scientificName = scientificName.ifBlank { metadata.scientificName },
        isModelActive = true,
        modelVersion = "Shared Leaf Model Active",
        supportedDiseases = metadata.diseases,
        description = description.ifBlank { metadata.description },
        optimalTempRange = metadata.temperature,
        optimalPhRange = phText
    )
}

private data class CropMetadata(
    val localName: String,
    val scientificName: String,
    val diseases: List<String>,
    val description: String,
    val temperature: String,
    val ph: String
)

private fun cropMetadata(id: String): CropMetadata = when (id.lowercase()) {
    "corn" -> CropMetadata("Makai (مکئی)", "Zea mays", listOf("Common Rust", "Gray Leaf Spot"), "Maize leaf disease screening.", "18-30°C", "5.8-7.0")
    "potato" -> CropMetadata("Aloo (آلو)", "Solanum tuberosum", listOf("Early Blight", "Late Blight"), "Potato leaf disease screening.", "15-24°C", "5.0-6.5")
    "rice" -> CropMetadata("Dhan (دھان)", "Oryza sativa", listOf("Brown Spot", "Leaf Blast"), "Rice leaf disease screening.", "20-35°C", "5.5-7.0")
    else -> CropMetadata("Gandum (گندم)", "Triticum aestivum", listOf("Brown Rust", "Yellow Rust"), "Wheat leaf disease screening.", "10-25°C", "6.0-7.5")
}
