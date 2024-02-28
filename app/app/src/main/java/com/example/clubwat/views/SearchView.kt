package com.example.clubwat.views
import SearchViewModel
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.clubwat.ui.theme.LightYellow

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SearchView(
    viewModel: SearchViewModel,
    navController: NavController
) {
    LaunchedEffect(Unit) {
        viewModel.onSearchQueryChanged("", true)
    }

    val text by viewModel.searchQuery.collectAsState()
    val active by viewModel.isSearching.collectAsState()
    val clubs by viewModel.clubs.collectAsState()
    val events by viewModel.events.collectAsState()
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Scaffold {
        Column(){
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LightYellow,
                    titleContentColor = Color.Black
                ),
                title = {
                    Text(
                        text = "Search",
                        modifier = Modifier.fillMaxWidth(),
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = {/* Do Something*/ }) {
                        Icon(Icons.Filled.Inbox, null)
                    }
                }
            )
            SearchTab(selectedTabIndex = selectedTabIndex) { index ->
<<<<<<< HEAD
                if (selectedTabIndex != index) {
                    selectedTabIndex = index
                    viewModel.onSearchQueryChanged("", index == 0)
                }
=======
                selectedTabIndex = index
>>>>>>> b2e2d3c70beb217e45422813eb8b8c30e6eb6da1
            }
            SearchBar(
                modifier = Modifier.fillMaxWidth().padding(14.dp),
                query = text,
                onQueryChange = { viewModel.onSearchQueryChanged(it, selectedTabIndex == 0) },
                onSearch = {
                    viewModel.onSearchQueryChanged(text, selectedTabIndex == 0)
                    viewModel.onIsSearchingChanged(false)
                },
                active = active,
                onActiveChange = {viewModel.onIsSearchingChanged(it)},
                placeholder = {
                    if (selectedTabIndex == 0) {
                        Text("Search Clubs")
                    } else {
                        Text("Search Events")
                    }
                },
                leadingIcon = {
                    Icon(imageVector= Icons.Default.Search, contentDescription="Search")
                },
                trailingIcon = {
                    Icon(modifier = Modifier.clickable {
                        viewModel.onSearchQueryChanged("", selectedTabIndex == 0)
                        viewModel.onIsSearchingChanged(false)
                    },
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }
            ) {
                LazyColumn(modifier=Modifier.fillMaxWidth()) {
                    if (selectedTabIndex == 0) {
                        items(clubs) {club ->
                            Row(modifier = Modifier.clickable {
                                navController.navigate("club/${club.id}")
                            }){
                                Text(text=club.title, modifier=Modifier.fillMaxWidth().padding(14.dp))
                            }
                        }
                    } else {
                        items(events) {event ->
                            Row(modifier = Modifier.clickable {
                                // change later
                                navController.navigate("club/${event.clubId}")
                            }){
                                Text(text=event.title, modifier=Modifier.fillMaxWidth().padding(14.dp))
                            }
                        }
                    }
                }
            }
            LazyColumn(modifier=Modifier.fillMaxWidth()) {
                if (selectedTabIndex == 0) {
                    items(clubs) {club ->
                        Row(modifier = Modifier.clickable {
                            navController.navigate("club/${club.id}")
                        }){
                            ClubItem(club = club, navController = navController)
                        }
                    }
                } else {
                    items(events) {event ->
                        Row(modifier = Modifier.clickable {
                            // change later
                            navController.navigate("club/${event.clubId}")
                        }){
                            EventItem(event = event, navController = navController)
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun SearchTab(selectedTabIndex: Int, onTabSelected: (Int) -> Unit) {
    val tabTitles = listOf("Clubs", "Events")
    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = LightYellow,
        contentColor = Color.Black,
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
