package com.example.clubwat.views

import DetailItem
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DataArray
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Interests
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAddAlt1
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavController
import com.example.clubwat.BuildConfig
import com.example.clubwat.R
import com.example.clubwat.model.Interest
import com.example.clubwat.model.UserProfile
import com.example.clubwat.ui.theme.LightYellow
import com.example.clubwat.ui.theme.successGreen
import com.example.clubwat.viewmodels.ProfileViewModel


@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProfileView(
    viewModel: ProfileViewModel, navController: NavController
) {
    LaunchedEffect(Unit) {
        viewModel.getUserProfile()
    }

    var showDownloadData by remember { mutableStateOf(false) }
    var showDeleteAccount by remember { mutableStateOf(false) }
    val profile = viewModel.profile.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                backgroundColor = LightYellow,
                contentColor = Color.Black,
                actions = {
                    IconButton(onClick = {
                        viewModel.logout()
                        navController.navigate("login")
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, null)
                    }
                }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DetailItem(text = "Edit Profile", icon = Icons.Filled.VerifiedUser,
                    onClick = {
                        navController.navigate("editprofile")
                    })
                DetailItem(text = "Edit Interests", icon = Icons.Filled.Interests,
                    onClick = {
                        navController.navigate("interests")
                    })
                DetailItem(text = "Change Password", icon = Icons.Filled.Password,
                    onClick = {
                        navController.navigate("password")
                    })
                DetailItem(text = "Manage Friends", icon = Icons.Filled.Group,
                    onClick = {
                        navController.navigate("friends")
                    })
                DetailItem(text = "Download Data", icon = Icons.Filled.DataArray,
                    onClick = {
                        showDownloadData = true
                    })
                Spacer(modifier = Modifier.height(20.dp))
                Text(text = "Danger Zone", fontWeight = FontWeight.Bold)
                DetailItem(text = "Delete Account", icon = Icons.Filled.DeleteForever, onClick = {
                    showDeleteAccount = true
                })
                if (profile.value != null && profile.value!!.adminFlag) {
                    DetailItem(text = "Create Club Fair", icon = Icons.Filled.Create, onClick = {
                        navController.navigate("club/${BuildConfig.WUSA_CLUB_ID}/event/new/spotlight")
                    })
                }
            }
        }
    )
    
    if (showDownloadData) {
        AlertDialog(title = {
            Text(text = "Download Your Data?")

        }, onDismissRequest = {
            showDownloadData = false
        }, confirmButton = {
            TextButton(onClick = {
                viewModel.downloadData()
                showDownloadData = false
            }) {
                Text("Confirm")
            }
        }, dismissButton = {
            TextButton(onClick = {
                showDownloadData = false
            }) {
                Text("Close")
            }
        })
    }
    
    if (showDeleteAccount) {
        AlertDialog(title = {
            Text(text = "Delete Your Account?")
        }, onDismissRequest = {
            showDeleteAccount = false
        }, confirmButton = {
            TextButton(onClick = {
                viewModel.deleteAccount()
                navController.navigate("login")
                showDeleteAccount = false
            }) {
                Text("Confirm")
            }
        }, dismissButton = {
            TextButton(onClick = {
                showDeleteAccount = false
            }) {
                Text("Close")
            }
        })
    }
}
