package com.example.ui.screens.diagnosis

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Compost
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.DiagnosisResult
import com.example.data.model.DiagnosisStatus
import com.example.ui.components.AppTopBar
import com.example.ui.theme.AgriBackground
import com.example.ui.theme.AgriErrorContainer
import com.example.ui.theme.AgriOnErrorContainer
import com.example.ui.theme.AgriPrimary
import com.example.ui.theme.AgriPrimaryContainer
import com.example.ui.theme.AgriSecondary
import com.example.ui.theme.AgriSecondaryContainer
import com.example.ui.theme.AgriSurfaceContainerHigh
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
fun DiagnosisResultScreen(
    result: DiagnosisResult,
    onSaveResult: () -> Unit,
    onViewWeatherRisks: () -> Unit,
    onScanAnotherLeaf: () -> Unit,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val context = LocalContext.current
    var symptomsExpanded by remember { mutableStateOf(true) }
    var causesExpanded by remember { mutableStateOf(false) }
    var organicExpanded by remember { mutableStateOf(false) }
    var chemicalExpanded by remember { mutableStateOf(false) }

    val dateFormat = SimpleDateFormat("EEEE, hh:mm a", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(result.timestamp))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AgriBackground)
    ) {
        AppTopBar(
            title = stringResource(id = R.string.diagnosis_detail_title),
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Field Scan Context Badge Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AgriSurfaceContainerLow, RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = AgriSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "${result.cropName} (${result.scientificName})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "$formattedDate • ${result.fieldLocation}",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                IconButton(
                    onClick = {
                        Toast.makeText(context, "Calibrated with Punjab field databases", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Details",
                        tint = AgriSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Scanned Leaf Visual Evidence Tile
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.6f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF29332D))
                    .testTag("scanned_evidence_tile"),
                contentAlignment = Alignment.Center
            ) {
                if (result.imageUri != null) {
                    AsyncImage(
                        model = result.imageUri,
                        contentDescription = "Diagnosed Leaf Evidence",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.size(64.dp)
                    )
                }

                // Overlay chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CenterFocusStrong,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Scanned Frame #482",
                            fontSize = 11.sp,
                            color = Color.White
                        )
                    }

                    Text(
                        text = "Resolution: 12 MP",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            // Diagnostic Status Banner (High Severity / Healthy)
            val isDisease = result.status == DiagnosisStatus.DISEASE_DETECTED
            val bannerBg = if (isDisease) AgriErrorContainer else Color(0xFFE8F5E9)
            val bannerTextColor = if (isDisease) AgriOnErrorContainer else Color(0xFF1B5E20)
            val statusTagColor = if (isDisease) StatusUnsuitable else StatusSuitable

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = bannerBg),
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
                                    .size(10.dp)
                                    .background(statusTagColor, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isDisease) "DISEASE DETECTED" else "HEALTHY FOLIAGE",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusTagColor,
                                letterSpacing = 1.sp
                            )
                        }

                        Icon(
                            imageVector = if (isDisease) Icons.Default.Warning else Icons.Default.Eco,
                            contentDescription = null,
                            tint = statusTagColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = result.diseaseName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = bannerTextColor
                    )

                    if (result.scientificPathogen.isNotBlank()) {
                        Text(
                            text = result.scientificPathogen,
                            fontSize = 12.sp,
                            fontStyle = FontStyle.Italic,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Responsible AI / Model Confidence Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Analytics,
                                contentDescription = null,
                                tint = AgriSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${result.confidence}% Model Confidence",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Text(
                            text = result.confidenceLevel,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AgriSecondary,
                            modifier = Modifier
                                .background(AgriSurfaceContainerHigh, RoundedCornerShape(9999.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { result.confidence / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(9999.dp)),
                        color = AgriPrimary,
                        trackColor = AgriSurfaceContainerLow
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = stringResource(id = R.string.confidence_disclaimer),
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }

            // Agronomic Guidance Accordion Group
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Accordion 1: Identified Symptoms
                GuidanceAccordionItem(
                    title = stringResource(id = R.string.identified_symptoms),
                    icon = Icons.Default.Search,
                    isExpanded = symptomsExpanded,
                    onToggle = { symptomsExpanded = !symptomsExpanded },
                    content = result.symptoms
                )

                // Accordion 2: Environmental Causes
                GuidanceAccordionItem(
                    title = stringResource(id = R.string.possible_causes),
                    icon = Icons.Default.Thermostat,
                    isExpanded = causesExpanded,
                    onToggle = { causesExpanded = !causesExpanded },
                    content = result.causes
                )

                // Accordion 3: Organic Management
                GuidanceAccordionItem(
                    title = stringResource(id = R.string.organic_management),
                    icon = Icons.Default.Compost,
                    isExpanded = organicExpanded,
                    onToggle = { organicExpanded = !organicExpanded },
                    content = result.organicManagement
                )

                // Accordion 4: Chemical Intervention
                GuidanceAccordionItem(
                    title = stringResource(id = R.string.chemical_intervention),
                    icon = Icons.Default.Science,
                    isExpanded = chemicalExpanded,
                    onToggle = { chemicalExpanded = !chemicalExpanded },
                    content = result.chemicalIntervention
                )
            }

            // Mandatory Agronomic Safety Warning Box
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(StatusCaution.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(StatusCaution.copy(alpha = 0.25f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.HealthAndSafety,
                        contentDescription = null,
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = stringResource(id = R.string.safety_warning_title),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(id = R.string.safety_warning_desc),
                        fontSize = 12.sp,
                        color = TextPrimary,
                        lineHeight = 17.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Action Buttons Stack
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        onSaveResult()
                        Toast.makeText(context, context.getString(R.string.result_saved_toast), Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("save_result_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AgriPrimaryContainer,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkAdd,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(id = R.string.save_to_field_record),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                }

                Button(
                    onClick = onViewWeatherRisks,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("view_weather_risks_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AgriSurfaceContainerLow,
                        contentColor = AgriPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.WbCloudy,
                        contentDescription = null,
                        tint = AgriPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(id = R.string.view_weather_risks),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                }

                TextButton(
                    onClick = onScanAnotherLeaf,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("scan_another_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.DocumentScanner,
                        contentDescription = null,
                        tint = AgriPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(id = R.string.scan_another_leaf),
                        color = AgriPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun GuidanceAccordionItem(
    title: String,
    icon: ImageVector,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    content: String
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(AgriSurfaceContainerLow, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = AgriPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Toggle",
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 14.dp, end = 14.dp, bottom = 14.dp)
                        .background(AgriSurfaceContainerLow, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = content,
                        fontSize = 13.sp,
                        color = TextPrimary,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}
