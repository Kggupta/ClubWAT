package com.example.clubwat.views
import HomeViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.clubwat.R
import com.example.clubwat.ui.theme.LightYellow

@Composable
fun HomeView(
    viewModel: HomeViewModel,
    navController: NavController
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Home",
                        modifier = Modifier.fillMaxWidth(),
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = {/* Do Something*/ }) {
                        Icon(Icons.Filled.Inbox, null)
                    }
                },
                backgroundColor = LightYellow,
                contentColor = Color.Black
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(16.dp))
                Image(
                    painter = painterResource(id = R.drawable.waterloocirclelogo),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(100.dp)
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "ClubWAT",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                CustomTabRow(selectedTabIndex = selectedTabIndex) { index ->
                    selectedTabIndex = index
                }
                Spacer(Modifier.height(16.dp))
                if (selectedTabIndex == 0) {
                    YourClubsContent(viewModel, navController)
                } else {
                    YourEventsContent(viewModel, navController)
                }
            }
        }
    )
}

@Composable
fun CustomTabRow(selectedTabIndex: Int, onTabSelected: (Int) -> Unit) {
    val tabTitles = listOf("Your Clubs", "Your Events")
    TabRow(
        selectedTabIndex = selectedTabIndex,
        backgroundColor = LightYellow,
        contentColor = Color.White,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                color = Color.Black,
            )
        }
    ) {
        tabTitles.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                text = { Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    )
                }
            )
        }
    }
}

@Composable
fun YourClubsContent(viewModel: HomeViewModel, navController: NavController) {
    LaunchedEffect(key1 = true) {
        viewModel.getAllClubs()
    }
    val clubs = viewModel.allClubs.collectAsState().value
    LazyColumn {
        items(clubs) { club ->
            ClubItem(club, navController)
        }
    }
}

@Composable
fun YourEventsContent(viewModel: HomeViewModel, navController: NavController) {
    LaunchedEffect(key1 = true) {
        viewModel.getAllEvents()
    }
    val eventWrappers = viewModel.allEvents.collectAsState().value
    LazyColumn {
        items(eventWrappers) { eventWrapper ->
            EventItem(eventWrapper, navController, true)
        }
    }
}
