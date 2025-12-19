package com.trustrepair.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    // Primary
    primary = TrustBlue,
    onPrimary = Color.White,
    primaryContainer = TrustBlueLight,
    onPrimaryContainer = TrustBlueDark,

    // Secondary (using success green for positive actions)
    secondary = SuccessGreen,
    onSecondary = Color.White,
    secondaryContainer = SuccessGreenLight,
    onSecondaryContainer = SuccessGreenDark,

    // Tertiary (using amber for warnings/attention)
    tertiary = WarningAmber,
    onTertiary = Color.White,
    tertiaryContainer = WarningAmberLight,
    onTertiaryContainer = WarningAmberDark,

    // Error
    error = ErrorRed,
    onError = Color.White,
    errorContainer = ErrorRedLight,
    onErrorContainer = ErrorRedDark,

    // Background
    background = Gray50,
    onBackground = Gray900,

    // Surface
    surface = Color.White,
    onSurface = Gray900,
    surfaceVariant = Gray100,
    onSurfaceVariant = Gray700,

    // Outline
    outline = Gray300,
    outlineVariant = Gray200,

    // Inverse
    inverseSurface = Gray900,
    inverseOnSurface = Gray100,
    inversePrimary = TrustBlueLight,

    // Scrim
    scrim = Color.Black.copy(alpha = 0.32f)
)

@Composable
fun TrustRepairTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.White.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = true
                isAppearanceLightNavigationBars = true
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
