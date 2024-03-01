package com.example.clubwat.views

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.clubwat.R
import com.example.clubwat.ui.theme.PurpleGrey80
import com.example.clubwat.viewmodels.ClubDiscussionViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubDiscussionView(
    viewModel: ClubDiscussionViewModel,
    navController: NavController,
    clubId: String?
) {
    val uiState = viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        clubId?.let { clubId ->
            viewModel.fetchClubDetails(clubId)
            viewModel.fetchUpdatedPosts(clubId)
        }
    }

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
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = uiState.value.clubDetails?.title ?: "",
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp
                        )
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            LazyColumn(
                modifier = Modifier
                    .wrapContentHeight()
            ) {
                items(uiState.value.posts) { post ->
                    val isMe = post.user_id == 999
                    Row(
                        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Card(
                            modifier = Modifier
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 48f,
                                        topEnd = 48f,
                                        bottomStart = if (isMe) 48f else 0f,
                                        bottomEnd = if (isMe) 0f else 48f
                                    )
                                )
                                .background(PurpleGrey80)
                                .padding(16.dp)
                        ) {
                            Text(text = "${post.user.first_name}: ${post.message}")
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ClubDiscussionViewPreview() {
    ClubDiscussionView(viewModel = viewModel(), navController = rememberNavController(), clubId = "")
}
