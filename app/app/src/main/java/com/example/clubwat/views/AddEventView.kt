package com.example.clubwat.views

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.clubwat.R
import com.example.clubwat.ui.theme.LightYellow
import com.example.clubwat.ui.theme.Orange
import com.example.clubwat.viewmodels.AddEventViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddEventView(viewModel: AddEventViewModel, navController: NavController, clubId: String?) {
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
                        text = "Add Event",
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
                    .padding(14.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                if (clubId == null) return@Scaffold
                Column() {
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = "Title", style = TextStyle(fontWeight = FontWeight.Bold))
                    OutlinedTextField(
                        value = viewModel.title.value,
                        onValueChange = { newValue: String -> viewModel.title.value = newValue },
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
                }

                Column() {
                    Text(text = "Description", style = TextStyle(fontWeight = FontWeight.Bold))
                    OutlinedTextField(
                        value = viewModel.description.value,
                        onValueChange = { newValue: String -> viewModel.description.value = newValue },
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Orange,
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent
                        )
                    )
                }

                Column() {
                    Text(text = "Start Date", style = TextStyle(fontWeight = FontWeight.Bold))
                    DateTimePickerView(dateTime = viewModel.startDateTime)
                }

                Column() {
                    Text(text = "End Date", style = TextStyle(fontWeight = FontWeight.Bold))
                    DateTimePickerView(dateTime = viewModel.endDateTime)
                }

                Column() {
                    Text(text = "Location", style = TextStyle(fontWeight = FontWeight.Bold))
                    OutlinedTextField(
                        value = viewModel.location.value,
                        onValueChange = { newValue: String -> viewModel.location.value = newValue },
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
                }

                Column() {
                    Text(text = "Privacy", style = TextStyle(fontWeight = FontWeight.Bold))
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        RadioButton(
                            selected = !viewModel.isPrivate.value,
                            colors = RadioButtonDefaults.colors(selectedColor = Orange),
                            onClick = {
                                viewModel.isPrivate.value = false
                            }
                        )
                        Text(
                            text = "Public",
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.padding(20.dp))
                        RadioButton(
                            selected = viewModel.isPrivate.value,
                            colors = RadioButtonDefaults.colors(selectedColor = Orange),
                            onClick = {
                                viewModel.isPrivate.value = true
                            }
                        )
                        Text(
                            text = "Private",
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }

                Button(
                    onClick = {
                        viewModel.addEvent(clubId) { isAdded ->
                            if (isAdded) {
                                navController.navigate("club/${clubId}")
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(Orange),
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Submit",
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
    )
}