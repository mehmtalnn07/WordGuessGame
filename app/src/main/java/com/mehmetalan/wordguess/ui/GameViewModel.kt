package com.mehmetalan.wordguess.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mehmetalan.wordguess.data.fourWords
import com.mehmetalan.wordguess.data.max_no_of_words
import com.mehmetalan.wordguess.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class GameViewModel : ViewModel() {

    // Game UI state
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    var userGuess by mutableStateOf("")
        private set

    // Set of words used in the game
    private var usedWords: MutableSet<String> = mutableSetOf()
    var currentWord: String = ""
    private lateinit var hintWord: CharArray

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance()
    private val userReference = db.getReference("users")

    private val _userScores = MutableStateFlow<List<User>>(emptyList())
    val userScores: StateFlow<List<User>> = _userScores


    init {
        resetGame()
    }

    fun fetchUserScores() {
        viewModelScope.launch {
            val db = FirebaseDatabase.getInstance().getReference("users")
            db.get().addOnSuccessListener { dataSnapshot ->
                val scoresList = mutableListOf<User>()
                for (userSnapshot in dataSnapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    user?.let {
                        scoresList.add(it)
                    }
                }
                _userScores.value = scoresList.sortedByDescending { it.userMaximumScore.toIntOrNull() ?: 0 }
            }
        }
    }

    fun resetGame() {
        usedWords.clear()
        _uiState.value = GameUiState(currentScrambledWord = pickRandomWordAndShuffle())
        hintWord = CharArray(currentWord.length) { '-' }
        _uiState.update { currentState ->
            currentState.copy(currentHintWord = String(hintWord))
        }
    }

    fun updateUserGuess(guessedWord: String) {
        userGuess = guessedWord
    }

    fun checkUserGuess() {
        if (userGuess.equals(currentWord, ignoreCase = true)) {
            val hintsUsed = hintWord.count { it != '-' }
            val updatedScore = _uiState.value.score + (currentWord.length * 10) - (hintsUsed * 10)
            updateGameState(updatedScore)
            _uiState.update { currentState ->
                currentState.copy(hintCount = 3)
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(isGuessedWordWrong = true)
            }
        }
        updateUserGuess("")
    }

    fun skipWord() {
        updateGameState(_uiState.value.score)
        updateUserGuess("")
        _uiState.update {
            it.copy(hintCount = 3)
        }
    }

    fun revealHint() {
        if (_uiState.value.hintCount != 0) {
            val hiddenIndices = hintWord.indices.filter { hintWord[it] == '-' }
            if (hiddenIndices.isNotEmpty()) {
                val revealIndex = hiddenIndices.random()
                hintWord[revealIndex] = currentWord[revealIndex]
                _uiState.update { currentState ->
                    currentState.copy(
                        currentHintWord = String(hintWord),
                        hintCount = currentState.hintCount - 1
                    )
                }
            }
        }
    }

    private fun updateGameState(updatedScore: Int) {
        if (usedWords.size == max_no_of_words) {
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    score = updatedScore,
                    isGameOver = true
                )
            }
            // Oyunu bitir ve kullanıcı skorunu güncelle
            updateMaxScoreIfNeeded(updatedScore)
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    currentScrambledWord = pickRandomWordAndShuffle(),
                    currentWordCount = currentState.currentWordCount.inc(),
                    score = updatedScore,
                    hintCount = 0
                )
            }
            hintWord = CharArray(currentWord.length) { '-' }
            _uiState.update { currentState ->
                currentState.copy(currentHintWord = String(hintWord))
            }
        }
    }

    private fun updateMaxScoreIfNeeded(newScore: Int) {
        val user = auth.currentUser
        user?.let {
            val userId = it.uid
            val userMaxScoreRef = userReference.child(userId).child("userMaximumScore")
            userMaxScoreRef.get().addOnSuccessListener { dataSnapshot ->
                val currentMaxScoreString = dataSnapshot.getValue(String::class.java)
                val currentMaxScore = currentMaxScoreString?.toIntOrNull() ?: 0
                if (newScore > currentMaxScore) {
                    userMaxScoreRef.setValue(newScore.toString())
                }
            }
        }
    }

    private fun shuffleCurrentWord(word: String): String {
        val tempWord = word.toCharArray()
        tempWord.shuffle()
        while (String(tempWord) == word) {
            tempWord.shuffle()
        }
        return String(tempWord)
    }

    private fun pickRandomWordAndShuffle(): String {
        currentWord = fourWords.random()
        return if (usedWords.contains(currentWord)) {
            pickRandomWordAndShuffle()
        } else {
            usedWords.add(currentWord)
            shuffleCurrentWord(currentWord)
        }
    }
}
