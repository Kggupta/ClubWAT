package com.example.clubwat.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.clubwat.model.Club

@Composable
fun ClubItem(club: Club, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { navController.navigate("club/${club.id}") }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = club.title, fontWeight = FontWeight.Bold)
            val descriptionToShow = if (club.description.length > 150) {
                "${club.description.take(75)}..."
            } else {
                club.description
            }
            Text(text = descriptionToShow)
        }
    }
}