package com.example.btsallot.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration

private val AppColorScheme = lightColorScheme(
    primary = Blue600,
    onPrimary = SurfaceWhite,
    primaryContainer = BlueSurface,
    onPrimaryContainer = Blue700,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = BlueSurface,
    onSurfaceVariant = TextSecondary,
    onTertiary = TextTertiary,
    onTertiaryFixed = TextFieldLabel,
    onTertiaryFixedVariant = TextFieldText,
    outline = StatusFull,

)

@Composable
fun BTSAllotTheme(content: @Composable () -> Unit) {
    val configuration = LocalConfiguration.current

    // Pick dimensions based on screen height or width
    val dimensions = if (configuration.screenWidthDp < 360 || configuration.screenHeightDp < 700) {
        CompactDimensions
    } else {
        StandardDimensions
    }
    CompositionLocalProvider(LocalDimensions provides dimensions) {
        MaterialTheme(
            colorScheme = AppColorScheme,
            typography = AppTypography,
            content = content
        )
    }

}
