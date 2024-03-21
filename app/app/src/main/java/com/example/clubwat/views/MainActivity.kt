package com.example.clubwat.views

import HomeViewModel
import LoginViewModel
import SearchViewModel
import SignUpViewModel
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.WindowManager
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
import com.example.clubwat.repository.UserRepository
import com.example.clubwat.ui.theme.ClubWATTheme
import com.example.clubwat.viewmodels.factories.HomeViewModelFactory
import com.example.clubwat.viewmodels.factories.LoginViewModelFactory
import com.example.clubwat.viewmodels.factories.SearchViewModelFactory
import com.example.clubwat.viewmodels.factories.SignUpViewModelFactory
import com.example.clubwat.views.NavigationBar.NavBar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userRepository: UserRepository

    override fun onResume() {
        super.onResume()
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClubWATTheme {
                val navController = rememberNavController()
                val currentUser by userRepository.currentUser

                val homeViewModel: HomeViewModel by viewModels { HomeViewModelFactory(userRepository) }
                val searchViewModel: SearchViewModel by viewModels { SearchViewModelFactory(userRepository) }
                val signUpViewModel: SignUpViewModel by viewModels { SignUpViewModelFactory(userRepository) }
                val loginViewModel: LoginViewModel by viewModels { LoginViewModelFactory(userRepository) }



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
                        NavHost(navController = navController, startDestination = "login") {
                            composable("signup") {
                                SignUpView(viewModel = signUpViewModel, navController = navController)
                            }
                            composable("login") {
                                LoginView(viewModel = loginViewModel, navController = navController)
                            }
                            composable("verification") {
                                CodeVerificationView(navController = navController)
                            }
                            composable("home") {
                                HomeView(viewModel = homeViewModel,
                                    navController = navController)
                            }
                            composable("forYou") {
                                ForYouView(navController = navController)
                            }
                            composable("profile") {
                                ProfileView(navController = navController)
                            }
                            composable("search") {
                                SearchView(viewModel = searchViewModel, navController = navController)
                            }
                            composable("club/{clubId}") { backStackEntry ->
                                ClubDetailsView(
                                    navController = navController,
                                    clubId = backStackEntry.arguments?.getString("clubId")
                                )
                            }
                            composable("discussion/{clubId}") { backStackEntry ->
                                ClubDiscussionView(
                                    navController = navController,
                                    clubId = backStackEntry.arguments?.getString("clubId")
                                )
                            }
                            composable("editprofile") {
                                EditProfileView(navController = navController)
                            }
                            composable("password") {
                                ChangePasswordView(navController = navController)
                            }
                            composable("friends") {
                                ManageFriendsView(navController = navController)
                            }
                            composable("interests") {
                                UserInterestsView(navController = navController)
                            }
                            composable("inbox") {
                                InboxView(navController = navController)
                            }
                            composable("event/{eventId}") {backStackEntry ->
                                EventDetailsView(
                                    navController = navController,
                                    eventId = backStackEntry.arguments?.getString("eventId"))
                            }
                            composable("event/{eventId}/eventDetails") {backStackEntry ->
                                EditEventDetailsView(
                                    navController = navController,
                                    eventId = backStackEntry.arguments?.getString("eventId"))
                            }
                            composable("club/{clubId}/event/new/{type}") {backStackEntry ->
                                AddEventView(
                                    navController = navController,
                                    clubId = backStackEntry.arguments?.getString("clubId"),
                                    type = backStackEntry.arguments?.getString("type"))
                            }
                            composable("club/{clubId}/management") {backStackEntry ->
                                ClubManagementView(
                                    navController = navController,
                                    clubId = backStackEntry.arguments?.getString("clubId"))
                            }
                            composable("club/{clubId}/management/users") {backStackEntry ->
                                ClubUserManagementView(
                                    navController = navController,
                                    clubId = backStackEntry.arguments?.getString("clubId"))
                            }
                            composable("club/{clubId}/management/clubDetails") {backStackEntry ->
                                EditClubDetailsView(
                                    navController = navController,
                                    clubId = backStackEntry.arguments?.getString("clubId"))
                            }
                        }
                    }
                }
            }
        }
    }
}
