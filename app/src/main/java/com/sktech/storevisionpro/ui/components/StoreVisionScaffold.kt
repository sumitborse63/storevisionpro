package com.sktech.storevisionpro.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sktech.storevisionpro.domain.model.SyncState
import com.sktech.storevisionpro.ui.navigation.BottomNavTabs
import com.sktech.storevisionpro.ui.state.StoreUiState
import com.sktech.storevisionpro.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreVisionScaffold(
    uiState: StoreUiState,
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onOpenChat: () -> Unit,
    onOpenLanguage: () -> Unit,
    onOpenTools: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SlateDark,
                    titleContentColor = OffWhitePearl
                ),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column {
                            Text(
                                text = uiState.currentShopName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                ),
                                maxLines = 1
                            )
                            Text(
                                text = "${uiState.shopType.displayName} • ${uiState.currentRole.displayName}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MutedSilver,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                },
                actions = {
                    // Sync Status Pill Indicator
                    SyncStatusBadge(syncState = uiState.syncState, pendingCount = uiState.pendingSyncCount)

                    // Language Selector Action
                    IconButton(onClick = onOpenLanguage) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = "Language",
                            tint = MutedSilver
                        )
                    }

                    // Extra Tools & Settings
                    IconButton(onClick = onOpenTools) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Extra Tools",
                            tint = MutedSilver
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = ZincCharcoal,
                tonalElevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = SlateDivider)
            ) {
                BottomNavTabs.forEach { tab ->
                    val selected = currentRoute == tab.route
                    NavigationBarItem(
                        selected = selected,
                        onClick = { onNavigate(tab.route) },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                tint = if (selected) ElectricEmerald else MutedSilver
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selected) OffWhitePearl else MutedSilver
                                )
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = EmeraldMuted
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            // Floating AI Assistant Button (waveform / agentic prompt)
            FloatingActionButton(
                onClick = onOpenChat,
                containerColor = ElectricEmerald,
                contentColor = OffWhitePearl,
                shape = CircleShape,
                modifier = Modifier
                    .size(56.dp)
                    .border(1.dp, EmeraldBorder, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = "AI Voice Assistant & Chatbot",
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        containerColor = SlateDark
    ) { innerPadding ->
        content(innerPadding)
    }
}

@Composable
fun SyncStatusBadge(syncState: SyncState, pendingCount: Int) {
    val (color, text) = when (syncState) {
        SyncState.SYNCED -> ElectricEmerald to "Synced"
        SyncState.SYNCING -> WarningAmber to "Syncing ($pendingCount)"
        SyncState.OFFLINE_LOCAL -> SignalCrimson to "Local Safe"
        SyncState.CONFLICT -> WarningAmber to "Conflict"
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(ZincMetal)
            .border(1.dp, SlateDivider, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(if (syncState == SyncState.SYNCING) color.copy(alpha = alpha) else color)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                color = OffWhitePearl,
                fontSize = 11.sp
            )
        )
    }
}
