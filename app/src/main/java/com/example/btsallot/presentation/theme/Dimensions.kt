package com.example.btsallot.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Dimensions(
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp
)

val StandardDimensions = Dimensions(
    extraSmall = 4.dp,
    small = 8.dp,
    medium = 16.dp,
    large = 24.dp,
)

val CompactDimensions = Dimensions(
    extraSmall = 2.dp,
    small = 5.dp,
    medium = 10.dp,
    large = 16.dp,
)

val LocalDimensions = staticCompositionLocalOf { StandardDimensions }

val MaterialTheme.dimensions: Dimensions
@Composable
@ReadOnlyComposable
get() = LocalDimensions.current