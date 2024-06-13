package com.mehmetalan.wordguess.screens

import GameViewModel
import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.mehmetalan.wordguess.R
import com.mehmetalan.wordguess.common.determineLevelColor
import com.mehmetalan.wordguess.model.Category
import com.mehmetalan.wordguess.model.Level
import com.mehmetalan.wordguess.model.User
import com.mehmetalan.wordguess.model.categoryList
import com.mehmetalan.wordguess.model.levelList
import com.mehmetalan.wordguess.ui.theme.AudioWide

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    navController: NavHostController,
    gameViewModel: GameViewModel
) {
    val expandedCategoryId = remember { mutableStateOf<Int?>(null) }
    var levelShow by remember { mutableStateOf(false) }
    val currentUser = remember { mutableStateOf<User?>(null) }

    LaunchedEffect(Unit) {
        gameViewModel.fetchCurrentUser { user ->
            if (user != null) {
                currentUser.value = user
            } else {
                Log.e("HomeScreen", "Kullanıcı Bulunamadı")
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.change_category),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                ),
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
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.secondaryContainer)
                .padding(innerPadding)
        ) {
            LazyColumn {
                items(categoryList) { category ->
                    val user = currentUser.value
                    var locked by remember { mutableStateOf(false) }

                    LaunchedEffect(category.categoryId) {
                        locked = gameViewModel.unlockCategory(category.categoryId)
                    }

                    CategoryCard(
                        category = category,
                        onCardClick = { cardId ->
                            if (expandedCategoryId.value == cardId) {
                                expandedCategoryId.value = null
                            } else {
                                expandedCategoryId.value = cardId
                            }
                            levelShow = !levelShow
                            if (levelShow) {
                                category.categoryIcon = Icons.Default.KeyboardArrowUp
                            } else {
                                category.categoryIcon = Icons.Default.KeyboardArrowDown
                            }
                        },
                        isExpanded = expandedCategoryId.value == category.categoryId,
                        gameViewModel = gameViewModel,
                        navController = navController,
                        locked = locked
                    )
                }
            }
        }
    }
}

@Composable
fun LevelCard(
    level: Level,
    onCardClick: (Int) -> Unit,
    rank: Int,
    isLocked: Boolean
) {
    val levelColor = determineLevelColor(rank = rank, isLocked = isLocked)
    Card (
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .height(50.dp)
            .clickable(onClick = { onCardClick(level.levelId) }),
        colors = CardDefaults.cardColors(
            containerColor = levelColor
        )
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = level.levelName,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = AudioWide,
                textAlign = TextAlign.Center,
                color = if (!isLocked) {
                    Color.White
                } else {
                    Color.LightGray
                }
            )
            if (isLocked) {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 16.dp)
                )
            }
        }
    }
}

@Composable
fun CategoryCard(
    category: Category,
    onCardClick: (Int) -> Unit,
    isExpanded: Boolean,
    gameViewModel: GameViewModel,
    navController: NavHostController,
    locked: Boolean
) {

    val context = LocalContext.current
    val currentUser = remember { mutableStateOf<User?>(null) }

    LaunchedEffect(Unit) {
        gameViewModel.fetchCurrentUser { user ->
            if ( user != null ) {
                currentUser.value = user
            } else {
                Log.e("HomeScreen", "Kullanıcı bulunurken bir hata oluştu.")
            }
        }
    }

    Column {
        Card (
            modifier = Modifier
                .padding(10.dp)
                .clickable(
                    onClick = {
                        if (!locked) {
                            onCardClick(category.categoryId)
                        } else {
                            Toast
                                .makeText(context, "Bu kategori Kilitli.", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                ),
            colors = CardDefaults.cardColors(
                containerColor = if (!locked) {
                    MaterialTheme.colorScheme.inversePrimary
                } else {
                    Color.DarkGray
                }
            )
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Image(
                    painter = painterResource(id = category.categoryImage),
                    contentDescription = "Category Image",
                    modifier = Modifier
                        .size(90.dp)
                        .padding(5.dp)
                )
                Column {
                    Text(
                        text = category.categoryName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = AudioWide,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    if (locked) {
                        Text(
                            text = "Bu aşama henüz açık değil.",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                Icon(
                    imageVector = if (!locked) {
                        category.categoryIcon
                    } else {
                        Icons.Outlined.Lock
                    },
                    contentDescription = ""
                )
            }
        }
        if (isExpanded) {
            levelList.forEach { level ->
                val user = currentUser.value
                var levelLocked by remember { mutableStateOf(false) }
                LaunchedEffect(level.levelId) {
                    levelLocked = gameViewModel.unlockLevel(category.categoryId, level.levelId)
                }
                LevelCard(
                    level = level,
                    onCardClick = { levelId ->
                        if (!levelLocked) {
                            gameViewModel.setCategoryWords(category.categoryId, levelId)
                            gameViewModel.setCategoryAndLevel(categoryId = category.categoryId, levelId = levelId)
                            navController.navigate(route = "game/${category.categoryId}/${levelId}")
                        } else {
                            Toast.makeText(context, "Bu seviye şu an kilitli", Toast.LENGTH_SHORT).show()
                        }
                    },
                    rank = level.levelId,
                    isLocked = levelLocked
                )
            }
        }
    }
}
