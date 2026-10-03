package com.example.data.repository

import com.example.data.model.AdvisoryPurpose
import com.example.data.model.AdvisoryStatus
import com.example.data.model.RiskFactor
import com.example.data.model.WeatherAdvisory
import com.example.data.remote.ApiLiveWeatherRequest
import com.example.data.remote.SmartAgricultureApi
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

interface WeatherRepository {
    suspend fun getWeatherAdvisory(
        cropId: String,
        purpose: AdvisoryPurpose,
        latitude: Double = 30.1575,
        longitude: Double = 71.5249
    ): Result<WeatherAdvisory>
}

class MockWeatherRepository : WeatherRepository {
    override suspend fun getWeatherAdvisory(
        cropId: String,
        purpose: AdvisoryPurpose,
        latitude: Double,
        longitude: Double
    ): Result<WeatherAdvisory> {
        delay(600)

        val cropName = when (cropId.lowercase()) {
            "cotton" -> "Cotton"
            "rice" -> "Rice"
            "tomato" -> "Tomato"
            "maize" -> "Maize"
            else -> "Wheat"
        }

        val advisory = when (purpose) {
            AdvisoryPurpose.SPRAYING -> WeatherAdvisory(
                cropId = cropId,
                cropName = cropName,
                locationName = "Multan District",
                elevationMeters = 122,
                stageName = "Tillering Stage",
                purpose = AdvisoryPurpose.SPRAYING,
                temperatureC = 29,
                humidityPct = 78,
                rainProbabilityPct = 65,
                precipitationMm = 12,
                windSpeedKmh = 18,
                status = AdvisoryStatus.CAUTION,
                verdictTitle = "CAUTION FOR SPRAYING",
                verdictSubtitle = "Sub-optimal Window",
                recommendedWindow = "Tomorrow 06:30 AM - 10:00 AM",
                riskFactors = listOf(
                    RiskFactor(
                        title = "Fungal Disease Conditions",
                        severity = "Critical",
                        riskTag = "High Risk Alert",
                        description = "High humidity (>75%) and moderate temperatures create an ideal spore germination environment for stripe rust and powdery mildew across $cropName leaf sheaths.",
                        nextAction = "Inspect under-canopy leaves within 24 hours. Prepare preventive fungicidal bio-control."
                    ),
                    RiskFactor(
                        title = "Unsafe Spraying Conditions",
                        severity = "Caution",
                        riskTag = "Drift & Wash-off Risk",
                        description = "Wind gusts at 18 km/h and rain probability at 65% in next 6 hours will cause spray wash-off and non-target chemical drift onto neighboring borders.",
                        nextAction = "Postpone foliar spraying until tomorrow morning when wind speeds drop below 10 km/h."
                    ),
                    RiskFactor(
                        title = "Heat Stress",
                        severity = "Safe",
                        riskTag = "Optimal Condition",
                        description = "Temperatures remain within healthy vegetative growth window (20-30°C). No risk of pollen sterility or canopy heat scorch observed for current cycle.",
                        nextAction = "Metabolic rate normal • No shading or emergency canopy cooling required."
                    )
                ),
                attribution = "Weather data provided by Open-Meteo API",
                lastUpdatedTime = "12 mins ago"
            )
            AdvisoryPurpose.GROWING -> WeatherAdvisory(
                cropId = cropId,
                cropName = cropName,
                locationName = "Multan District",
                elevationMeters = 122,
                stageName = "Tillering Stage",
                purpose = AdvisoryPurpose.GROWING,
                temperatureC = 28,
                humidityPct = 62,
                rainProbabilityPct = 20,
                precipitationMm = 2,
                windSpeedKmh = 11,
                status = AdvisoryStatus.SUITABLE,
                verdictTitle = "OPTIMAL FOR VEGETATIVE GROWTH",
                verdictSubtitle = "Healthy Growth Stage",
                recommendedWindow = "Full Sunshine Day: Good Photosynthesis",
                riskFactors = listOf(
                    RiskFactor(
                        title = "Thermal Balance",
                        severity = "Safe",
                        riskTag = "Vigorous Biomass",
                        description = "Day/night thermal delta promotes starch accumulation and deep root establishment in $cropName root zones.",
                        nextAction = "Maintain standard secondary nitrogen application according to soil testing logs."
                    ),
                    RiskFactor(
                        title = "Transpiration Rate",
                        severity = "Safe",
                        riskTag = "Normal Water Uptake",
                        description = "Atmospheric vapor pressure deficit (VPD) is balanced. Stomata remain open for optimal carbon assimilation.",
                        nextAction = "Irrigate within 3 days to preserve root zone field capacity."
                    )
                ),
                attribution = "Weather data provided by Open-Meteo API",
                lastUpdatedTime = "Just now"
            )
            AdvisoryPurpose.SOWING -> WeatherAdvisory(
                cropId = cropId,
                cropName = cropName,
                locationName = "Multan District",
                elevationMeters = 122,
                stageName = "Seedbed Preparation",
                purpose = AdvisoryPurpose.SOWING,
                temperatureC = 27,
                humidityPct = 68,
                rainProbabilityPct = 40,
                precipitationMm = 6,
                windSpeedKmh = 14,
                status = AdvisoryStatus.CAUTION,
                verdictTitle = "CHECK SOIL SEEDBED MOISTURE",
                verdictSubtitle = "Intermittent Showers Possible",
                recommendedWindow = "Sow after topsoil dries: Day After Tomorrow",
                riskFactors = listOf(
                    RiskFactor(
                        title = "Seedbed Crusting Risk",
                        severity = "Caution",
                        riskTag = "Hardpan Prevention",
                        description = "Light rain on freshly tilled seedbeds may cause crust formation, hindering fragile seedling coleoptile emergence.",
                        nextAction = "Delay precision seed drilling until soil moisture stabilizes to prevent seed decay."
                    )
                ),
                attribution = "Weather data provided by Open-Meteo API",
                lastUpdatedTime = "5 mins ago"
            )
        }

        return Result.success(advisory)
    }
}

