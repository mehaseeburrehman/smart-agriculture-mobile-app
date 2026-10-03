package com.example.ui.screens.history

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
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
import com.example.data.local.SoilRecordEntity
import com.example.data.model.DiagnosisResult
import com.example.data.model.DiagnosisStatus
import com.example.ui.components.AppTopBar
import com.example.ui.components.BadgeSeverity
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AgriBackground
import com.example.ui.theme.AgriPrimary
import com.example.ui.theme.AgriSurfaceContainerLow
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onScanClick: (String) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AgriBackground)
    ) {
        AppTopBar(
            title = stringResource(id = R.string.app_name),
            subtitle = stringResource(id = R.string.history_title),
            onSettingsClick = onNavigateToSettings
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search Input
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text(stringResource(id = R.string.action_search) + " crops, diseases, plots...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("history_search_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Category Filter Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HistoryFilterPill(
                    title = stringResource(id = R.string.filter_all),
                    isSelected = uiState.selectedFilter == HistoryFilter.ALL,
                    onClick = { viewModel.setFilter(HistoryFilter.ALL) }
                )
                HistoryFilterPill(
                    title = stringResource(id = R.string.filter_scans),
                    isSelected = uiState.selectedFilter == HistoryFilter.SCANS,
                    onClick = { viewModel.setFilter(HistoryFilter.SCANS) }
                )
                HistoryFilterPill(
                    title = stringResource(id = R.string.filter_soil),
                    isSelected = uiState.selectedFilter == HistoryFilter.SOIL,
                    onClick = { viewModel.setFilter(HistoryFilter.SOIL) }
                )
            }

            // Filtered records list
            val filteredScans = uiState.scanRecords.filter { scan ->
                uiState.searchQuery.isBlank() ||
                        scan.cropName.contains(uiState.searchQuery, ignoreCase = true) ||
                        scan.diseaseName.contains(uiState.searchQuery, ignoreCase = true) ||
                        scan.fieldLocation.contains(uiState.searchQuery, ignoreCase = true)
            }

            val filteredSoils = uiState.soilRecords.filter { soil ->
                uiState.searchQuery.isBlank() ||
                        soil.cropName.contains(uiState.searchQuery, ignoreCase = true) ||
                        soil.fieldName.contains(uiState.searchQuery, ignoreCase = true)
            }

            val showScans = uiState.selectedFilter == HistoryFilter.ALL || uiState.selectedFilter == HistoryFilter.SCANS
            val showSoils = uiState.selectedFilter == HistoryFilter.ALL || uiState.selectedFilter == HistoryFilter.SOIL

            if ((showScans && filteredScans.isEmpty()) && (showSoils && filteredSoils.isEmpty())) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = TextSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = stringResource(id = R.string.empty_history),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = stringResource(id = R.string.empty_history_sub),
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (showScans) {
                        items(filteredScans, key = { it.id }) { scan ->
                            ScanHistoryCard(
                                scan = scan,
                                onClick = { onScanClick(scan.id) },
                                onDelete = { viewModel.deleteScanRecord(scan.id) }
                            )
                        }
                    }

                    if (showSoils) {
                        items(filteredSoils, key = { it.id }) { soil ->
                            SoilHistoryCard(soil = soil)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryFilterPill(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(9999.dp))
            .background(if (isSelected) AgriPrimary else AgriSurfaceContainerLow)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else TextPrimary
        )
    }
}

@Composable
fun ScanHistoryCard(
    scan: DiagnosisResult,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(scan.timestamp))

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("scan_history_card_${scan.id}")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(AgriSurfaceContainerLow, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CenterFocusStrong,
                    contentDescription = null,
                    tint = AgriPrimary,
                    modifier = Modifier.size(24.dp)
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
                        text = "${scan.cropName} • ${scan.fieldLocation}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = formattedDate,
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val severity = when (scan.status) {
                        DiagnosisStatus.HEALTHY -> BadgeSeverity.SUITABLE
                        DiagnosisStatus.DISEASE_DETECTED -> BadgeSeverity.UNSUITABLE
                        DiagnosisStatus.UNCERTAIN -> BadgeSeverity.CAUTION
                    }

                    StatusBadge(
                        text = scan.diseaseName,
                        severity = severity
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${scan.confidence}% match",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun SoilHistoryCard(soil: SoilRecordEntity) {
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(soil.timestamp))

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(Color(0xFFE8F5E9), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Science,
                    contentDescription = null,
                    tint = AgriPrimary,
                    modifier = Modifier.size(24.dp)
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
                        text = "${soil.cropName} • ${soil.fieldName}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = formattedDate,
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusBadge(
                        text = soil.overallRating,
                        severity = BadgeSeverity.SUITABLE
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "pH ${soil.ph} • Moist ${soil.moisturePct}%",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}
