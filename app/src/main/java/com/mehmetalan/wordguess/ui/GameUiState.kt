package com.mehmetalan.wordguess.ui

data class GameUiState(
    val currentScrambledWord: String = "",
    val currentHintWord: String = "",
    val currentWordCount: Int = 1,
    val score: Int = 0,
    val isGuessedWordWrong: Boolean = false,
    val isGameOver: Boolean = false,
    var hintCount: Int = 3,
    var timeInitialCount: Int = 30,
)

