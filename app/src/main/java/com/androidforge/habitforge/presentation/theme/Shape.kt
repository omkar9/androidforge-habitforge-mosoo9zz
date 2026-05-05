package com.androidforge.habitforge.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp), // Button, Chip
    large = RoundedCornerShape(16.dp),  // Card
    extraLarge = RoundedCornerShape(24.dp) // BottomSheet top corners
)