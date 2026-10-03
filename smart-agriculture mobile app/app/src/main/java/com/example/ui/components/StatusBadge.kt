package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.StatusCaution
import com.example.ui.theme.StatusInfo
import com.example.ui.theme.StatusSuitable
import com.example.ui.theme.StatusUnsuitable

enum class BadgeSeverity {
    SUITABLE,
    CAUTION,
    UNSUITABLE,
    INFO
}

@Composable
fun StatusBadge(
    text: String,
    severity: BadgeSeverity,
    modifier: Modifier = Modifier,
    showDot: Boolean = true
) {
    val (bgColor, textColor, dotColor) = when (severity) {
        BadgeSeverity.SUITABLE -> Triple(Color(0xFFE8F5E9), StatusSuitable, StatusSuitable)
        BadgeSeverity.CAUTION -> Triple(Color(0xFFFFF8E1), Color(0xFF8A5B00), StatusCaution)
        BadgeSeverity.UNSUITABLE -> Triple(Color(0xFFFFEBEE), StatusUnsuitable, StatusUnsuitable)
        BadgeSeverity.INFO -> Triple(Color(0xFFE3F2FD), StatusInfo, StatusInfo)
    }

    Row(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(9999.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showDot) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(dotColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(5.dp))
        }
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 14.sp
        )
    }
}
