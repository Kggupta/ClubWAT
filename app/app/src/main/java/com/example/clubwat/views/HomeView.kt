package com.example.clubwat.views
import HomeViewModel
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun HomeView(
    viewModel: HomeViewModel,
    navController: NavController
) {
    Text("HOME")
}
