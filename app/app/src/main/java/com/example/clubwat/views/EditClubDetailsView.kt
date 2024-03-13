package com.example.clubwat.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.clubwat.R
import com.example.clubwat.ui.theme.LightYellow
import com.example.clubwat.ui.theme.Orange
import com.example.clubwat.viewmodels.EditClubDetailsViewModel

@Composable
fun EditClubDetailsView(
    viewModel: EditClubDetailsViewModel,
    navController: NavController,
    clubId: String?
) {
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
                    androidx.compose.material3.Text(
                        text = "Edit Club Details",
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
                modifier = androidx.compose.ui.Modifier
                    .padding(it)
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = viewModel.title,
                    onValueChange = { newText ->
                        viewModel.title = newText
                    },
                    label = { Text("New Title") }
                )
                Spacer(androidx.compose.ui.Modifier.height(16.dp))
                OutlinedTextField(
                    value = viewModel.description,
                    onValueChange = { newText ->
                        viewModel.description = newText
                    },
                    label = { Text("New Description") }
                )
                Spacer(androidx.compose.ui.Modifier.height(16.dp))
                OutlinedTextField(
                    value = viewModel.membershipFee,
                    onValueChange = { newText ->
                        viewModel.membershipFee = newText
                    },
                    label = { Text("New Membership Fee") }
                )
                Spacer(androidx.compose.ui.Modifier.height(16.dp))
                Button(
                    colors = ButtonDefaults.buttonColors(Orange),
                    onClick = {
                        if (clubId != null) {
                            viewModel.updateClub(clubId)
                        }
                    },
                    modifier = androidx.compose.ui.Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text("Done")
                }
            }
        }
    )
}
