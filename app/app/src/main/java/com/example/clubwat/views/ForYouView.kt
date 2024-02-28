package com.example.clubwat.views
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.clubwat.viewmodels.ForYouViewModel

@Composable
fun ForYouView(
    viewModel: ForYouViewModel,
    navController: NavController
) {
    Text("FOR YOU")
}
