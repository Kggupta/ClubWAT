package com.example.clubwat.views

import LoginViewModel
import SignUpViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clubwat.ui.theme.ClubWATTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.clubwat.viewmodels.CodeVerificationViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClubWATTheme {
                val navController = rememberNavController()
                val signUpViewModel = viewModel<SignUpViewModel>()
                val loginViewModel = viewModel<LoginViewModel>()
                val codeVerificationViewModel = viewModel<CodeVerificationViewModel>()

                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    NavHost(navController = navController, startDestination = "signup") {
                        composable("signup") {
                            SignUpView(viewModel = signUpViewModel, navController = navController)
                        }
                        composable("login") {
                            LoginView(viewModel = loginViewModel, navController = navController)
                        }
                        composable("verification") {
                            CodeVerificationView(viewModel = codeVerificationViewModel, navController = navController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
            text = "Hello $name!",
            modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ClubWATTheme {
        Greeting("Android")
    }
}