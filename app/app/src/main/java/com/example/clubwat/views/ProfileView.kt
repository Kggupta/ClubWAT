package com.example.clubwat.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAddAlt1
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.clubwat.R
import com.example.clubwat.viewmodels.ProfileViewModel


@Composable
fun ProfileView(
    viewModel: ProfileViewModel,
    navController: NavController
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Place the logout link/button in the top right corner
        Text(
            text = "Logout",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .clickable {
                    viewModel.logout()
                    navController.navigate("login")
                }
                .padding(16.dp), // Add padding for the clickable area
            fontSize = 18.sp,
        )
    }
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
                .size(130.dp)
                .padding(16.dp)
        ) {
            // Replace with your image loading logic
            Image(
                // change this to profile pic
                painter = painterResource(id = R.drawable.profile),
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
        Spacer(modifier = Modifier.height(20.dp))
        TextWithIcon("Edit Interests", Icons.Default.Edit,  onClick = {
            navController.navigate("edit_interests")
        })
        Spacer(modifier = Modifier.height(20.dp))
        TextWithIcon("Edit Profile", Icons.Default.Person,  onClick = {
            navController.navigate("edit_profile")
        })
        Spacer(modifier = Modifier.height(20.dp))
        TextWithIcon("Edit Friends", Icons.Default.PersonAddAlt1,  onClick = {
            navController.navigate("edit_friends")
        })
    }

}


@Composable
fun TextWithIcon(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable(onClick = onClick) ) {
        Text(text = text)
        Spacer(modifier = Modifier.width(4.dp)) 
        Icon(
            imageVector = icon,
            contentDescription = ""
        )
    }
}
