
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.mehmetalan.wordguess.common.PointLimits
import com.mehmetalan.wordguess.model.GameUiState
import com.mehmetalan.wordguess.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

const val max_no_of_words = 10

class GameViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var wordList: MutableList<String> = mutableListOf()

    var userGuess by mutableStateOf("")
        private set

    private var usedWords: MutableList<String> = mutableListOf()
    var currentWord: String = ""
    private lateinit var hintWord: CharArray

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance()
    private val userReference = db.getReference("users")

    private val _userScores = MutableStateFlow<List<User>>(emptyList())
    val userScores: StateFlow<List<User>> = _userScores




    init {
        setCategoryAndLevel(1, 1)
    }

    suspend fun unlockLevel(categoryId: Int, levelId: Int): Boolean {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        return if (currentUser != null) {
            val userId = currentUser.uid
            val userReference = FirebaseDatabase.getInstance().getReference("users").child(userId)

            suspendCoroutine { continuation ->
                userReference.get().addOnSuccessListener { dataSnapshot ->
                    val user = dataSnapshot.getValue(User::class.java)
                    if (user != null) {
                        val isUnlocked = when (categoryId) {
                            1 -> when (levelId) {
                                1 -> false
                                2 -> user.fourEasyPoints.toInt() <= PointLimits.FOUR_WORDS_EASY_POINTS
                                3 -> user.fourMediumPoints.toInt() <= PointLimits.FOUR_WORDS_MEDIUM_POINTS
                                else -> true
                            }
                            2 -> when (levelId) {
                                1 -> false
                                2 -> user.fiveEasyPoints.toInt() <= PointLimits.FIVE_WORDS_EASY_POINTS
                                3 -> user.fiveMediumPoints.toInt() <= PointLimits.FIVE_WORDS_MEDIUM_POINTS
                                else -> true
                            }
                            3 -> when (levelId) {
                                1 -> false
                                2 -> user.sixEasyPoints.toInt() <= PointLimits.SIX_WORDS_EASY_POINTS
                                3 -> user.sixMediumPoints.toInt() <= PointLimits.SIX_WORDS_MEDIUM_POINTS
                                else -> true
                            }
                            4 -> when (levelId) {
                                1 -> false
                                2 -> user.sevenEasyPoints.toInt() <= PointLimits.SEVEN_WORDS_EASY_POINTS
                                3 -> user.sevenMediumPoints.toInt() <= PointLimits.SEVEN_WORDS_MEDIUM_POINTS
                                else -> true
                            }
                            else -> true
                        }
                        continuation.resume(isUnlocked)
                    } else {
                        continuation.resume(true)
                    }
                }.addOnFailureListener {
                    continuation.resume(false)
                }
            }
        } else {
            false
        }
    }

    suspend fun unlockCategory(categoryId: Int): Boolean {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        return if (currentUser != null) {
            val userId = currentUser.uid
            val userReference = FirebaseDatabase.getInstance().getReference("users").child(userId)

            suspendCoroutine { continuation ->
                userReference.get().addOnSuccessListener { dataSnapshot ->
                    val user = dataSnapshot.getValue(User::class.java)
                    if (user != null) {
                        val isUnlocked = when (categoryId) {
                            1 -> false
                            2 -> user.fourHardPoints.toInt() <= PointLimits.FOUR_WORDS_HARD_POINTS
                            3 -> user.fiveHardPoints.toInt() <= PointLimits.FIVE_WORDS_HARD_POINTS
                            4 -> user.sixHardPoints.toInt() <= PointLimits.SIX_WORDS_HARD_POINTS
                            else -> true
                        }
                        continuation.resume(isUnlocked)
                    } else {
                        continuation.resume(true)
                    }
                }.addOnFailureListener {
                    continuation.resume(false)
                }
            }
        } else {
            false
        }
    }


    fun fetchCurrentUser(onUserFetched: (User?) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid
            val userReference = FirebaseDatabase.getInstance().getReference("users").child(userId)

            userReference.get().addOnSuccessListener { dataSnapshot ->
                val user = dataSnapshot.getValue(User::class.java)
                onUserFetched(user)
            }.addOnFailureListener {
                Log.e("GameViewModel", "Failed to fetch current user: ${it.message}")
                onUserFetched(null)
            }
        } else {
            Log.e("GameViewModel", "Current user is null.")
            onUserFetched(null)
        }
    }

    private fun fetchWordsFromFirestore(
        documentName: String,
        onSuccess: (List<String>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val firestore = FirebaseFirestore.getInstance()
        val documentRef = firestore.collection("words").document(documentName)

        documentRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val wordList = document.get("0") as? List<String>
                    if (wordList != null) {
                        onSuccess(wordList)
                    } else {
                        onFailure(Exception("Field '0' not found or is not a list"))
                    }
                } else {
                    onFailure(Exception("Document does not exist"))
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
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
        if (wordList.isNotEmpty()) {
            usedWords.clear()
            _uiState.value = GameUiState(currentScrambledWord = pickRandomWordAndShuffle(), selectedCategoryId = _uiState.value.selectedCategoryId, selectedLevelId = _uiState.value.selectedLevelId)
            hintWord = CharArray(currentWord.length) { '-' }
            _uiState.update { currentState ->
                currentState.copy(currentHintWord = String(hintWord))
            }
        } else {
            Log.e("GameViewModel", "Kelime listesi boş, oyun başlatılamıyor.")
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

    fun setCategoryAndLevel(categoryId: Int, levelId: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedCategoryId = categoryId,
                selectedLevelId = levelId
            )
        }
        setCategoryWords(categoryId, levelId)
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
        if (usedWords.size >= max_no_of_words) {
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    score = updatedScore,
                    isGameOver = true
                )
            }
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
            val categoryId = _uiState.value.selectedCategoryId
            val levelId = _uiState.value.selectedLevelId
            val categoryLevelKey = getCategoryLevelKey(categoryId, levelId)
            val userMaxScoreRef = userReference.child(userId).child(categoryLevelKey)
            userMaxScoreRef.get().addOnSuccessListener { dataSnapshot ->
                val currentMaxScoreString = dataSnapshot.getValue(String::class.java)
                val currentMaxScore = currentMaxScoreString?.toIntOrNull() ?: 0
                if (newScore > currentMaxScore) {
                    userMaxScoreRef.setValue(newScore.toString())
                }
            }
        }
    }

    private fun getCategoryLevelKey(categoryId: Int, levelId: Int): String {
        return when (categoryId) {
            1 -> when (levelId) {
                1 -> "fourEasyPoints"
                2 -> "fourMediumPoints"
                3 -> "fourHardPoints"
                else -> "fourEasyPoints"
            }
            2 -> when (levelId) {
                1 -> "fiveEasyPoints"
                2 -> "fiveMediumPoints"
                3 -> "fiveHardPoints"
                else -> "fiveEasyPoints"
            }
            3 -> when (levelId) {
                1 -> "sixEasyPoints"
                2 -> "sixMediumPoints"
                3 -> "sixHardPoints"
                else -> "sixEasyPoints"
            }
            4 -> when (levelId) {
                1 -> "sevenEasyPoints"
                2 -> "sevenMediumPoints"
                3 -> "sevenHardPoints"
                else -> "sevenEasyPoints"
            }
            else -> "fourEasyPoints"
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
        val availableWords = wordList - usedWords.toSet()
        if (availableWords.isEmpty()) {
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    isGameOver = true
                )
            }
            return ""
        }

        currentWord = availableWords.random()
        usedWords.add(currentWord)
        return shuffleCurrentWord(currentWord)
    }

    fun setCategoryWords(categoryId: Int, levelId: Int) {
        val documentName = when (categoryId) {
            1 -> when (levelId) {
                1 -> "fourEasyWords"
                2 -> "fourMediumWords"
                3 -> "fourHardWords"
                else -> "fourEasyWords"
            }
            2 -> when (levelId) {
                1 -> "fiveEasyWords"
                2 -> "fiveMediumWords"
                3 -> "fiveHardWords"
                else -> "fiveEasyWords"
            }
            3 -> when (levelId) {
                1 -> "sixEasyWords"
                2 -> "sixMediumWords"
                3 -> "sixHardWords"
                else -> "sixEasyWords"
            }
            4 -> when (levelId) {
                1 -> "sevenEasyWords"
                2 -> "sevenMediumWords"
                3 -> "sevenHardWords"
                else -> "sevenEasyWords"
            }
            else -> "fourEasyWords"
        }

        fetchWordsFromFirestore(
            documentName,
            onSuccess = { words ->
                wordList = words.toMutableList()
                usedWords.clear()
                Log.e("wordSetViewModel", "wordSet: $wordList")
                resetGame()
            },
            onFailure = { exception ->
                Log.e("wordSetViewModel", "Error fetching words: ${exception.message}")
            }
        )
    }
}
