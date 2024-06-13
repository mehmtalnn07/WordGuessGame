package com.mehmetalan.wordguess.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.mehmetalan.wordguess.R
import com.mehmetalan.wordguess.ui.theme.AudioWide
import com.mehmetalan.wordguess.ui.theme.WordGuessTheme
import com.mehmetalan.wordguess.viewModels.AuthViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authViewModel: AuthViewModel = viewModel()
    val coroutineScope = rememberCoroutineScope()
    var passwordVisible by remember { mutableStateOf(false) }
    var inputsWrong by remember { mutableStateOf(false) }
    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.login),
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
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
                Column (
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 50.dp, start = 16.dp, end = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = {
                            Text(
                                text = stringResource(id = R.string.enter_your_mail),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = {
                            Text(
                                text = stringResource(id = R.string.enter_your_password),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Go
                        ),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    passwordVisible = !passwordVisible
                                }
                            ) {
                                if (password == "") {
                                    null
                                } else {
                                    Icon(
                                        imageVector = if (passwordVisible) { Icons.Outlined.Visibility } else { Icons.Outlined.VisibilityOff },
                                        contentDescription = "Visible Button"
                                    )
                                }
                            }
                        },
                        visualTransformation = if (passwordVisible) { VisualTransformation.None } else { PasswordVisualTransformation() },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    if (inputsWrong) {
                        Text(
                            text = stringResource(id = R.string.email_or_password_wrong),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                authViewModel.login(
                                    email = email,
                                    password = password,
                                    onSuccess = { navController.navigate(route = "home") },
                                    onFailure = {
                                        inputsWrong = true
                                    }
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(id = R.string.login),
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = AudioWide,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Row (
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.none_accoount_string),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        TextButton(
                            onClick = {
                                navController.navigate(route = "register")
                            }
                        ) {
                            Text(
                                text = stringResource(id = R.string.register_here),
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.Magenta
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    WordGuessTheme {
        LoginScreen(
         navController = rememberNavController()
        )
    }
}