package com.example.clubwat.views
import SearchViewModel
import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.TopAppBar
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.clubwat.ui.theme.LightYellow

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SearchView(
    viewModel: SearchViewModel,
    navController: NavController
) {
    LaunchedEffect(Unit) {
        viewModel.onSearchQueryChanged("")
    }

    val text by viewModel.searchQuery.collectAsState()
    val active by viewModel.isSearching.collectAsState()
    val clubs by viewModel.clubs.collectAsState()

    Scaffold {
        Column(){
            TopAppBar(
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
                },
                backgroundColor = LightYellow,
                contentColor = Color.Black
            )
            SearchBar(
                modifier = Modifier.fillMaxWidth().padding(14.dp),
                query = text,
                onQueryChange = viewModel::onSearchQueryChanged,
                onSearch = {
                    viewModel.onSearchQueryChanged(text)
                    viewModel.onIsSearchingChanged(false)
                },
                active = active,
                onActiveChange = {viewModel.onIsSearchingChanged(it)},
                placeholder = {
                    Text("Search Clubs")
                },
                leadingIcon = {
                    Icon(imageVector= Icons.Default.Search, contentDescription="Search")
                },
                trailingIcon = {
                    Icon(modifier = Modifier.clickable {
                        viewModel.onSearchQueryChanged("")
                        viewModel.onIsSearchingChanged(false)
                    },
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }
            ) {
                LazyColumn(modifier=Modifier.fillMaxWidth()) {
                    items(clubs) {club ->
                        Row(modifier = Modifier.clickable {
                            navController.navigate("club/${club.id}")
                        }){
                            Text(text=club.title, modifier=Modifier.fillMaxWidth().padding(14.dp))
                        }
                    }
                }
            }
            LazyColumn(modifier=Modifier.fillMaxWidth()) {
                items(clubs) {club ->
                    Row(modifier = Modifier.clickable {
                        navController.navigate("club/${club.id}")
                    }){
                        Text(text=club.title, modifier=Modifier.fillMaxWidth().padding(14.dp))
                    }
                }
            }
        }

    }
}
