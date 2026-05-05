package com.androidforge.habitforge.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryVariant,
    onPrimaryContainer = DarkOnPrimary,
    secondary = DarkSecondary,
    onSecondary = DarkOnPrimary,
    secondaryContainer = DarkSecondary.copy(alpha = 0.2f),
    onSecondaryContainer = DarkOnPrimary,
    tertiary = DarkPrimary,
    onTertiary = DarkOnPrimary,
    tertiaryContainer = DarkPrimaryVariant,
    onTertiaryContainer = DarkOnPrimary,
    error = DarkError,
    onError = DarkOnPrimary,
    errorContainer = DarkError.copy(alpha = 0.2f),
    onErrorContainer = DarkOnPrimary,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkSurfaceVariant,
    inverseOnSurface = DarkBackground,
    inverseSurface = DarkOnBackground,
    inversePrimary = DarkSecondary,
    surfaceTint = DarkPrimary,
    outlineVariant = DarkSurface
)

// Not explicitly using light theme, but good to define if needed later.
private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryVariant,
    onPrimaryContainer = OnPrimary,
    secondary = Secondary,
    onSecondary = OnPrimary,
    secondaryContainer = Secondary.copy(alpha = 0.2f),
    onSecondaryContainer = OnPrimary,
    tertiary = Primary,
    onTertiary = OnPrimary,
    tertiaryContainer = PrimaryVariant,
    onTertiaryContainer = OnPrimary,
    error = Error,
    onError = OnPrimary,
    errorContainer = Error.copy(alpha = 0.2f),
    onErrorContainer = OnPrimary,
    background = Color(0xFFF5F5F5), // Lighter background for light theme
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE0E0E0),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF79747E),
    inverseOnSurface = Color(0xFFF4EFF4),
    inverseSurface = Color(0xFF313033),
    inversePrimary = Color(0xFFF2B8B5),
    surfaceTint = Primary,
    outlineVariant = Color(0xFFC4C6D0)
)

@Composable
fun HabitForgeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme // Using DarkColorScheme for both dark and light for consistent 'Cinematic Dark' theme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb() // Status bar matches background
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}