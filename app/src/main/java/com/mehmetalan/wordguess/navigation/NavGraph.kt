package com.mehmetalan.wordguess.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseAuth
import com.mehmetalan.wordguess.screens.LoginScreen
import com.mehmetalan.wordguess.screens.RegisterScreen
import com.mehmetalan.wordguess.screens.ScoreScreen
import com.mehmetalan.wordguess.ui.GameScreen


@Composable
fun NavGraph(
    navController: NavHostController
) {
    val firebaseUser = FirebaseAuth.getInstance().currentUser
    NavHost(
        navController = navController,
        startDestination = if(firebaseUser != null) {"home"} else { "login" }
    ) {
        composable(route = "home") {
            GameScreen(
                navController = navController
            )
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