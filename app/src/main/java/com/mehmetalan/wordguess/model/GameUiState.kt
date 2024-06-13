package com.mehmetalan.wordguess.model

data class GameUiState(
    val currentScrambledWord: String = "",
    val currentHintWord: String = "",
    val currentWordCount: Int = 1,
    val score: Int = 0,
    val isGuessedWordWrong: Boolean = false,
    val isGameOver: Boolean = false,
    var hintCount: Int = 3,
    var timeInitialCount: Int = 30,
    var selectedCategoryId: Int = 1,
    var selectedLevelId: Int = 1
)
