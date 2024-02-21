package com.example.clubwat.views
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.clubwat.R
import com.example.clubwat.ui.theme.LightOrange
import com.example.clubwat.viewmodels.ClubDetailsViewModel
import com.example.clubwat.viewmodels.ForYouViewModel

@Composable
fun ClubDetailsView(
    viewModel: ClubDetailsViewModel,
    navController: NavController,
    clubId: String?
) {
    LaunchedEffect(Unit) {
        if (clubId != null) {
            viewModel.getClub(clubId)
        }
    }

    val club by viewModel.club.collectAsState()

    Box(modifier = Modifier
        .fillMaxSize()) {
        IconButton(modifier=Modifier.align(Alignment.TopStart), onClick= {navController.navigate("search")}) {
            Icon(
                painterResource(id = R.drawable.baseline_arrow_back_24),
                contentDescription = "Back"
            )
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Club Details",
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(150.dp)
                .padding(16.dp)
        ) {
            // get API to check if user has PP, if not, have an empty image
            Image(
                // change this to profile pic
                painter = painterResource(id = R.drawable.waterloocirclelogo),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(100.dp)
            )
        }
        Text(text = viewModel.getClubTitle(), fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        if (club != null && club!!.isJoined) {
            Button(
                onClick = {
                    viewModel.updateClubMembership()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red
                ),
                modifier = Modifier
                    .width(300.dp)
                    .height(40.dp)
            ) {
                Text("Leave Club")
            }
        } else if (club != null && club!!.isJoinPending) {
            Button(
                onClick = {
                    viewModel.updateClubMembership()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray
                ),
                modifier = Modifier
                    .width(300.dp)
                    .height(40.dp)
            ) {
                Text("Cancel Request")
            }
        } else if (club != null) {
            Button(
                onClick = {
                    viewModel.updateClubMembership()
                },
                colors = ButtonDefaults.buttonColors(containerColor = LightOrange
                ),
                modifier = Modifier
                    .width(300.dp)
                    .height(40.dp)
            ) {
                Text("Join Club")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = viewModel.getClubDescription(), fontSize = 18.sp, textAlign = TextAlign.Center)
    }
}
