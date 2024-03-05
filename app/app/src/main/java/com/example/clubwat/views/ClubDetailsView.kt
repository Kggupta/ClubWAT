package com.example.clubwat.views

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import com.example.clubwat.ui.theme.LightYellow
import com.example.clubwat.viewmodels.ClubDetailsViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
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

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon =
                {
                    IconButton(
                        onClick = { navController.popBackStack() }) {
                        Icon(
                            painterResource(id = R.drawable.baseline_arrow_back_24),
                            contentDescription = "Back"
                        )
                    }
                },
                title = {
                    Text(
                        text = viewModel.getClubTitle(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                backgroundColor = LightYellow,
                contentColor = Color.Black
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (club != null && club!!.isJoined) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Button(
                            onClick = {
                                viewModel.updateClubMembership()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .padding(end = 8.dp)
                        ) {
                            Text("Leave Club")
                        }
                        Button(
                            onClick = {
                                navController.navigate("discussion/${clubId}")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Gray
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                        ) {
                            Text("Discussion")
                        }
                    }
                } else if (club != null && club!!.isJoinPending) {
                    Button(
                        onClick = {
                            viewModel.updateClubMembership()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.LightGray
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
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LightOrange
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
    )
}
