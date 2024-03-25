package com.example.clubwat.views

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.clubwat.viewmodels.ApproveClubViewModel
import androidx.compose.material3.Card
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.clubwat.R
import com.example.clubwat.ui.theme.LightYellow


@Composable
fun ApproveClubView(
    viewModel: ApproveClubViewModel = hiltViewModel(),
    navController: NavController,
) {
    val uiState = viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchClubs()
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
                        text = "Unapproved Clubs",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                backgroundColor = LightYellow,
                contentColor = Color.Black
            )
        },
        content = {
            LoadingScreen(uiState.value.isLoading) {
                Column(
                    modifier = Modifier
                        .padding(it)
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LazyColumn(Modifier.fillMaxSize()) {
                        itemsIndexed(uiState.value.clubs) { _, club ->
                            Card(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                Column(Modifier.padding(8.dp)) {
                                    Row {
                                        Column {
                                            Text(
                                                if (club.title.length > 15) club.title.take(12) + "..." else club.title,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                if (club.description.length > 30) club.description.take(
                                                    30
                                                ) + "..." else club.description,
                                            )
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            IconButton(onClick = {
                                                viewModel.approveClub(
                                                    club.id.toInt()
                                                )
                                            }) {
                                                Icon(
                                                    imageVector = Icons.Filled.Check,
                                                    contentDescription = "Accept"
                                                )
                                            }
                                            IconButton(onClick = {
                                                viewModel.approveClub(
                                                    club.id.toInt()
                                                )
                                            }) {
                                                Icon(
                                                    imageVector = Icons.Filled.Delete,
                                                    contentDescription = "Delete"
                                                )
                                            }
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