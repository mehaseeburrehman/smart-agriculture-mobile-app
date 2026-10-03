package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CenterFocusStrong
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.navigation.Screen
import com.example.ui.theme.AgriPrimary
import com.example.ui.theme.TextSecondary

sealed class BottomNavItem(
    val route: String,
    val titleRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    object Home : BottomNavItem(
        route = Screen.Home.route,
        titleRes = R.string.nav_home,
        selectedIcon = Icons.Filled.Eco,
        unselectedIcon = Icons.Outlined.Eco,
        testTag = "nav_home"
    )

    object Scan : BottomNavItem(
        route = Screen.Scan.route,
        titleRes = R.string.nav_scan,
        selectedIcon = Icons.Filled.CenterFocusStrong,
        unselectedIcon = Icons.Outlined.CenterFocusStrong,
        testTag = "nav_scan"
    )

    object Advisories : BottomNavItem(
        route = Screen.Advisories.route,
        titleRes = R.string.nav_advisories,
        selectedIcon = Icons.Filled.Shield,
        unselectedIcon = Icons.Outlined.Shield,
        testTag = "nav_advisories"
    )

    object History : BottomNavItem(
        route = Screen.History.route,
        titleRes = R.string.nav_history,
        selectedIcon = Icons.Filled.CalendarMonth,
        unselectedIcon = Icons.Outlined.CalendarMonth,
        testTag = "nav_history"
    )

    companion object {
        val items = listOf(Home, Scan, Advisories, History)
    }
}

@Composable
fun AppBottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 8.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem.items.forEach { item ->
                val isSelected = currentRoute == item.route
                val interactionSource = remember { MutableInteractionSource() }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = ripple(bounded = false, radius = 28.dp),
                            onClick = { onNavigate(item.route) }
                        )
                        .testTag(item.testTag),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isSelected) AgriPrimary.copy(alpha = 0.12f) else Color.Transparent,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = stringResource(id = item.titleRes),
                            tint = if (isSelected) AgriPrimary else TextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Text(
                        text = stringResource(id = item.titleRes),
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) AgriPrimary else TextSecondary
                    )
                }
            }
        }
    }
}
