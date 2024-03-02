package com.example.clubwat.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.clubwat.model.EventType
import com.example.clubwat.model.EventWrapper

@Composable
fun EventItem(eventWrapper: EventWrapper, navController: NavController) {
    val event = eventWrapper.event
    val type = eventWrapper.type
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { navController.navigate("club/${event.clubId}") }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = event.title, fontWeight = FontWeight.Bold)
                Icon(
                    imageVector = getIcon(type),
                    contentDescription = "Icon"
                )
            }
            val descriptionToShow = if (event.description.length > 150) {
                "${event.description.take(75)}..."
            } else {
                event.description
            }
            Text(text = descriptionToShow)
        }
    }
}

fun getIcon(type: Int?): ImageVector {
    return when (type) {
        EventType.ATTEND.value -> {
            Icons.Default.Done
        }
        EventType.BOOKMARK.value -> {
            Icons.Default.Bookmark
        }
        else -> {
            Icons.Default.Groups
        }
    }
}
