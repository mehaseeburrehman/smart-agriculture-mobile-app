package com.example.ui.screens.scan

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.DiagnosisResult
import com.example.ui.theme.AgriBackground
import com.example.ui.theme.AgriPrimary
import com.example.ui.theme.AgriSurfaceContainerLow
import com.example.ui.theme.StatusSuitable
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AnalyzingScreen(
    viewModel: ScanViewModel,
    onSuccess: (DiagnosisResult) -> Unit,
    onLowConfidence: (DiagnosisResult) -> Unit
) {
    val analysisState by viewModel.analysisState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.analyzeImage(
            onSuccess = { result -> onSuccess(result) },
            onLowConfidence = { result -> onLowConfidence(result) }
        )
    }

    val currentStep = when (val state = analysisState) {
        is AnalysisState.Analyzing -> state.step
        is AnalysisState.Success -> 3
        is AnalysisState.LowConfidence -> 3
        else -> 1
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AgriBackground)
            .testTag("analyzing_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Animated Leaf Icon Centerpiece
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(AgriPrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(86.dp),
                    color = AgriPrimary,
                    strokeWidth = 3.dp
                )

                Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = null,
                    tint = AgriPrimary,
                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(id = R.string.analyzing_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Processing leaf image against regional crop neural models",
                fontSize = 13.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Step Indicator Cards
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PipelineStepRow(
                    stepNumber = 1,
                    text = stringResource(id = R.string.step_uploading),
                    isCompleted = currentStep > 1,
                    isActive = currentStep == 1
                )

                PipelineStepRow(
                    stepNumber = 2,
                    text = stringResource(id = R.string.step_morphology),
                    isCompleted = currentStep > 2,
                    isActive = currentStep == 2
                )

                PipelineStepRow(
                    stepNumber = 3,
                    text = stringResource(id = R.string.step_evaluating),
                    isCompleted = currentStep >= 3,
                    isActive = currentStep == 3
                )
            }
        }
    }
}

@Composable
fun PipelineStepRow(
    stepNumber: Int,
    text: String,
    isCompleted: Boolean,
    isActive: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isActive) MaterialTheme.colorScheme.surface else AgriSurfaceContainerLow
            )
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isCompleted) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Completed",
                tint = StatusSuitable,
                modifier = Modifier.size(22.dp)
            )
        } else if (isActive) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = AgriPrimary,
                strokeWidth = 2.5.dp
            )
        } else {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .background(Color.Gray.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stepNumber.toString(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isActive) TextPrimary else TextSecondary
        )
    }
}
