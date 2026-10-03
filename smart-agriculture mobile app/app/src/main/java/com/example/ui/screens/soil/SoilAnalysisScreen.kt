package com.example.ui.screens.soil

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.SoilAnalysisResult
import com.example.ui.components.AppTopBar
import com.example.ui.screens.crops.CropSelectionSheet
import com.example.ui.theme.AgriBackground
import com.example.ui.theme.AgriPrimary
import com.example.ui.theme.AgriPrimaryContainer
import com.example.ui.theme.AgriSurfaceContainerHigh
import com.example.ui.theme.AgriSurfaceContainerLow
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SoilAnalysisScreen(
    viewModel: SoilViewModel,
    onAnalysisSuccess: (SoilAnalysisResult) -> Unit,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCropSheet by remember { mutableStateOf(false) }

    val selectedCrop = uiState.crops.find { it.id.equals(uiState.selectedCropId, ignoreCase = true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AgriBackground)
    ) {
        AppTopBar(
            title = stringResource(id = R.string.soil_analysis_title),
            subtitle = selectedCrop?.name ?: "Wheat",
            showBackButton = true,
            onBackClick = onBackClick,
            onSettingsClick = onSettingsClick,
            showLocationChip = false
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Target Crop Selector
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCropSheet = true }
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(AgriSurfaceContainerLow, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Grass,
                                contentDescription = null,
                                tint = AgriPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Calibrate for Crop",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = selectedCrop?.name ?: "Wheat",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Field Plot Name
            OutlinedTextField(
                value = uiState.fieldPlotName,
                onValueChange = { viewModel.setFieldPlotName(it) },
                label = { Text(stringResource(id = R.string.field_plot_name)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("soil_plot_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Primary Macro-Nutrients Card (N, P, K)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Science,
                            contentDescription = null,
                            tint = AgriPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Macro-Nutrient Concentration",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    // Nitrogen
                    SoilNutrientInputField(
                        label = stringResource(id = R.string.nitrogen_label),
                        value = uiState.nitrogenInput,
                        unit = uiState.nitrogenUnit,
                        rangeTip = "Normal: 140 - 240 kg/ha",
                        onValueChange = { viewModel.setNitrogen(it) },
                        testTag = "nitrogen_input"
                    )

                    // Phosphorus
                    SoilNutrientInputField(
                        label = stringResource(id = R.string.phosphorus_label),
                        value = uiState.phosphorusInput,
                        unit = uiState.phosphorusUnit,
                        rangeTip = "Normal: 25 - 60 kg/ha",
                        onValueChange = { viewModel.setPhosphorus(it) },
                        testTag = "phosphorus_input"
                    )

                    // Potassium
                    SoilNutrientInputField(
                        label = stringResource(id = R.string.potassium_label),
                        value = uiState.potassiumInput,
                        unit = uiState.potassiumUnit,
                        rangeTip = "Normal: 160 - 280 kg/ha",
                        onValueChange = { viewModel.setPotassium(it) },
                        testTag = "potassium_input"
                    )
                }
            }

            // Chemical & Physical Parameters Card (pH & Moisture)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = AgriPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Soil Reaction & Field Moisture",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    // pH Input
                    SoilNutrientInputField(
                        label = stringResource(id = R.string.ph_label),
                        value = uiState.phInput,
                        unit = "pH",
                        rangeTip = "Optimal: 6.5 - 7.5",
                        onValueChange = { viewModel.setPh(it) },
                        testTag = "ph_input"
                    )

                    // Moisture Input
                    SoilNutrientInputField(
                        label = stringResource(id = R.string.moisture_label),
                        value = uiState.moistureInput,
                        unit = "%",
                        rangeTip = "Field Capacity: 20% - 35%",
                        onValueChange = { viewModel.setMoisture(it) },
                        testTag = "moisture_input"
                    )
                }
            }

            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            // Submit Button
            Button(
                onClick = { viewModel.submitSoilAnalysis(onAnalysisSuccess) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("run_soil_analysis_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AgriPrimaryContainer,
                    contentColor = Color.White
                ),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Science,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(id = R.string.run_soil_analysis),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showCropSheet) {
        CropSelectionSheet(
            crops = uiState.crops,
            selectedCropId = uiState.selectedCropId,
            isLoading = false,
            onCropSelected = { crop ->
                viewModel.setCrop(crop.id)
                showCropSheet = false
            },
            onDismiss = { showCropSheet = false }
        )
    }
}

@Composable
fun SoilNutrientInputField(
    label: String,
    value: String,
    unit: String,
    rangeTip: String,
    onValueChange: (String) -> Unit,
    testTag: String
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            trailingIcon = {
                Text(
                    text = unit,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AgriPrimary,
                    modifier = Modifier
                        .background(AgriSurfaceContainerLow, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag),
            shape = RoundedCornerShape(12.dp)
        )
        Text(
            text = rangeTip,
            fontSize = 11.sp,
            color = TextSecondary,
            modifier = Modifier.padding(start = 6.dp, top = 2.dp)
        )
    }
}
