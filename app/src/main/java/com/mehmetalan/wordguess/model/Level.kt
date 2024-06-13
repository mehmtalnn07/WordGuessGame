package com.mehmetalan.wordguess.model

data class Level(
    val levelId: Int,
    val levelName: String
)

val levelList = listOf(
    Level(
        levelId = 1,
        levelName = "Kolay"
    ),
    Level(
        levelId = 2,
        levelName = "Orta"
    ),
    Level(
        levelId = 3,
        levelName = "Zor"
    )
)
