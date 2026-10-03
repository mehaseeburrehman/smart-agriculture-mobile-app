package com.example.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.components.AppBottomNavBar
import com.example.ui.screens.crops.CropDetailScreen
import com.example.ui.screens.crops.CropsViewModel
import com.example.ui.screens.diagnosis.DiagnosisResultScreen
import com.example.ui.screens.diagnosis.LowConfidenceScreen
import com.example.ui.screens.history.HistoryScreen
import com.example.ui.screens.history.HistoryViewModel
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.home.HomeViewModel
import com.example.ui.screens.scan.AnalyzingScreen
import com.example.ui.screens.scan.CameraScanScreen
import com.example.ui.screens.scan.ImagePreviewScreen
import com.example.ui.screens.scan.ScanViewModel
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.settings.SettingsViewModel
import com.example.ui.screens.soil.SoilAnalysisScreen
import com.example.ui.screens.soil.SoilResultScreen
import com.example.ui.screens.soil.SoilViewModel
import com.example.ui.screens.splash.SplashScreen
import com.example.ui.screens.weather.WeatherAdvisoryScreen
import com.example.ui.screens.weather.WeatherViewModel

@Composable
fun SmartAgricultureApp(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarRoutes = listOf(
        Screen.Home.route,
        Screen.Scan.route,
        Screen.Advisories.route,
        Screen.History.route
    )
    val shouldShowBottomBar = currentRoute in bottomBarRoutes

    // Shared ViewModels
    val homeViewModel: HomeViewModel = viewModel()
    val scanViewModel: ScanViewModel = viewModel()
    val weatherViewModel: WeatherViewModel = viewModel()
    val cropsViewModel: CropsViewModel = viewModel()
    val historyViewModel: HistoryViewModel = viewModel()
    val soilViewModel: SoilViewModel = viewModel()
    val settingsViewModel: SettingsViewModel = viewModel()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (shouldShowBottomBar) {
                AppBottomNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        if (currentRoute != route) {
                            navController.navigate(route) {
                                popUpTo(Screen.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    onSplashFinished = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = homeViewModel,
                    onStartScan = { navController.navigate(Screen.Scan.route) },
                    onNavigateToAdvisories = { navController.navigate(Screen.Advisories.route) },
                    onNavigateToSoil = { navController.navigate(Screen.SoilAnalysis.route) },
                    onNavigateToCropGuide = {
                        val activeCropId = homeViewModel.preferences.getSelectedCropId()
                        navController.navigate(Screen.CropDetail.createRoute(activeCropId))
                    },
                    onNavigateToHistory = { navController.navigate(Screen.History.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    onScanClick = { scanId ->
                        navController.navigate(Screen.DiagnosisDetail.createRoute(scanId))
                    }
                )
            }

            composable(Screen.Scan.route) {
                CameraScanScreen(
                    viewModel = scanViewModel,
                    onImageCaptured = {
                        navController.navigate(Screen.ImagePreview.route)
                    },
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    onNavigateToHistory = { navController.navigate(Screen.History.route) }
                )
            }

            composable(Screen.ImagePreview.route) {
                ImagePreviewScreen(
                    viewModel = scanViewModel,
                    onRetakeClick = { navController.popBackStack() },
                    onAnalyzeClick = { navController.navigate(Screen.Analyzing.route) }
                )
            }

            composable(Screen.Analyzing.route) {
                AnalyzingScreen(
                    viewModel = scanViewModel,
                    onSuccess = { result ->
                        navController.navigate(Screen.DiagnosisDetail.createRoute(result.id)) {
                            popUpTo(Screen.Scan.route)
                        }
                    },
                    onLowConfidence = { result ->
                        navController.navigate(Screen.LowConfidence.createRoute(result.cropId)) {
                            popUpTo(Screen.Scan.route)
                        }
                    }
                )
            }

            composable(
                route = Screen.DiagnosisDetail.route,
                arguments = listOf(navArgument("scanId") { type = NavType.StringType })
            ) { backStackEntry ->
                val scanId = backStackEntry.arguments?.getString("scanId") ?: ""
                val scans by historyViewModel.uiState.collectAsState()
                val result = scanViewModel.latestResult.value?.takeIf { it.id == scanId }
                    ?: scans.scanRecords.find { it.id == scanId }
                    ?: scanViewModel.latestResult.value

                if (result != null) {
                    DiagnosisResultScreen(
                        result = result,
                        onSaveResult = {
                            scanViewModel.saveToFieldRecord(result)
                        },
                        onViewWeatherRisks = {
                            navController.navigate(Screen.Advisories.route)
                        },
                        onScanAnotherLeaf = {
                            scanViewModel.resetAnalysis()
                            navController.navigate(Screen.Scan.route) {
                                popUpTo(Screen.Home.route)
                            }
                        },
                        onBackClick = { navController.popBackStack() },
                        onSettingsClick = { navController.navigate(Screen.Settings.route) }
                    )
                } else {
                    navController.popBackStack()
                }
            }

            composable(
                route = Screen.LowConfidence.route,
                arguments = listOf(navArgument("cropId") { type = NavType.StringType })
            ) {
                val result = scanViewModel.latestResult.value
                if (result != null) {
                    LowConfidenceScreen(
                        result = result,
                        onRetakeClick = {
                            scanViewModel.resetAnalysis()
                            navController.navigate(Screen.Scan.route) {
                                popUpTo(Screen.Home.route)
                            }
                        },
                        onConsultExtensionClick = {
                            navController.navigate(Screen.Advisories.route)
                        },
                        onBackClick = { navController.popBackStack() }
                    )
                } else {
                    navController.popBackStack()
                }
            }

            composable(Screen.Advisories.route) {
                WeatherAdvisoryScreen(
                    viewModel = weatherViewModel,
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
            }

            composable(Screen.History.route) {
                HistoryScreen(
                    viewModel = historyViewModel,
                    onScanClick = { scanId ->
                        navController.navigate(Screen.DiagnosisDetail.createRoute(scanId))
                    },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
            }

            composable(Screen.SoilAnalysis.route) {
                SoilAnalysisScreen(
                    viewModel = soilViewModel,
                    onAnalysisSuccess = { analysis ->
                        navController.navigate(Screen.SoilResult.createRoute(analysis.id))
                    },
                    onBackClick = { navController.popBackStack() },
                    onSettingsClick = { navController.navigate(Screen.Settings.route) }
                )
            }

            composable(
                route = Screen.SoilResult.route,
                arguments = listOf(navArgument("soilId") { type = NavType.StringType })
            ) {
                val soilState by soilViewModel.uiState.collectAsState()
                val result = soilState.result
                if (result != null) {
                    SoilResultScreen(
                        result = result,
                        onBackClick = { navController.popBackStack() },
                        onNewAnalysisClick = { navController.popBackStack() }
                    )
                } else {
                    navController.popBackStack()
                }
            }

            composable(
                route = Screen.CropDetail.route,
                arguments = listOf(navArgument("cropId") { type = NavType.StringType })
            ) { backStackEntry ->
                val cropId = backStackEntry.arguments?.getString("cropId") ?: "wheat"
                CropDetailScreen(
                    cropId = cropId,
                    viewModel = cropsViewModel,
                    onBackClick = { navController.popBackStack() },
                    onStartScanForCrop = { selectedCropId ->
                        cropsViewModel.selectCrop(selectedCropId)
                        navController.navigate(Screen.Scan.route)
                    }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
