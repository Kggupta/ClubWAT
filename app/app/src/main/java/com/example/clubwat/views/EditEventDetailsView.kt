package com.example.clubwat.views

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.clubwat.viewmodels.EditEventDetailsViewModel

@Composable
fun EditEventDetailsView(
    viewModel: EditEventDetailsViewModel,
    navController: NavController,
    eventId: String?
) {
    Column {
        Text("Hi")
    }
}
