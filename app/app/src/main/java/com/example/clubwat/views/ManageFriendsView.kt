package com.example.clubwat.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.clubwat.R
import com.example.clubwat.ui.theme.LightYellow
import com.example.clubwat.viewmodels.ManageFriendsViewModel


@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ManageFriendsView(
    viewModel: ManageFriendsViewModel  = hiltViewModel(), navController: NavController
) {
    LaunchedEffect(Unit) {
        viewModel.getFriends()
        viewModel.getFriendsReq()
    }

    val friends by viewModel.friends.collectAsState()
    val reqFriends by viewModel.req_friends.collectAsState()

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
                        text = "Manage Friends",
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
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row {
                    OutlinedTextField(
                        value = viewModel.friendEmail.value,
                        label = { Text("Friend Email") },
                        onValueChange = {
                            value: String -> viewModel.friendEmail.value = value
                        })
                    TextButton(modifier= Modifier
                        .fillMaxWidth()
                        .padding(2.dp)
                        .align(Alignment.CenterVertically),
                        onClick = { viewModel.sendFriendRequest(viewModel.friendEmail.value)}) {
                        Text("Send Request")
                    }
                }
                if (viewModel.addFriendMessage.value.isNotEmpty()) {
                    Text(text = viewModel.addFriendMessage.value)
                }
                Column (Modifier.fillMaxSize()) {
                    reqFriends.forEach{friend ->
                        Card (
                            Modifier
                                .fillMaxWidth()
                                .padding(8.dp)){
                            Column (Modifier.padding(8.dp)) {
                                Row {
                                    Column {
                                        val name = "${friend.firstName} ${friend.lastName.first()}"
                                        Text(if (name.length > 15) name.take(12) + "..." else "$name." , fontWeight = FontWeight.SemiBold)
                                        Text(friend.email.substringBefore("@"))
                                    }

                                    Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End){
                                        IconButton(onClick = {
                                            viewModel.acceptFriend(
                                                friend.id
                                            )
                                        }) {
                                            Icon(imageVector = Icons.Filled.Check, contentDescription = "Accept")
                                        }
                                        IconButton(onClick = {
                                            viewModel.deleteFriend(
                                                friend.id
                                            )
                                        }) {
                                            Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete")
                                        }
                                    }
                                }
                            }
                        }
                    }
                    friends.forEach{friend ->
                        Card (
                            Modifier
                                .fillMaxWidth()
                                .padding(8.dp)){
                            Column (Modifier.padding(8.dp)) {
                                Row {
                                    Column {
                                        val name = "${friend.firstName} ${friend.lastName.first()}"
                                        Text(if (name.length > 15) name.take(12) + "..." else "$name." , fontWeight = FontWeight.SemiBold)
                                        Text(friend.email.substringBefore("@"))
                                    }

                                    Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End){
                                        IconButton(onClick = {
                                            viewModel.deleteFriend(
                                                friend.id
                                            )
                                        }) {
                                            Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete")
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
