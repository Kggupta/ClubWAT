package com.example.clubwat.views

import LoginViewModel
import androidx.navigation.NavController
import com.example.clubwat.viewmodels.ProfileViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.LinearGradient
import com.example.clubwat.R

@Composable
fun ProfileView(
    viewModel: ProfileViewModel,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(100.dp)
                .padding(16.dp)
        ) {
            // Replace with your image loading logic
            Image(
                painter = painterResource(id = R.drawable.waterloo_logo),
                contentDescription = "Profile Picture"
            )
        }
        viewModel.firstName?.let {
            Text(
                text = it.value,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Divider()
        ProfileOption("Edit Interests")
        ProfileOption("Edit Profile")
        ProfileOption("Edit Friends")
    }

}


@Composable
fun ProfileOption(text: String) {
    TextButton(
        onClick = { /* handle click */ },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(text, color = Color.Black)
    }
}
