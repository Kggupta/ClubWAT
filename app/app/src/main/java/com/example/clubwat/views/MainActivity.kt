package com.example.clubwat.views

import HomeViewModel
import LoginViewModel
import SearchViewModel
import SignUpViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.clubwat.model.UserRepository
import com.example.clubwat.ui.theme.ClubWATTheme
import com.example.clubwat.viewmodels.ClubDetailsViewModel
import com.example.clubwat.viewmodels.CodeVerificationViewModel
import com.example.clubwat.viewmodels.ForYouViewModel
import com.example.clubwat.viewmodels.ProfileViewModel
import com.example.clubwat.viewmodels.factories.ClubDetailsViewModelFactory
import com.example.clubwat.viewmodels.factories.CodeVerificationViewModelFactory
import com.example.clubwat.viewmodels.factories.ForYouViewModelFactory
import com.example.clubwat.viewmodels.factories.HomeViewModelFactory
import com.example.clubwat.viewmodels.factories.LoginViewModelFactory
import com.example.clubwat.viewmodels.factories.ProfileViewModelFactory
import com.example.clubwat.viewmodels.factories.SearchViewModelFactory
import com.example.clubwat.viewmodels.factories.SignUpViewModelFactory
import com.example.clubwat.views.NavigationBar.NavBar

class MainActivity : ComponentActivity() {
    private val userRepository by lazy { UserRepository() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClubWATTheme {
                val navController = rememberNavController()
                val currentUser by userRepository.currentUser
                val signUpViewModel: SignUpViewModel by viewModels { SignUpViewModelFactory(userRepository) }
                val loginViewModel: LoginViewModel by viewModels { LoginViewModelFactory(userRepository) }
                val codeVerificationViewModel: CodeVerificationViewModel by viewModels { CodeVerificationViewModelFactory(userRepository) }
                val homeViewModel: HomeViewModel by viewModels { HomeViewModelFactory(userRepository) }
                val forYouViewModel: ForYouViewModel by viewModels { ForYouViewModelFactory(userRepository) }
                val profileViewModel: ProfileViewModel by viewModels { ProfileViewModelFactory(userRepository) }
                val searchViewModel: SearchViewModel by viewModels { SearchViewModelFactory(userRepository) }
                val clubDetailsViewModel: ClubDetailsViewModel by viewModels { ClubDetailsViewModelFactory(userRepository) }

                Scaffold(
                    bottomBar = {
                        if (currentUser?.userId != null) {
                            NavBar(navController)
                        }
                    }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = Color.White
                    ) {
                        NavHost(navController = navController, startDestination = "profile") {
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
                            composable("forYou") {
                                ForYouView(viewModel = forYouViewModel, navController = navController)
                            }
                            composable("profile") {
                                ProfileView(viewModel = profileViewModel, navController = navController)
                            }
                            composable("search") {
                                SearchView(viewModel = searchViewModel, navController = navController)
                            }
                            composable("club/{clubId}") { backStackEntry ->
                                ClubDetailsView(
                                    viewModel = clubDetailsViewModel,
                                    navController = navController,
                                    clubId = backStackEntry.arguments?.getString("clubId")
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
