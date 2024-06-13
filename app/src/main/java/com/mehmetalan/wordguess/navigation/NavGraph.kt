package com.mehmetalan.wordguess.navigation

import GameViewModel
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.mehmetalan.wordguess.screens.GameScreen
import com.mehmetalan.wordguess.screens.HomeScreen
import com.mehmetalan.wordguess.screens.LoginScreen
import com.mehmetalan.wordguess.screens.RegisterScreen
import com.mehmetalan.wordguess.screens.ScoreScreen


@Composable
fun NavGraph(
    navController: NavHostController
) {
    val firebaseUser = FirebaseAuth.getInstance().currentUser
    val gameViewModel: GameViewModel = viewModel()
    NavHost(
        navController = navController,
        startDestination = if(firebaseUser != null) {"home"} else { "login" }
    ) {
        composable(route = "home") {
            HomeScreen(
                navController = navController,
                gameViewModel = gameViewModel
            )
        }
        composable(
            route = "game/{categoryId}/{levelId}",
            arguments = listOf(
                navArgument("categoryId") { type = NavType.IntType },
                navArgument("levelId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getInt("categoryId")
            val levelId = backStackEntry.arguments?.getInt("levelId")
            if (categoryId != null) {
                if (levelId != null) {
                    GameScreen(
                        navController = navController,
                        gameViewModel = gameViewModel,
                        categoryId = categoryId,
                        levelId = levelId
                    )
                }
            }
        }
        composable(route = "login") {
            LoginScreen(
                navController = navController
            )
        }
        composable(route = "register") {
            RegisterScreen(
                navController = navController
            )
        }
        composable(route = "rank") {
            ScoreScreen(
                navController = navController
            )
        }
    }
}