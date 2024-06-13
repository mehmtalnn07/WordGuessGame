package com.mehmetalan.wordguess.screens

import GameViewModel
import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.mehmetalan.wordguess.R
import com.mehmetalan.wordguess.model.GameUiState
import com.mehmetalan.wordguess.viewModels.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GameScreen(
    navController: NavHostController,
    gameViewModel: GameViewModel,
    categoryId: Int,
    levelId: Int
) {
    val gameUiState by gameViewModel.uiState.collectAsState()
    val mediumPadding = dimensionResource(R.dimen.padding_medium)
    val authViewModel: AuthViewModel = viewModel()
    val coroutineScope = rememberCoroutineScope()
    var timer by remember { mutableStateOf(30) }
    var isTimerStart by remember { mutableStateOf(true) }
    val bringIntoViewRequester = BringIntoViewRequester()

    LaunchedEffect(key1 = timer, key2 = isTimerStart) {
        if (isTimerStart && timer > 0) {
            delay(1000)
            timer--
        } else if (timer == 0) {
            isTimerStart = false
        }
    }

    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.title_text)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                ),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                authViewModel.signOut {
                                    navController.navigate("login") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }
                            }
                        }
                    ) {
                        Icon(
                            Icons.Outlined.Logout,
                            contentDescription = "Logout Button",
                            tint = Color.Red
                        )
                    }
                },
            )
        }
    ) {innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .safeDrawingPadding(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(modifier = Modifier.size(20.dp))
                    GameStatus(score = gameUiState.score)
                    GameLayout(
                        onUserGuessChanged = { gameViewModel.updateUserGuess(it) },
                        wordCount = gameUiState.currentWordCount,
                        userGuess = gameViewModel.userGuess,
                        onKeyboardDone = { gameViewModel.checkUserGuess() },
                        currentScrambledWord = gameUiState.currentScrambledWord,
                        isGuessWrong = gameUiState.isGuessedWordWrong,
                        currentHintWord = gameUiState.currentHintWord,
                        timer = timer,
                        gameViewModel = gameViewModel,
                        gameUiState = gameUiState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(mediumPadding)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(mediumPadding),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (gameViewModel.userGuess.capitalize(Locale.ROOT) == gameViewModel.currentWord) {
                                    timer = 30
                                }
                                gameViewModel.checkUserGuess()
                            },
                            enabled = if (isTimerStart) true else false,
                            modifier = Modifier
                                .bringIntoViewRequester(bringIntoViewRequester),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.background
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.submit),
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        OutlinedButton(
                            onClick = { gameViewModel.revealHint() },
                            enabled = gameUiState.hintCount != 0 && timer != 0,
                            modifier = Modifier
                                .bringIntoViewRequester(bringIntoViewRequester),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.background
                            )
                        ) {
                            Text(
                                text = stringResource(id = R.string.give_tips),
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        OutlinedButton(
                            onClick = {
                                gameViewModel.skipWord()
                                timer = 30
                                isTimerStart = true
                            },
                            modifier = Modifier
                                .bringIntoViewRequester(bringIntoViewRequester),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.background,

                                )
                        ) {
                            Text(
                                text = if (gameUiState.currentWordCount != 10) {
                                    stringResource(id = R.string.skip)} else {
                                    stringResource(id = R.string.game_finish)},
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    if (gameUiState.isGameOver) {
                        FinalScoreDialog(
                            score = gameUiState.score,
                            onPlayAgain = {
                                gameViewModel.resetGame()
                                navController.navigate(route = "home")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GameStatus(score: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Text(
            text = stringResource(R.string.score, score),
            style = typography.headlineMedium,
            modifier = Modifier.padding(8.dp),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GameLayout(
    currentScrambledWord: String,
    wordCount: Int,
    isGuessWrong: Boolean,
    userGuess: String,
    onUserGuessChanged: (String) -> Unit,
    onKeyboardDone: () -> Unit,
    currentHintWord: String,
    timer: Int,
    gameViewModel: GameViewModel,
    gameUiState: GameUiState,
    modifier: Modifier = Modifier
) {
    val mediumPadding = dimensionResource(R.dimen.padding_medium)
    val hintDashes = currentHintWord.replace("", " ").trim()

    val bringIntoViewRequester = BringIntoViewRequester()
    val coroutineScope = rememberCoroutineScope()

    val timerColor = if (timer <= 10) Color.Red else MaterialTheme.colorScheme.onBackground

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isGuessWrong) colorScheme.error else MaterialTheme.colorScheme.background
        )
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(mediumPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(mediumPadding)
        ) {
            Text(
                modifier = Modifier
                    .clip(shapes.medium)
                    .background(colorScheme.background)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                text = stringResource(R.string.word_count, wordCount),
                style = typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Row (
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.time),
                    color = timerColor
                )
                Text(
                    text = timer.toString(),
                    color = timerColor
                )
            }
            Text(
                text = currentScrambledWord,
                style = typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (timer == 0) {"Doğru Cevap: ${gameViewModel.currentWord}"} else {
                        stringResource(id = R.string.clue) + ":"},
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = if(timer == 0) {""} else {hintDashes},
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = stringResource(R.string.instructions, gameUiState.hintCount),
                textAlign = TextAlign.Center,
                style = typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = stringResource(id = R.string.word_info),
                textAlign = TextAlign.Center,
                style = typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = stringResource(id = R.string.hint_word_info),
                textAlign = TextAlign.Center,
                style = typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            OutlinedTextField(
                value = userGuess,
                singleLine = true,
                shape = shapes.large,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusEvent { event ->
                        if (event.isFocused) {
                            coroutineScope.launch {
                                bringIntoViewRequester.bringIntoView()
                            }
                        }
                    },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colorScheme.surface,
                    unfocusedContainerColor = colorScheme.surface,
                    disabledContainerColor = colorScheme.surface,
                ),
                onValueChange = onUserGuessChanged,
                label = {
                    if (isGuessWrong) {
                        Text(stringResource(R.string.wrong_guess), color = MaterialTheme.colorScheme.onBackground)
                    } else {
                        Text(stringResource(R.string.enter_your_word), color = MaterialTheme.colorScheme.onBackground)
                    }
                },
                isError = isGuessWrong,
            )
        }
    }
}

@Composable
private fun FinalScoreDialog(
    score: Int,
    onPlayAgain: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val activity = (LocalContext.current as Activity)

    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = stringResource(R.string.congratulations), color = MaterialTheme.colorScheme.onBackground) },
        text = { Text(text = stringResource(R.string.you_scored, score), color = MaterialTheme.colorScheme.onBackground) },
        modifier = modifier,
        dismissButton = {
            TextButton(
                onClick = {
                    activity.finish()
                }
            ) {
                Text(text = stringResource(R.string.exit), color = MaterialTheme.colorScheme.onBackground)
            }
        },
        confirmButton = {
            TextButton(onClick = onPlayAgain) {
                Text(text = stringResource(R.string.play_again), color = MaterialTheme.colorScheme.onBackground)
            }
        }
    )
}