class ApiWeatherRepository(private val api: SmartAgricultureApi) : WeatherRepository {
    override suspend fun getWeatherAdvisory(
        cropId: String,
        purpose: AdvisoryPurpose,
        latitude: Double,
        longitude: Double
    ): Result<WeatherAdvisory> {
        return try {
            val response = api.getLiveWeatherAdvisory(
                ApiLiveWeatherRequest(
                    cropId = cropId,
                    latitude = latitude,
                    longitude = longitude,
                    purpose = purpose.name.lowercase()
                )
            )
            val cropName = when (cropId.lowercase()) {
                "corn" -> "Corn / Maize"
                "potato" -> "Potato"
                "rice" -> "Rice"
                else -> "Wheat"
            }
            val status = when (response.suitability) {
                "suitable" -> AdvisoryStatus.SUITABLE
                "caution" -> AdvisoryStatus.CAUTION
                else -> AdvisoryStatus.UNSUITABLE
            }
            Result.success(
                WeatherAdvisory(
                    cropId = cropId,
                    cropName = cropName,
                    locationName = "Selected GPS location",
                    purpose = purpose,
                    temperatureC = response.weather.temperatureC.roundToInt(),
                    humidityPct = response.weather.humidityPercent.roundToInt(),
                    rainProbabilityPct = response.weather.rainProbabilityPercent.roundToInt(),
                    precipitationMm = response.weather.precipitationMm.roundToInt(),
                    windSpeedKmh = response.weather.windSpeedKmh.roundToInt(),
                    status = status,
                    verdictTitle = when (status) {
                        AdvisoryStatus.SUITABLE -> "SUITABLE CONDITIONS"
                        AdvisoryStatus.CAUTION -> "PROCEED WITH CAUTION"
                        AdvisoryStatus.UNSUITABLE -> "ACTION NOT ADVISED"
                    },
                    verdictSubtitle = response.purpose.replaceFirstChar { it.uppercase() } + " weather assessment",
                    recommendedWindow = response.advisories.firstOrNull()?.message ?: response.disclaimer,
                    riskFactors = response.advisories.map { item ->
                        RiskFactor(
                            title = item.title,
                            severity = when (item.level) { "high" -> "Critical"; "moderate" -> "Caution"; else -> "Safe" },
                            riskTag = item.level.replaceFirstChar { it.uppercase() } + " risk",
                            description = item.message,
                            nextAction = if (item.level == "low") "Continue routine monitoring." else "Review field conditions before proceeding."
                        )
                    },
                    lastUpdatedTime = "Live via Open-Meteo"
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
