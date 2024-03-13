package com.example.clubwat.views
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Card
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.clubwat.R
import com.example.clubwat.ui.theme.LightOrange
import com.example.clubwat.ui.theme.LightYellow
import com.example.clubwat.viewmodels.ClubUserManagementViewModel

@Composable
fun ClubUserManagementView(
    viewModel: ClubUserManagementViewModel,
    navController: NavController,
    clubId: String?
) {
    LaunchedEffect(Unit) {
        if (clubId != null) {
            viewModel.getMembers(clubId)
        }
    }

    val memberList by viewModel.members.collectAsState()
    val isClientClubAdmin by viewModel.isClientClubAdmin.collectAsState()
    val isClientClubCreator by viewModel.isClientClubCreator.collectAsState()

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
                        text = "User Management",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                backgroundColor = LightYellow,
                contentColor = Color.Black
            )
        },
        content = {
            LazyColumn(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (clubId == null) return@LazyColumn
                items(memberList) {member ->
                    Card (
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp)){
                        Column (Modifier.padding(8.dp)) {
                            Row {
                                Column {
                                    val name = "${member.firstName} ${member.lastName.first()}"
                                    Text(if (name.length > 15) name.take(12) + "..." else "$name." , fontWeight = FontWeight.SemiBold)
                                    Text(member.email.substringBefore("@"))
                                }
                                if (!isClientClubAdmin) return@Row
                                if (member.isClubCreator) return@Row
                                if (member.isClubAdmin && !isClientClubCreator) return@Row

                                Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End){
                                    if (!member.isApproved) {
                                        TextButton(onClick = {
                                            viewModel.approveUser(
                                                member.userId,
                                                clubId
                                            )
                                        }) {
                                            Text(color = LightOrange, text = "Paid")
                                        }
                                    } else {
                                        TextButton(onClick = {
                                            viewModel.removeUser(
                                                member.userId,
                                                clubId
                                            )
                                        }) {
                                            Text(color = LightOrange, text = "Remove")
                                        }
                                    }

                                    if (!member.isApproved) return@Column
                                    if (!isClientClubCreator) return@Column

                                    if (!member.isClubAdmin) {
                                        TextButton(onClick = {
                                            viewModel.promoteUser(
                                                member.userId,
                                                clubId
                                            )
                                        }) {
                                            Text(color = LightOrange, text = "Promote")
                                        }
                                    } else {
                                        TextButton(onClick = {
                                            viewModel.demoteUser(
                                                member.userId,
                                                clubId
                                            )
                                        }) {
                                            Text(color = LightOrange, text = "Demote")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}
