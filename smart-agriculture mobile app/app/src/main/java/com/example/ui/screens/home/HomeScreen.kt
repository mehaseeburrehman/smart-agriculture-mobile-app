package com.example.ui.screens.home

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.DiagnosisResult
import com.example.data.model.DiagnosisStatus
import com.example.ui.components.AppTopBar
import com.example.ui.components.BadgeSeverity
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AgriBackground
import com.example.ui.theme.AgriPrimary
import com.example.ui.theme.AgriPrimaryContainer
import com.example.ui.theme.AgriSecondaryContainer
import com.example.ui.theme.AgriSurfaceContainerLow
import com.example.ui.theme.StatusCaution
import com.example.ui.theme.StatusInfo
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onStartScan: () -> Unit,
    onNavigateToAdvisories: () -> Unit,
    onNavigateToSoil: () -> Unit,
    onNavigateToCropGuide: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onScanClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AgriBackground)
    ) {
        AppTopBar(
            title = stringResource(id = R.string.app_name),
            subtitle = stringResource(id = R.string.nav_home),
            onSettingsClick = onNavigateToSettings
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Greeting & Environmental Status Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(id = R.string.morning_inspection),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = stringResource(id = R.string.greeting_farmer),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(AgriSecondaryContainer.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = "Weather Icon",
                        tint = AgriPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.PinDrop,
                    contentDescription = null,
                    tint = AgriPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(id = R.string.current_zone),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Text(
                    text = " • " + stringResource(id = R.string.home_weather_chip),
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Primary Hero Card: Check a Leaf
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_check_leaf_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(AgriSecondaryContainer.copy(alpha = 0.4f), RoundedCornerShape(9999.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = AgriPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(id = R.string.instant_diagnosis),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AgriPrimary
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.CenterFocusStrong,
                            contentDescription = null,
                            tint = AgriPrimaryContainer,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = stringResource(id = R.string.check_leaf_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = stringResource(id = R.string.check_leaf_desc),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onStartScan,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("start_disease_scan_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AgriPrimaryContainer,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.CenterFocusStrong,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.start_disease_scan),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Weather & Spraying Advisory Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToAdvisories() }
                    .testTag("spray_conditions_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = stringResource(id = R.string.spray_conditions),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = stringResource(id = R.string.current_field_microclimate),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }

                        StatusBadge(
                            text = stringResource(id = R.string.spray_caution_badge),
                            severity = BadgeSeverity.CAUTION
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 4-Column Weather Bento
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        WeatherMetricTile(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Thermostat,
                            iconTint = TextSecondary,
                            value = "29°",
                            label = stringResource(id = R.string.metric_temp)
                        )
                        WeatherMetricTile(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.WaterDrop,
                            iconTint = StatusInfo,
                            value = "78%",
                            label = stringResource(id = R.string.metric_humidity)
                        )
                        WeatherMetricTile(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Umbrella,
                            iconTint = AgriPrimary,
                            value = "65%",
                            label = stringResource(id = R.string.metric_rain)
                        )
                        WeatherMetricTile(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Air,
                            iconTint = TextSecondary,
                            value = "18",
                            label = "km/h"
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Advisory Warning Callout Box
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(StatusCaution.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = StatusCaution,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(top = 1.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.spray_advisory_snippet),
                            fontSize = 12.sp,
                            color = TextPrimary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Actions Section
            Text(
                text = stringResource(id = R.string.quick_actions),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 2.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.WbCloudy,
                    iconBg = AgriSecondaryContainer.copy(alpha = 0.4f),
                    iconTint = AgriPrimary,
                    title = stringResource(id = R.string.action_weather_advisory),
                    subtitle = stringResource(id = R.string.weather_advisory_sub),
                    onClick = onNavigateToAdvisories,
                    testTag = "quick_action_weather"
                )

                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Science,
                    iconBg = Color(0xFFAAF3B8).copy(alpha = 0.4f),
                    iconTint = AgriPrimary,
                    title = stringResource(id = R.string.action_soil_analysis),
                    subtitle = stringResource(id = R.string.soil_analysis_sub),
                    onClick = onNavigateToSoil,
                    testTag = "quick_action_soil"
                )

                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    iconBg = AgriSurfaceContainerLow,
                    iconTint = AgriPrimaryContainer,
                    title = stringResource(id = R.string.action_crop_guide),
                    subtitle = stringResource(id = R.string.crop_guide_sub),
                    onClick = onNavigateToCropGuide,
                    testTag = "quick_action_crops"
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Recent Scans Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(id = R.string.recent_scans),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(id = R.string.total_count, uiState.recentScans.size),
                        fontSize = 11.sp,
                        color = TextSecondary,
                        modifier = Modifier
                            .background(AgriSurfaceContainerLow, RoundedCornerShape(9999.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .clickable { onNavigateToHistory() }
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.action_view_all),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AgriPrimary
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = AgriPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (uiState.recentScans.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.empty_history_sub),
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    uiState.recentScans.forEach { scan ->
                        RecentScanItem(
                            scan = scan,
                            onClick = { onScanClick(scan.id) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun WeatherMetricTile(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    value: String,
    label: String
) {
    Box(
        modifier = modifier
            .background(AgriSurfaceContainerLow, RoundedCornerShape(12.dp))
            .padding(vertical = 10.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun QuickActionCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(iconBg, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun RecentScanItem(
    scan: DiagnosisResult,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val dateString = dateFormat.format(Date(scan.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("recent_scan_item_${scan.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AgriSurfaceContainerLow),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CenterFocusStrong,
                    contentDescription = null,
                    tint = AgriPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = scan.fieldLocation.ifBlank { scan.cropName },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = dateString,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val (badgeSeverity, badgeText) = when (scan.status) {
                        DiagnosisStatus.HEALTHY -> Pair(BadgeSeverity.SUITABLE, "Healthy Leaf")
                        DiagnosisStatus.DISEASE_DETECTED -> Pair(BadgeSeverity.UNSUITABLE, scan.diseaseName)
                        DiagnosisStatus.UNCERTAIN -> Pair(BadgeSeverity.CAUTION, "Uncertain")
                    }

                    StatusBadge(
                        text = badgeText,
                        severity = badgeSeverity
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "${scan.confidence}% confidence",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = scan.chemicalIntervention.ifBlank { scan.symptoms },
                    fontSize = 11.sp,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
