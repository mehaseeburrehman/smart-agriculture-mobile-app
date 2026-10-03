package com.example.ui.screens.crops

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Crop
import com.example.ui.theme.AgriPrimary
import com.example.ui.theme.AgriSecondaryContainer
import com.example.ui.theme.AgriSurfaceContainerHigh
import com.example.ui.theme.AgriSurfaceContainerLow
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropSelectionSheet(
    crops: List<Crop>,
    selectedCropId: String,
    isLoading: Boolean,
    onCropSelected: (Crop) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AgriSurfaceContainerLow)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(id = R.string.select_crop_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = stringResource(id = R.string.select_crop_desc),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.surface, CircleShape)
                        .testTag("close_crop_sheet_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AgriPrimary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(crops) { crop ->
                        val isSelected = crop.id.equals(selectedCropId, ignoreCase = true)
                        CropOptionItem(
                            crop = crop,
                            isSelected = isSelected,
                            onSelect = {
                                if (crop.isModelActive) {
                                    onCropSelected(crop)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CropOptionItem(
    crop: Crop,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val isEnabled = crop.isModelActive
    val alpha = if (isEnabled) 1.0f else 0.55f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) AgriSecondaryContainer.copy(alpha = 0.3f) else AgriSurfaceContainerLow)
            .clickable(enabled = isEnabled) { onSelect() }
            .alpha(alpha)
            .padding(12.dp)
            .testTag("crop_option_${crop.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(if (isSelected) AgriPrimary else Color(0xFFC8E6C9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Spa,
                contentDescription = null,
                tint = if (isSelected) Color.White else AgriPrimary,
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = crop.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = crop.localName,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = if (crop.supportedDiseases.isNotEmpty()) {
                    crop.supportedDiseases.joinToString(", ")
                } else {
                    crop.description
                },
                fontSize = 11.sp,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        when {
            isSelected -> {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = stringResource(id = R.string.status_active),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AgriPrimary,
                        modifier = Modifier
                            .background(AgriSecondaryContainer, RoundedCornerShape(9999.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Active Crop",
                        tint = AgriPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            crop.isModelActive -> {
                Text(
                    text = stringResource(id = R.string.status_ready),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary,
                    modifier = Modifier
                        .background(AgriSurfaceContainerHigh, RoundedCornerShape(9999.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
            else -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color(0xFFE0E0E0), RoundedCornerShape(9999.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LockClock,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = stringResource(id = R.string.status_coming_soon),
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}
