package com.example.clubwat.views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.clubwat.R
import com.example.clubwat.ui.theme.LightYellow
import com.example.clubwat.ui.theme.Orange
import com.example.clubwat.viewmodels.EditEventDetailsViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditEventDetailsView(
    viewModel: EditEventDetailsViewModel = hiltViewModel(),
    navController: NavController,
    eventId: String?
) {

    var errorMessage by viewModel.errorMessage

    LaunchedEffect(Unit) {
        if (eventId != null) {
            viewModel.getEvent(eventId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
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
                        text = "Edit Event",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                backgroundColor = LightYellow,
                contentColor = Color.Black
            )
        },
        content = { it ->
            Column(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    .padding(14.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Column() {
                    Spacer(modifier = Modifier.height(5.dp))
                    OutlinedTextField(
                        value = viewModel.title.value,
                        label = { Text("Title") },
                        onValueChange = { newValue: String -> viewModel.title.value = newValue },
                        placeholder = { Text(text = viewModel.getEventTitle()) },
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
                    OutlinedTextField(
                        value = viewModel.description.value,
                        label = { Text("Description") },
                        onValueChange = { newValue: String -> viewModel.description.value = newValue },
                        placeholder = { Text(text = viewModel.getEventDescription()) },
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Orange,
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent
                        ),
                    )
                }

                Column() {
                    OutlinedTextField(
                        value = viewModel.location.value,
                        label = { Text("Location") },
                        onValueChange = { newValue: String -> viewModel.location.value = newValue },
                        placeholder = { Text(text = viewModel.getEventLocation()) },
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
                    Text(text = "Start Date", style = TextStyle(fontWeight = FontWeight.Bold))
                    DateTimePickerView(dateTime = viewModel.startDate)
                }

                Column() {
                    Text(text = "End Date", style = TextStyle(fontWeight = FontWeight.Bold))
                    DateTimePickerView(dateTime = viewModel.endDate)
                }

                Button(
                    onClick = {
                        val originalStartDate = viewModel.parseDateString(viewModel.getEventStartDate())
                        val isStartDateUnchanged = originalStartDate?.let {
                            viewModel.areCalendarsEqualIgnoringMilliseconds(viewModel.startDate.value, it)
                        } ?: false

                        val originalEndDate = viewModel.parseDateString(viewModel.getEventEndDate())
                        val isEndDateUnchanged = originalEndDate?.let {
                            viewModel.areCalendarsEqualIgnoringMilliseconds(viewModel.endDate.value, it)
                        } ?: false

                        if (viewModel.title.value.isBlank() &&
                            viewModel.description.value.isBlank() &&
                            viewModel.location.value.isBlank() &&
                            isStartDateUnchanged && isEndDateUnchanged) {
                            errorMessage = "Please fill in all values"
                        } else if (!(viewModel.startDate.value.time.before(viewModel.endDate.value.time))) {
                            errorMessage = "Start date must be earlier than end date!"
                        } else {
                            viewModel.updateEvent { isAdded ->
                                if (isAdded) {
                                    navController.popBackStack()
                                }
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


