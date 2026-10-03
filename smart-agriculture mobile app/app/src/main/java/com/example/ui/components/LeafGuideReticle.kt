package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AgriOnPrimaryContainer

@Composable
fun LeafGuideReticle(
    modifier: Modifier = Modifier,
    guideColor: Color = AgriOnPrimaryContainer
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = modifier.size(width = 220.dp, height = 280.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height
            val strokeWidth = 3.dp.toPx()
            val cornerLength = 24.dp.toPx()

            // 4 Corner Brackets
            // Top Left
            drawLine(
                color = guideColor,
                start = Offset(0f, 0f),
                end = Offset(cornerLength, 0f),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            drawLine(
                color = guideColor,
                start = Offset(0f, 0f),
                end = Offset(0f, cornerLength),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )

            // Top Right
            drawLine(
                color = guideColor,
                start = Offset(w, 0f),
                end = Offset(w - cornerLength, 0f),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            drawLine(
                color = guideColor,
                start = Offset(w, 0f),
                end = Offset(w, cornerLength),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )

            // Bottom Left
            drawLine(
                color = guideColor,
                start = Offset(0f, h),
                end = Offset(cornerLength, h),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            drawLine(
                color = guideColor,
                start = Offset(0f, h),
                end = Offset(0f, h - cornerLength),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )

            // Bottom Right
            drawLine(
                color = guideColor,
                start = Offset(w, h),
                end = Offset(w - cornerLength, h),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            drawLine(
                color = guideColor,
                start = Offset(w, h),
                end = Offset(w, h - cornerLength),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )

            // Stylized Dashed Leaf Outline
            val leafPath = Path().apply {
                moveTo(w / 2f, h * 0.12f)
                cubicTo(
                    w * 0.85f, h * 0.3f,
                    w * 0.9f, h * 0.7f,
                    w / 2f, h * 0.92f
                )
                cubicTo(
                    w * 0.1f, h * 0.7f,
                    w * 0.15f, h * 0.3f,
                    w / 2f, h * 0.12f
                )
            }

            drawPath(
                path = leafPath,
                color = guideColor.copy(alpha = alphaAnim),
                style = Stroke(
                    width = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f))
                )
            )

            // Central Leaf Vein
            drawLine(
                color = guideColor.copy(alpha = alphaAnim * 0.7f),
                start = Offset(w / 2f, h * 0.22f),
                end = Offset(w / 2f, h * 0.86f),
                strokeWidth = 1.5.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Center targeting reticle circle
            drawCircle(
                color = guideColor.copy(alpha = 0.3f * alphaAnim),
                radius = 16.dp.toPx(),
                center = Offset(w / 2f, h / 2f)
            )
            drawCircle(
                color = guideColor,
                radius = 3.dp.toPx(),
                center = Offset(w / 2f, h / 2f)
            )
        }
    }
}
