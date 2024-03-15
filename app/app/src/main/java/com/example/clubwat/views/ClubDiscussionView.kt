package com.example.clubwat.views

//noinspection UsingMaterialAndMaterial3Libraries
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.clubwat.R
import com.example.clubwat.ui.theme.LightOrange
import com.example.clubwat.ui.theme.LightYellow
import com.example.clubwat.ui.theme.PurpleGrey80
import com.example.clubwat.viewmodels.ClubDiscussionViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.gestures.detectTapGestures

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.ui.input.pointer.pointerInput
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "SimpleDateFormat",
    "CoroutineCreationDuringComposition"
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubDiscussionView(
    viewModel: ClubDiscussionViewModel,
    navController: NavController,
    clubId: String?
) {
    val uiState = viewModel.uiState.collectAsState()
    val isLoadingClubDetails by viewModel.isLoadingClubDetails.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val state = rememberLazyListState()

    LaunchedEffect(Unit) {
        clubId?.let {
            viewModel.fetchClubDetails(it)
            viewModel.fetchUpdatedPosts(it)
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
                    Text(
                        text = uiState.value.clubDetails?.title ?: "",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                backgroundColor = LightYellow,
                contentColor = Color.Black
            )
        },
        content = {
            if (!isLoadingClubDetails) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .verticalScroll(enabled = false, state = rememberScrollState())
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f),
                        verticalArrangement = Arrangement.Bottom,
                        userScrollEnabled = true,
                        state = state
                    ) {
                        itemsIndexed(uiState.value.posts) { index, post ->
                            val name =
                                if (uiState.value.posts.getOrNull(index - 1)?.messageData?.user?.email == post.messageData.user.email || post.isMe)
                                    null
                                else post.messageData.user.firstName
                            val apiDateTime = LocalDateTime.parse(
                                post.messageData.createDate,
                                DateTimeFormatter.ISO_DATE_TIME
                            )
                            val formatter = DateTimeFormatter.ofPattern("MMM dd, hh:mm")
                            val formattedDateTime = apiDateTime.format(formatter)
                            MessageBubble(
                                isClubAdmin = uiState.value.clubDetails?.isClubAdmin ?: false,
                                isMe = post.isMe,
                                name = if (post.isMe.not()) name + " (${formattedDateTime})" else formattedDateTime,
                                message = post.messageData.message,
                                onDelete = {
                                    if (clubId != null) {
                                        viewModel.deleteMessage(post.messageData.id, uiState.value.clubDetails?.id)
                                    }
                                }
                            )
                            coroutineScope.launch {
                                state.animateScrollToItem(uiState.value.posts.size -1)
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            var text by remember { mutableStateOf("") }
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(5.dp)
                )
                Button(
                    onClick = {
                        if (text.isNotEmpty()) {
                            viewModel.sendMessage(
                                text,
                                uiState.value.clubDetails?.id,
                            )
                        }
                        text = ""
                    },
                    modifier = Modifier.padding(end = 5.dp, top = 5.dp, bottom = 5.dp)
                ) {
                    Text(text = "SEND", color = Color.White)
                }
            }
        }
    )
}

@Composable
fun MessageBubble(isClubAdmin: Boolean?, isMe: Boolean, name: String?, message: String, onDelete: () -> Unit) {

    var showDelDialog by remember { mutableStateOf(false)}
    if (showDelDialog) {
        AlertDialog(
            onDismissRequest = { showDelDialog = false },
            title = { Text("Delete Message") },
            text = { Text("Are you sure you want to delete this message?" ) },
            confirmButton = {
                TextButton(onClick = { 
                    onDelete()
                    showDelDialog = false
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = {  showDelDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    Column(
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        name?.let { name ->
            Text(
                text = name,
                modifier = Modifier.background(
                    color = Color.Transparent
                ),
                fontSize = 12.sp,
            )
        }
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 48f,
                        topEnd = 48f,
                        bottomStart = if (isMe) 48f else 0f,
                        bottomEnd = if (isMe) 0f else 48f
                    )
                )
                .background(if (isMe) LightOrange else PurpleGrey80)
                .pointerInput(Unit) {
                    detectTapGestures (
                        onLongPress = {
                            if (isMe || isClubAdmin == true) {
                                showDelDialog = true
                            }
                        }
                    )
                }
                .padding(16.dp)
        ) {
            Text(
                text = message,
                modifier = Modifier.background(
                    color = Color.Transparent
                ),
                fontSize = 15.sp,
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun ClubDiscussionViewPreview() {
    ClubDiscussionView(
        viewModel = viewModel(),
        navController = rememberNavController(),
        clubId = ""
    )
}
