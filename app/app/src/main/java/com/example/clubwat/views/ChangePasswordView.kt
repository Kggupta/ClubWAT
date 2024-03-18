package com.example.clubwat.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.TopAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.clubwat.R
import com.example.clubwat.ui.theme.LightOrange
import com.example.clubwat.ui.theme.LightYellow
import com.example.clubwat.viewmodels.ChangePasswordViewModel

@Composable
fun ChangePasswordView(
    viewModel: ChangePasswordViewModel = hiltViewModel(),
    navController: NavController
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
                    Text(
                        text = "Change Password",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                backgroundColor = LightYellow,
                contentColor = Color.Black
            )
        },
        content = {
            Column (
                Modifier
                    .padding(it)
                    .fillMaxSize()) {
                OutlinedTextField(
                    modifier= Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    value = viewModel.oldPassword.value,
                    onValueChange = {value -> viewModel.oldPassword.value = value },
                    label = { Text("Old Password") },
                    visualTransformation = PasswordVisualTransformation()
                )
                OutlinedTextField(
                    modifier= Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    value = viewModel.newPassword.value,
                    onValueChange = {value -> viewModel.newPassword.value = value },
                    label = { Text("New Password") },
                    visualTransformation = PasswordVisualTransformation()
                )
                OutlinedTextField(
                    modifier= Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    value = viewModel.confirmNewPassword.value,
                    onValueChange = {value -> viewModel.confirmNewPassword.value = value },
                    label = { Text("Confirm New Password") },
                    visualTransformation = PasswordVisualTransformation()
                )
                TextButton(onClick = {
                    viewModel.editPassword(viewModel.oldPassword.value, viewModel.newPassword.value)
                                     },
                    colors = ButtonDefaults.buttonColors(containerColor = LightOrange),
                    modifier = Modifier
                        .width(300.dp)
                        .height(40.dp)
                        .align(Alignment.CenterHorizontally)) {
                    Text("Change Password")
                }
                if (viewModel.passwordSuccess.value != null) {
                    Text(modifier=Modifier.align(Alignment.CenterHorizontally), text = viewModel.passwordSuccess.value!!)
                } else if (viewModel.passwordError.value != null) {
                    Text(modifier=Modifier.align(Alignment.CenterHorizontally), text = viewModel.passwordError.value!!)
                }
            }
        }
    )

}
