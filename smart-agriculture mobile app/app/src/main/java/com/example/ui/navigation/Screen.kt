package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Scan : Screen("scan")
    object Advisories : Screen("advisories")
    object History : Screen("history")

    object ImagePreview : Screen("image_preview")
    object Analyzing : Screen("analyzing")
    object DiagnosisDetail : Screen("diagnosis_detail/{scanId}") {
        fun createRoute(scanId: String) = "diagnosis_detail/$scanId"
    }
    object LowConfidence : Screen("low_confidence/{cropId}") {
        fun createRoute(cropId: String) = "low_confidence/$cropId"
    }
    object SoilAnalysis : Screen("soil_analysis")
    object SoilResult : Screen("soil_result/{soilId}") {
        fun createRoute(soilId: String) = "soil_result/$soilId"
    }
    object CropDetail : Screen("crop_detail/{cropId}") {
        fun createRoute(cropId: String) = "crop_detail/$cropId"
    }
    object Settings : Screen("settings")
}
