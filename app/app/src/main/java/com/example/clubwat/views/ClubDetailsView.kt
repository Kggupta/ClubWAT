package com.example.clubwat.views

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.AlertDialog
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.clubwat.R
import com.example.clubwat.model.EventWrapper
import com.example.clubwat.ui.theme.LightOrange
import com.example.clubwat.ui.theme.LightYellow
import com.example.clubwat.viewmodels.ClubDetailsViewModel

@OptIn(ExperimentalLayoutApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ClubDetailsView(
    viewModel: ClubDetailsViewModel,
    navController: NavController,
    clubId: String?
) {
    LaunchedEffect(Unit) {
        viewModel.getFriends()
        if (clubId != null) {
            viewModel.getClub(clubId)
        }
    }

    val friends by viewModel.friends.collectAsState()
    val club by viewModel.club.collectAsState()
    var showClubDetailsView by remember { mutableStateOf(false) }
    var showShareView by remember { mutableStateOf(false) }

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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (club != null) {
                        Button(onClick = { viewModel.updateClubMembership() }, colors = ButtonDefaults.buttonColors(
                            containerColor = if (!club!!.isJoinPending && !club!!.isJoined) LightOrange else Color.LightGray,
                            contentColor = if (!club!!.isJoinPending && !club!!.isJoined) Color.White else Color.Black
                        )) {
                            Icon(if (club!!.isJoined) {
                                Icons.AutoMirrored.Filled.ExitToApp
                            } else if (club!!.isJoinPending) {
                                Icons.Filled.Cancel
                            } else {
                                Icons.Filled.Add
                            }, contentDescription = "Leave Club")
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        if (club!!.isJoined) {
                            Button(onClick = {
                                navController.navigate("discussion/${clubId}")
                            }, colors = ButtonDefaults.buttonColors(
                                containerColor = LightOrange
                            )) {
                                Icon(Icons.Filled.ChatBubble, contentDescription = "Discussion")
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                        }
                        Button(onClick = {
                            showShareView = true
                        }, colors = ButtonDefaults.buttonColors(
                            containerColor = LightOrange
                        )) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Share")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                ClickableText(modifier = Modifier
                    .background(
                        Color.LightGray,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp),
                    text = AnnotatedString(viewModel.getClubDescription().take(75) + "..."),
                    style= TextStyle(textAlign = TextAlign.Center),
                    onClick = {
                        showClubDetailsView = true
                })
                Spacer(modifier = Modifier.height(16.dp))
                Column(modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        text = "Club Events"
                    )
                    if (club != null) {
                        LazyColumn {
                            items(club!!.events) { event ->
                                Row {
                                    val eventWrapper = EventWrapper(event)
                                    EventItem(eventWrapper = eventWrapper, navController = navController)
                                }
                            }
                        }
                    }
                }
            }
        }
    )

    if (showShareView) {
        ShareDialog(friends = friends, dismissCallback = { showShareView = false }, chooseFriendCallback = {
            showShareView = false
            viewModel.shareClub(it)
        })
    }

    if (showClubDetailsView) {
        AlertDialog(
            title = {
                Text(text = viewModel.getClubTitle(), textAlign = TextAlign.Center)
            },
            text = {
                Column {
                    Text(text = viewModel.getClubDescription())
                    Spacer(modifier = Modifier.padding(8.dp))
                    FlowRow(modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)) {
                        if (club != null) {
                            club!!.categories.forEach { category ->
                                Text(
                                    text = category.name,
                                    modifier = Modifier
                                        .background(
                                            color = LightOrange,
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .padding(8.dp),
                                    color = Color.White,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            },
            onDismissRequest = {
                showClubDetailsView = false
            },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = {
                        showClubDetailsView = false
                    }
                ) {
                    Text("Dismiss")
                }
            }
        )
    }
}
