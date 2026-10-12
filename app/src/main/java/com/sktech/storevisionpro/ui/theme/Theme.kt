package com.sktech.storevisionpro.ui.theme

import android.app.Activity
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = ElectricEmerald,
    onPrimary = OffWhitePearl,
    primaryContainer = EmeraldMuted,
    onPrimaryContainer = OffWhitePearl,
    secondary = PrecisionAzure,
    onSecondary = OffWhitePearl,
    secondaryContainer = AzureMuted,
    onSecondaryContainer = PrecisionAzure,
    tertiary = WarningAmber,
    onTertiary = OffWhitePearl,
    tertiaryContainer = AmberMuted,
    onTertiaryContainer = WarningAmber,
    background = SlateDark,
    onBackground = OffWhitePearl,
    surface = ZincCharcoal,
    onSurface = OffWhitePearl,
    surfaceVariant = ZincMetal,
    onSurfaceVariant = MutedSilver,
    surfaceContainer = ZincCharcoal,
    surfaceContainerHigh = ZincMetal,
    outline = SlateDivider,
    outlineVariant = SlateDivider,
    error = SignalCrimson,
    onError = OffWhitePearl,
    errorContainer = CrimsonMuted,
    onErrorContainer = SignalCrimson
)

private val LightColorScheme = lightColorScheme(
    primary = ElectricEmerald,
    onPrimary = SurfaceLight,
    primaryContainer = Color(0xFFD1FAE5),
    onPrimaryContainer = Color(0xFF065F46),
    secondary = Color(0xFF0284C7),
    onSecondary = SurfaceLight,
    background = CanvasLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceElevatedLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = DividerLight,
    error = SignalCrimson,
    onError = SurfaceLight
)

@Composable
fun StoreVisionProTheme(
    darkTheme: Boolean = true, // Cockpit Dark is the default signature retail mode
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.surface.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}