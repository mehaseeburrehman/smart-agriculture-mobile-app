package com.example.ui.screens.weather

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Coronavirus
import androidx.compose.material.icons.filled.EditLocationAlt
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.NotificationAdd
import androidx.compose.material.icons.filled.PestControl
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AdvisoryPurpose
import com.example.data.model.AdvisoryStatus
import com.example.data.model.RiskFactor
import com.example.ui.components.AppTopBar
import com.example.ui.components.BadgeSeverity
import com.example.ui.components.StatusBadge
import com.example.ui.screens.crops.CropSelectionSheet
import com.example.ui.theme.AgriBackground
import com.example.ui.theme.AgriPrimary
import com.example.ui.theme.AgriPrimaryContainer
import com.example.ui.theme.AgriSecondaryContainer
import com.example.ui.theme.AgriSurfaceContainerHigh
import com.example.ui.theme.AgriSurfaceContainerLow
import com.example.ui.theme.StatusCaution
import com.example.ui.theme.StatusInfo
import com.example.ui.theme.StatusSuitable
import com.example.ui.theme.StatusUnsuitable
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun WeatherAdvisoryScreen(
    viewModel: WeatherViewModel,
    onNavigateToSettings: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var showCropSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AgriBackground)
    ) {
        AppTopBar(
            title = stringResource(id = R.string.app_name),
            subtitle = stringResource(id = R.string.advisories_title),
            onSettingsClick = onNavigateToSettings
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Top Selectors: Crop Chip & Stage Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Crop Selector Chip
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(AgriSurfaceContainerHigh)
                        .clickable { showCropSheet = true }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .testTag("weather_crop_select_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Grass,
                        contentDescription = null,
                        tint = AgriPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = uiState.selectedCrop?.name ?: "Wheat",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Active Season Stage Tag
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(AgriSurfaceContainerLow, RoundedCornerShape(9999.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(StatusSuitable, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = uiState.advisory?.stageName ?: "Tillering Stage",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = AgriPrimary
                    )
                }
            }

            // Location Bar with GPS tag
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(AgriSurfaceContainerLow, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.NearMe,
                                contentDescription = null,
                                tint = AgriPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = uiState.advisory?.locationName ?: "Multan District",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "GPS",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AgriPrimary,
                                    modifier = Modifier
                                        .background(AgriPrimary.copy(alpha = 0.1f), RoundedCornerShape(9999.dp))
                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = "Auto-detected • Elev. ${uiState.advisory?.elevationMeters ?: 122}m",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            Toast.makeText(context, "Location coordinates locked via GPS", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditLocationAlt,
                            contentDescription = "Edit Location",
                            tint = AgriPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Purpose Segmented Control (Growing, Sowing, Spraying)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AgriSurfaceContainerHigh, RoundedCornerShape(14.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                PurposeTabItem(
                    title = stringResource(id = R.string.purpose_growing),
                    icon = Icons.Default.Spa,
                    isSelected = uiState.selectedPurpose == AdvisoryPurpose.GROWING,
                    onClick = { viewModel.setPurpose(AdvisoryPurpose.GROWING) },
                    modifier = Modifier.weight(1f),
                    testTag = "purpose_tab_growing"
                )
                PurposeTabItem(
                    title = stringResource(id = R.string.purpose_sowing),
                    icon = Icons.Default.Grain,
                    isSelected = uiState.selectedPurpose == AdvisoryPurpose.SOWING,
                    onClick = { viewModel.setPurpose(AdvisoryPurpose.SOWING) },
                    modifier = Modifier.weight(1f),
                    testTag = "purpose_tab_sowing"
                )
                PurposeTabItem(
                    title = stringResource(id = R.string.purpose_spraying),
                    icon = Icons.Default.PestControl,
                    isSelected = uiState.selectedPurpose == AdvisoryPurpose.SPRAYING,
                    onClick = { viewModel.setPurpose(AdvisoryPurpose.SPRAYING) },
                    modifier = Modifier.weight(1f),
                    testTag = "purpose_tab_spraying"
                )
            }

            val advisory = uiState.advisory
            if (advisory != null) {
                // Overall Advisory Verdict Banner
                val accentColor = when (advisory.status) {
                    AdvisoryStatus.SUITABLE -> StatusSuitable
                    AdvisoryStatus.CAUTION -> StatusCaution
                    AdvisoryStatus.UNSUITABLE -> StatusUnsuitable
                }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        // Colored accent stripe on left
                        Box(
                            modifier = Modifier
                                .width(6.dp)
                                .height(160.dp)
                                .background(accentColor)
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFFF8E1).copy(alpha = 0.5f))
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .background(accentColor.copy(alpha = 0.2f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.WarningAmber,
                                            contentDescription = null,
                                            tint = accentColor,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = advisory.verdictTitle,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF5D3E00),
                                            modifier = Modifier
                                                .background(accentColor, RoundedCornerShape(9999.dp))
                                                .padding(horizontal = 7.dp, vertical = 2.dp)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = advisory.verdictSubtitle,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    }
                                }

                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "High humidity and gusty winds reduce chemical efficacy and increase drift risks. Foliar uptake is significantly compromised under incoming precipitation.",
                                fontSize = 12.sp,
                                color = TextPrimary,
                                lineHeight = 17.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Micro Recommendation Bar
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(8.dp))
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Alarm,
                                    contentDescription = null,
                                    tint = AgriPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Recommended Window: ",
                                    fontSize = 11.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = advisory.recommendedWindow,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AgriPrimary
                                )
                            }
                        }
                    }
                }

                // Micro-Climate Current Conditions Grid
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.microclimate_header),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = stringResource(id = R.string.field_synced),
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    // 2-column large cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Temperature Tile
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "Temperature", fontSize = 11.sp, color = TextSecondary)
                                    Icon(
                                        imageVector = Icons.Default.Thermostat,
                                        contentDescription = null,
                                        tint = StatusCaution,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "${advisory.temperatureC}",
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "°C",
                                        fontSize = 16.sp,
                                        color = TextSecondary,
                                        modifier = Modifier.padding(bottom = 3.dp, start = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = StatusSuitable,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Optimal (20-30°C)",
                                        fontSize = 10.sp,
                                        color = StatusSuitable,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        // Rel. Humidity Tile
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "Rel. Humidity", fontSize = 11.sp, color = TextSecondary)
                                    Icon(
                                        imageVector = Icons.Default.WaterDrop,
                                        contentDescription = null,
                                        tint = StatusUnsuitable,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "${advisory.humidityPct}",
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "%",
                                        fontSize = 16.sp,
                                        color = TextSecondary,
                                        modifier = Modifier.padding(bottom = 3.dp, start = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "↑ Elevated (+12% avg)",
                                    fontSize = 10.sp,
                                    color = StatusUnsuitable,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // 3 Mini Metric Cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MiniWeatherMetricCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.WaterDrop,
                            iconTint = StatusInfo,
                            title = stringResource(id = R.string.rain_probability),
                            value = "${advisory.rainProbabilityPct}%",
                            subtitle = "High wash-off"
                        )
                        MiniWeatherMetricCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Umbrella,
                            iconTint = StatusInfo,
                            title = stringResource(id = R.string.precipitation),
                            value = "${advisory.precipitationMm} mm",
                            subtitle = "Next 6h total"
                        )
                        MiniWeatherMetricCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Air,
                            iconTint = StatusCaution,
                            title = stringResource(id = R.string.wind_speed),
                            value = "${advisory.windSpeedKmh} km/h",
                            subtitle = "Gusty drift"
                        )
                    }
                }

                // Agronomic Risk Breakdown
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.agronomic_risk_breakdown),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = stringResource(id = R.string.active_factors, advisory.riskFactors.size),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AgriPrimary
                        )
                    }

                    advisory.riskFactors.forEach { factor ->
                        RiskFactorCard(factor = factor)
                    }
                }

                // Interactive Action CTA
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            Toast.makeText(context, "Spray window alert scheduled for 06:30 AM", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("set_spray_alert_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AgriPrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationAdd,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.set_spray_alert),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    IconButton(
                        onClick = {
                            Toast.makeText(context, "Advisory summary shared", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(AgriSurfaceContainerHigh)
                            .testTag("share_weather_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Attribution Footer
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(id = R.string.weather_attribution),
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(id = R.string.last_updated, advisory.lastUpdatedTime),
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { viewModel.refresh() }
                                .padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = AgriPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = stringResource(id = R.string.action_refresh),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AgriPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showCropSheet) {
        CropSelectionSheet(
            crops = uiState.availableCrops,
            selectedCropId = uiState.selectedCrop?.id ?: "wheat",
            isLoading = false,
            onCropSelected = { crop ->
                viewModel.selectCrop(crop)
                showCropSheet = false
            },
            onDismiss = { showCropSheet = false }
        )
    }
}

@Composable
fun PurposeTabItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 10.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) AgriPrimary else TextSecondary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) AgriPrimary else TextSecondary
            )
        }
    }
}

@Composable
fun MiniWeatherMetricCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String,
    value: String,
    subtitle: String
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = title,
                    fontSize = 10.sp,
                    color = TextSecondary,
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = TextSecondary,
                maxLines = 1
            )
        }
    }
}

@Composable
fun RiskFactorCard(factor: RiskFactor) {
    val (badgeBg, badgeText, icon) = when (factor.severity.lowercase()) {
        "critical" -> Triple(Color(0xFFFFEBEE), StatusUnsuitable, Icons.Default.Coronavirus)
        "caution" -> Triple(Color(0xFFFFF8E1), StatusCaution, Icons.Default.Thunderstorm)
        else -> Triple(Color(0xFFE8F5E9), StatusSuitable, Icons.Default.WbSunny)
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
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(badgeBg, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = badgeText,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = factor.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = factor.riskTag,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeText
                        )
                    }
                }

                Text(
                    text = factor.severity,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = badgeText,
                    modifier = Modifier
                        .background(badgeBg, RoundedCornerShape(9999.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = factor.description,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AgriSurfaceContainerLow, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.AssignmentTurnedIn,
                    contentDescription = null,
                    tint = AgriPrimary,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(top = 1.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = stringResource(id = R.string.next_action),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = factor.nextAction,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}
