package com.mehmetalan.wordguess.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun determineLevelColor(rank: Int, isLocked: Boolean): Color {
    return if (isLocked) {
        Color.DarkGray
    } else {
        when(rank) {
            1 -> Color(0xFF6B8E23)  // EASY
            2 -> Color(0xFFFFD700)  // MEDIUM
            3 -> Color(0xFFFF6347)  // HARD
            else -> Color(0xFF6B8E23)  // Default to EASY
        }
    }
}