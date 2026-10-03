package com.example.ui.screens.soil

import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Compost
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.NutrientRating
import com.example.data.model.SoilAnalysisResult
import com.example.ui.components.AppTopBar
import com.example.ui.components.BadgeSeverity
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AgriBackground
import com.example.ui.theme.AgriPrimary
import com.example.ui.theme.AgriPrimaryContainer
import com.example.ui.theme.AgriSecondary
import com.example.ui.theme.AgriSurfaceContainerLow
import com.example.ui.theme.StatusCaution
import com.example.ui.theme.StatusSuitable
import com.example.ui.theme.StatusUnsuitable
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SoilResultScreen(
    result: SoilAnalysisResult,
    onBackClick: () -> Unit,
    onNewAnalysisClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMMM yyyy, hh:mm a", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(result.timestamp))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AgriBackground)
    ) {
        AppTopBar(
            title = stringResource(id = R.string.soil_scorecard),
            subtitle = "${result.cropName} • ${result.fieldName}",
            showBackButton = true,
            onBackClick = onBackClick,
            showLocationChip = false
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Overall Rating Banner
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
                                    imageVector = Icons.Default.Science,
                                    contentDescription = null,
                                    tint = AgriPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = result.fieldName,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = formattedDate,
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        StatusBadge(
                            text = result.overallRating,
                            severity = if (result.overallRating.contains("Healthy")) BadgeSeverity.SUITABLE else BadgeSeverity.CAUTION
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(AgriSurfaceContainerLow, RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(text = "Soil Reaction", fontSize = 11.sp, color = TextSecondary)
                                Text(
                                    text = result.phRating,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(text = result.phAdvice, fontSize = 10.sp, color = TextSecondary)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(AgriSurfaceContainerLow, RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(text = "Field Moisture", fontSize = 11.sp, color = TextSecondary)
                                Text(
                                    text = result.moistureRating,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(text = result.moistureAdvice, fontSize = 10.sp, color = TextSecondary)
                            }
                        }
                    }
                }
            }

            // NPK Diagnostic Breakdown
            Text(
                text = "Primary Nutrients Evaluation",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                result.nutrients.forEach { nutrient ->
                    NutrientScoreCard(nutrient = nutrient)
                }
            }

            // Fertilizer Action Plan
            Text(
                text = stringResource(id = R.string.fertilizer_recommendations),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    result.fertilizerPlan.forEach { action ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = null,
                                tint = AgriPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = action,
                                fontSize = 13.sp,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }

            // Organic Soil Amendments
            Text(
                text = stringResource(id = R.string.organic_soil_amendments),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(AgriSurfaceContainerLow, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Compost,
                            contentDescription = null,
                            tint = AgriSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = result.organicAmendments,
                        fontSize = 13.sp,
                        color = TextPrimary,
                        lineHeight = 18.sp
                    )
                }
            }

            // Action Buttons
            Button(
                onClick = onNewAnalysisClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("new_soil_analysis_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AgriPrimaryContainer,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Perform Another Soil Test",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun NutrientScoreCard(nutrient: NutrientRating) {
    val (statusSeverity, statusColor) = when {
        nutrient.status.contains("Deficient") || nutrient.status.contains("Low") -> Pair(BadgeSeverity.CAUTION, StatusCaution)
        nutrient.status.contains("Excess") -> Pair(BadgeSeverity.UNSUITABLE, StatusUnsuitable)
        else -> Pair(BadgeSeverity.SUITABLE, StatusSuitable)
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = nutrient.nutrient,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${nutrient.value} ${nutrient.unit}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AgriPrimary
                    )
                }

                StatusBadge(
                    text = nutrient.status,
                    severity = statusSeverity
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Target Optimal: ${nutrient.targetRange}",
                fontSize = 11.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = nutrient.advice,
                fontSize = 12.sp,
                color = TextPrimary,
                lineHeight = 16.sp
            )
        }
    }
}
