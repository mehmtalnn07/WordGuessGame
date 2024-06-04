package com.mehmetalan.wordguess.screens

import android.annotation.SuppressLint
import android.provider.MediaStore.Audio
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.mehmetalan.wordguess.R
import com.mehmetalan.wordguess.common.PodiumColor
import com.mehmetalan.wordguess.model.User
import com.mehmetalan.wordguess.ui.GameViewModel
import com.mehmetalan.wordguess.ui.theme.AudioWide
import com.mehmetalan.wordguess.ui.theme.WordGuessTheme



@Composable
fun determinePodiumColor(rank: Int): PodiumColor {
    return when (rank) {
        1 -> PodiumColor.GOLD
        2 -> PodiumColor.SILVER
        3 -> PodiumColor.BRONZE
        else -> PodiumColor.OTHER
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ScoreScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    gameViewModel: GameViewModel = viewModel()
) {
    val userScores by gameViewModel.userScores.collectAsState()

    LaunchedEffect(Unit) {
        gameViewModel.fetchUserScores()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.scores),
                        fontWeight = FontWeight.ExtraBold,
                        style = TextStyle(
                            fontFamily = AudioWide,
                            fontSize = 30.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Back Button"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                itemsIndexed(userScores) { index, user ->
                    UserCard(
                        userName = user.userName,
                        userScore = user.userMaximumScore,
                        rank = index + 1
                    )
                }
            }
        }
    }
}

@Composable
fun UserCard(
    userName: String,
    userScore: String,
    rank: Int,
    modifier: Modifier = Modifier
) {
    val podiumColor = determinePodiumColor(rank)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = when (podiumColor) {
                PodiumColor.GOLD -> Color(0xFFFFD700)
                PodiumColor.SILVER -> Color(0xFFC0C0C0)
                PodiumColor.BRONZE -> Color(0xFFCD7F32)
                else -> MaterialTheme.colorScheme.secondaryContainer
            }
        ),
        modifier = modifier.padding(5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when(podiumColor) {
                    PodiumColor.GOLD -> painterResource(id = R.drawable.gold_medal)
                    PodiumColor.SILVER -> painterResource(id = R.drawable.silver_medal)
                    PodiumColor.BRONZE -> painterResource(id = R.drawable.bronz_medal)
                    else -> null
                }?.let {
                    Image(
                        painter = it,
                        contentDescription = "",
                        modifier = Modifier
                            .size(50.dp)
                    )
                }
                Text(
                    text = userName,
                    style = TextStyle(
                        fontFamily = AudioWide
                    )
                )
            }
            Text(
                text = userScore,
                style = TextStyle(
                    fontFamily = AudioWide
                )
            )
        }
    }
}
