package com.example.clubwat.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAddAlt1
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.clubwat.R
import com.example.clubwat.ui.theme.LightYellow
import com.example.clubwat.viewmodels.ProfileViewModel


@Composable
fun ProfileView(
    viewModel: ProfileViewModel,
    navController: NavController
) {
    var showEditInterests by remember { mutableStateOf(false) }
    var showEditProfile by remember { mutableStateOf(false) }
    var showEditFriends by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Logout",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .clickable {
                    viewModel.logout()
                    navController.navigate("login")
                }
                .padding(16.dp),
            fontSize = 18.sp,
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Profile",
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            actions = {
                IconButton(onClick = {
                    viewModel.logout()
                    navController.navigate("login")
                }) {
                    Icon(Icons.AutoMirrored.Filled.Logout, null)
                }
            },
            backgroundColor = LightYellow,
            contentColor = Color.Black
        )


        Spacer(modifier = Modifier.height(16.dp))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(150.dp)
                .padding(16.dp)
        ) {
            // get API to check if user has PP, if not, have an empty image
            Image(
                // change this to profile pic
                painter = painterResource(id = R.drawable.waterloocirclelogo),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(100.dp)
            )
        }

        val firstName = viewModel.firstName?.value ?: ""
        val lastName = viewModel.lastName?.value ?: ""

        if (firstName.isNotEmpty() && lastName.isNotEmpty()) {
            Text(
                text = "$firstName $lastName",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Divider()
        Spacer(modifier = Modifier.height(20.dp))
        TextWithIcon("Edit Interests", Icons.Default.Edit,  onClick = {
            showEditInterests = true

        })
        Spacer(modifier = Modifier.height(20.dp))
        TextWithIcon("Edit Profile", Icons.Default.Person,  onClick = {
            showEditProfile = true
        })
        Spacer(modifier = Modifier.height(20.dp))
        TextWithIcon("Edit Friends", Icons.Default.PersonAddAlt1,  onClick = {
            showEditFriends = true
        })
    }

    if (showEditInterests) {
        AlertDialogExample(
            onDismissRequest = { showEditInterests = false },
            onConfirmation = {
                showEditInterests = false
                viewModel.editInterests()
            },
            dialogTitle = "Edit Interests",
            dialogText = "Here you can edit your interests.",
            icon = Icons.Default.Edit,
            content = {
                // Example of custom UI content
                Text("Hobbies")
                Text("Availability")
            }
        )
    }

    if (showEditProfile) {
        AlertDialogExample(
            onDismissRequest = { showEditProfile = false },
            onConfirmation = {
                showEditProfile = false
                viewModel.editProfile()
            },
            dialogTitle = "Edit Password",
            dialogText = "Here you can edit your profile",
            icon = Icons.Default.Edit,
            content = {
                Text("Edit Password")

            }
        )
    }

    if (showEditFriends) {
        AlertDialogExample(
            onDismissRequest = { showEditFriends = false },
            onConfirmation = {
                showEditFriends = false
                viewModel.editFriends()
            },
            dialogTitle = "Edit Friends",
            dialogText = "Here you can add, delete and view all friends.",
            icon = Icons.Default.Edit,
            content = {
                Text("Edit")
                Text("Delete")
                Text("Add")
            }
        )
    }





}


@Composable
fun TextWithIcon(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable(onClick = onClick) ) {
        Text(text = text)
        Spacer(modifier = Modifier.width(4.dp)) 
        Icon(
            imageVector = icon,
            contentDescription = ""
        )
    }
}


@Composable
fun AlertDialogExample(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)

        },
        text = {
            Column {
                content()
            }
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}
