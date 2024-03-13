package com.example.clubwat.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dashedBorder

@Composable
fun AddEventItem(navController: NavController, clubId: String?, isClubPaid: Boolean) {
    Column(
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .dashedBorder(
                    width = 2.dp,
                    color = Color.DarkGray,
                    shape = MaterialTheme.shapes.medium, on = 10.dp, off = 6.dp
                )
                .clickable { navController.navigate("event/${clubId}/${isClubPaid}/new") },
            shape = RoundedCornerShape(8.dp)
        ) {
            Box(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Add Event",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
