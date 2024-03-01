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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.clubwat.model.Event
import java.util.Calendar
import java.util.Date

@Composable
fun EventItem(event: Event, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { navController.navigate("club/${event.clubId}") }

    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = event.title, fontWeight = FontWeight.Bold)
            val descriptionToShow = if (event.description.length > 150) {
                "${event.description.take(75)}..."
            } else {
                event.description
            }
            Text(text = descriptionToShow)
        }
    }
}
