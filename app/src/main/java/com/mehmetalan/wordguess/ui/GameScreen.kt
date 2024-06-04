package com.mehmetalan.wordguess.ui

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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Output
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.mehmetalan.wordguess.R
import com.mehmetalan.wordguess.data.colorList
import com.mehmetalan.wordguess.ui.theme.AudioWide
import com.mehmetalan.wordguess.ui.theme.WordGuessTheme
import com.mehmetalan.wordguess.viewModels.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GameScreen(
    navController: NavHostController,
    gameViewModel: GameViewModel = viewModel(),
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
                            Icons.Outlined.Output,
                            contentDescription = "Logout Button",
                            tint = Color.Red
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            navController.navigate(route = "rank")
                        }
                    ) {
                        Text(
                            text = stringResource(id = R.string.qualifyin),
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = AudioWide
                        )
                    }
                }
            )
        }
    ) {innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = Brush.verticalGradient(colorList))
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
                        Button(
                            onClick = {
                                if (gameViewModel.userGuess.capitalize(Locale.ROOT) == gameViewModel.currentWord) {
                                    timer = 30
                                }
                                gameViewModel.checkUserGuess()
                                      },
                            enabled = if (isTimerStart) true else false,
                            modifier = Modifier
                                .bringIntoViewRequester(bringIntoViewRequester)
                        ) {
                            Text(
                                text = stringResource(R.string.submit),
                                fontSize = 16.sp
                            )
                        }
                        Button(
                            onClick = { gameViewModel.revealHint() },
                            enabled = gameUiState.hintCount != 0 && timer != 0,
                            modifier = Modifier
                                .bringIntoViewRequester(bringIntoViewRequester)
                        ) {
                            Text(
                                text = stringResource(id = R.string.give_tips),
                                fontSize = 16.sp
                            )
                        }
                        OutlinedButton(
                            onClick = {
                                gameViewModel.skipWord()
                                timer = 30
                                isTimerStart = true
                            },
                            modifier = Modifier
                                .bringIntoViewRequester(bringIntoViewRequester)
                        ) {
                            Text(
                                text = if (gameUiState.currentWordCount != 10) {
                                    stringResource(id = R.string.skip)} else {
                                    stringResource(id = R.string.game_finish)},
                                fontSize = 16.sp
                            )
                        }
                    }

                    if (gameUiState.isGameOver) {
                        FinalScoreDialog(
                            score = gameUiState.score,
                            onPlayAgain = { gameViewModel.resetGame() }
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
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.score, score),
            style = typography.headlineMedium,
            modifier = Modifier.padding(8.dp)
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

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isGuessWrong) colorScheme.error else Color(0xFF4EE1BE)
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
                    .background(colorScheme.surfaceTint)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                text = stringResource(R.string.word_count, wordCount),
                style = typography.titleMedium,
                color = colorScheme.onPrimary
            )
            Row (
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.time)
                )
                Text(
                    text = timer.toString()
                )
            }
            Text(
                text = currentScrambledWord,
                style = typography.displayMedium
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (timer == 0) {"Doğru Cevap: ${gameViewModel.currentWord}"} else {
                        stringResource(id = R.string.clue) + ":"},
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = if(timer == 0) {""} else {hintDashes},
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Text(
                text = stringResource(R.string.instructions, gameUiState.hintCount),
                textAlign = TextAlign.Center,
                style = typography.titleMedium
            )
            Text(
                text = stringResource(id = R.string.word_info),
                textAlign = TextAlign.Center,
                style = typography.titleMedium
            )
            Text(
                text = stringResource(id = R.string.hint_word_info),
                textAlign = TextAlign.Center,
                style = typography.titleMedium
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
                        Text(stringResource(R.string.wrong_guess))
                    } else {
                        Text(stringResource(R.string.enter_your_word))
                    }
                },
                isError = isGuessWrong,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onKeyboardDone() }
                )
            )
        }
    }
}

@Composable
private fun FinalScoreDialog(
    score: Int,
    onPlayAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activity = (LocalContext.current as Activity)

    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = stringResource(R.string.congratulations)) },
        text = { Text(text = stringResource(R.string.you_scored, score)) },
        modifier = modifier,
        dismissButton = {
            TextButton(
                onClick = {
                    activity.finish()
                }
            ) {
                Text(text = stringResource(R.string.exit))
            }
        },
        confirmButton = {
            TextButton(onClick = onPlayAgain) {
                Text(text = stringResource(R.string.play_again))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    WordGuessTheme {
        GameScreen(
            navController = rememberNavController()
        )
    }
}
