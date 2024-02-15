package com.example.clubwat.views

import HomeViewModel
import LoginViewModel
import SignUpViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.clubwat.ui.theme.ClubWATTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.clubwat.model.UserRepository
import com.example.clubwat.viewmodels.CodeVerificationViewModel
import com.example.clubwat.viewmodels.factories.CodeVerificationViewModelFactory
import com.example.clubwat.viewmodels.factories.HomeViewModelFactory
import com.example.clubwat.viewmodels.factories.LoginViewModelFactory
import com.example.clubwat.viewmodels.factories.SignUpViewModelFactory

class MainActivity : ComponentActivity() {
    private val userRepository by lazy { UserRepository() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClubWATTheme {
                val navController = rememberNavController()
                val signUpViewModel: SignUpViewModel by viewModels { SignUpViewModelFactory(userRepository) }
                val loginViewModel: LoginViewModel by viewModels { LoginViewModelFactory(userRepository) }
                val codeVerificationViewModel: CodeVerificationViewModel by viewModels { CodeVerificationViewModelFactory(userRepository) }
                val homeViewModel: HomeViewModel by viewModels { HomeViewModelFactory(userRepository) }

                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    NavHost(navController = navController, startDestination = "login") {
                        composable("signup") {
                            SignUpView(viewModel = signUpViewModel, navController = navController)
                        }
                        composable("login") {
                            LoginView(viewModel = loginViewModel, navController = navController)
                        }
                        composable("verification") {
                            CodeVerificationView(viewModel = codeVerificationViewModel, navController = navController)
                        }
                        composable("home") {
                            HomeView(viewModel = homeViewModel, navController = navController)
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
