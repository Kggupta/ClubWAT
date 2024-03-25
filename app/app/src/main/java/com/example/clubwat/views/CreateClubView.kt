package com.example.clubwat.views

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.clubwat.R
import com.example.clubwat.ui.theme.LightYellow
import com.example.clubwat.ui.theme.Orange
import com.example.clubwat.viewmodels.CreateClubViewModel


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint(
    "UnusedMaterial3ScaffoldPaddingParameter", "SimpleDateFormat",
    "CoroutineCreationDuringComposition"
)
@Composable
fun CreateClubView(
    viewModel: CreateClubViewModel = hiltViewModel(),
    navController: NavController,
) {
    val uiState = viewModel.uiState.collectAsState()
    val createdClubSuccess = viewModel.clubCreatedSuccess

    LaunchedEffect(Unit) {
        viewModel.fetchCategories()
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
                title = { Text("Create Club", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
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
                        .padding(14.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.value.title,
                        label = { Text("Club Name") },
                        onValueChange = { viewModel.onTitleChange(it) },
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Orange,
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = uiState.value.description,
                        label = { Text("Description") },
                        onValueChange = { viewModel.onDescriptionChange(it) },
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Orange,
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent
                        )
                    )

                    OutlinedTextField(
                        value = uiState.value.membershipFee.toString(),
                        label = { Text("Membership fee") },
                        onValueChange = { newValue ->
                            newValue.toDoubleOrNull()?.let {
                                viewModel.onMembershipFeeChange(it)
                            }
                        },
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Orange,
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent
                        )
                    )

                    LazyColumn(
                        modifier = Modifier
                            .padding(16.dp)
                            .weight(1f)
                    ) {
                        item {
                            Text(
                                text = "Categories",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.1.sp
                                )
                            )
                        }
                        items(uiState.value.categories) { category ->
                            CategoryItem(
                                category = category,
                                isSelected = uiState.value.selectedCategoryIds.contains(category.id),
                                onCategorySelected = { id, isSelected ->
                                    viewModel.handleCategorySelection(id, isSelected)
                                }
                            )
                            HorizontalDivider()
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.createClub()
                        },
                        colors = ButtonDefaults.buttonColors(Orange),
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Create Club",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.1.sp
                            )
                        )
                    }

                    Text(
                        text = viewModel.errorMessage.value,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            color = Color.Red,
                            fontSize = 16.sp,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                }
            }
        }
    )

    if (createdClubSuccess.value) {
        navController.popBackStack()
        createdClubSuccess.value = false
    }
}
