package com.mehmetalan.wordguess.common

import androidx.compose.runtime.Composable

enum class PodiumColor {
    GOLD, SILVER, BRONZE, OTHER
}

@Composable
fun determinePodiumColor(rank: Int): PodiumColor {
    return when (rank) {
        1 -> PodiumColor.GOLD
        2 -> PodiumColor.SILVER
        3 -> PodiumColor.BRONZE
        else -> PodiumColor.OTHER
    }
}