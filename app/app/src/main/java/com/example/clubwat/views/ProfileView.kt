package com.example.clubwat.views

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAddAlt1
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        androidx.compose.material.TopAppBar(title = {
            Text(
                text = "Profile",
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }, actions = {
            IconButton(onClick = {
                viewModel.logout()
                navController.navigate("login")
            }) {
                Icon(Icons.AutoMirrored.Filled.Logout, null)
            }
        }, backgroundColor = LightYellow, contentColor = Color.Black
        )




        Spacer(modifier = Modifier.height(16.dp))
        Box(
            contentAlignment = Alignment.Center, modifier = Modifier
                .size(150.dp)
                .padding(16.dp)
        ) {
            // get API to check if user has PP, if not, have an empty image
            Image(
                // change this to profile pic
                painter = painterResource(id = R.drawable.waterloocirclelogo),
                contentDescription = "Profile Picture",
                modifier = Modifier.size(100.dp)
            )
        }

        val firstName = viewModel.firstName?.value ?: ""
        val lastName = viewModel.lastName?.value ?: ""

        if (firstName.isNotEmpty() && lastName.isNotEmpty()) {
            Text(
                text = "$firstName $lastName", fontWeight = FontWeight.Bold, fontSize = 24.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Divider()
        Spacer(modifier = Modifier.height(20.dp))
        TextWithIcon("Edit Interests", Icons.Default.Edit, onClick = {
            showEditInterests = true

        })
        Spacer(modifier = Modifier.height(20.dp))
        TextWithIcon("Edit Password", Icons.Default.Person, onClick = {
            showEditProfile = true
        })
        Spacer(modifier = Modifier.height(20.dp))
        TextWithIcon("Edit Friends", Icons.Default.PersonAddAlt1, onClick = {
            showEditFriends = true
        })
    }


    if (showEditInterests) {
        viewModel.getUserInterests()
        var faculty by rememberSaveable { mutableStateOf(viewModel.facultyID) }
        var ethnicity by rememberSaveable { mutableStateOf(viewModel.ethicityID) }
        var religion by rememberSaveable { mutableStateOf(viewModel.religionID) }
        var program by rememberSaveable { mutableStateOf(viewModel.programID) }
        var hobbies by rememberSaveable { mutableStateOf(viewModel.hobbyID) }


        var buttonModifier = Modifier.width(1000.dp)

        AlertDialogExample(onDismissRequest = {
            showEditInterests = false
            viewModel.resetInterests()
        },
            onConfirmation = {
                showEditInterests = false
                viewModel.resetInterests()
            },
            dialogTitle = "Edit Interests",
            dialogText = "Here you can edit your interests.",
            icon = Icons.Default.Edit,
            content = {
                Spacer(modifier = Modifier.height(16.dp))

                Text("Select Faculty", style = MaterialTheme.typography.titleSmall)
                DropdownList(
                    itemList = viewModel.faculties,
                    selectedIndex = faculty,
                    modifier = buttonModifier
                ) {
                    faculty = it
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text("Select Ethnicity", style = MaterialTheme.typography.titleSmall)
                DropdownList(
                    itemList = viewModel.ethnicities,
                    selectedIndex = ethnicity,
                    modifier = buttonModifier
                ) {
                    ethnicity = it
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text("Select Religion", style = MaterialTheme.typography.titleSmall)
                DropdownList(
                    itemList = viewModel.religions,
                    selectedIndex = religion,
                    modifier = buttonModifier
                ) {
                    religion = it
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text("Select Program", style = MaterialTheme.typography.titleSmall)
                DropdownList(
                    itemList = viewModel.programs,
                    selectedIndex = program,
                    modifier = buttonModifier
                ) {
                    program = it
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Select Hobbies", style = MaterialTheme.typography.titleSmall)
                MultiSelectDropdownList(
                    itemList = viewModel.hobbies, selectedIds = hobbies, modifier = buttonModifier
                ) {
                    hobbies = it
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    viewModel.editInterests(faculty, ethnicity, religion, program, hobbies)
                }) {
                    Text("Save interests")
                }

                if (viewModel.fillAllfields.value != null) {
                    Text(
                        text = viewModel.fillAllfields.value!!,
                        color = Color.Black,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

            })
    }

    if (showEditProfile) {
        AlertDialogExample(onDismissRequest = {
            showEditProfile = false
            viewModel.profileReset()

        },
            onConfirmation = {
                showEditProfile = false
                viewModel.profileReset()
            },
            dialogTitle = "Edit Password",
            dialogText = "",
            icon = Icons.Default.Edit,
            content = {
                Text("Edit Password")
                var viewPassword by remember { mutableStateOf(false) }
                var viewPassword2 by remember { mutableStateOf(false) }

                OutlinedTextField(
                    value = viewModel.oldPassword.value,
                    onValueChange = { viewModel.oldPassword.value = it },
                    label = { Text("Old Password") },
                    visualTransformation = if (viewPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image =
                            if (viewPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        val description = if (viewPassword) "Hide password" else "Show password"
                        IconButton(onClick = { viewPassword = !viewPassword }) {
                            Icon(imageVector = image, contentDescription = description)
                        }
                    },
                )

                OutlinedTextField(
                    value = viewModel.newPassword.value,
                    onValueChange = { viewModel.newPassword.value = it },
                    label = { Text("New Password") },
                    visualTransformation = if (viewPassword2) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image =
                            if (viewPassword2) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        val description = if (viewPassword2) "Hide password" else "Show password"
                        IconButton(onClick = { viewPassword2 = !viewPassword2 }) {
                            Icon(imageVector = image, contentDescription = description)
                        }
                    },
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    viewModel.validatePasswordAndSignUp(viewModel.newPassword.value)
                    viewModel.editPassword(viewModel.oldPassword.value, viewModel.newPassword.value)
                }) {
                    Text("Update")
                }

                if (viewModel.passwordError.value != null) {
                    Text(
                        text = viewModel.passwordError.value!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                if (viewModel.passwordSuccess.value != null) {
                    Text(
                        text = viewModel.passwordSuccess.value!!,
                        color = successGreen,
                        style = MaterialTheme.typography.bodySmall
                    )

                }

            })
    }

    if (showEditFriends) {
        viewModel.getFriends()
        viewModel.getFriendsReq()
        val friends by viewModel.friends.collectAsState()
        val reqFriends by viewModel.req_friends.collectAsState()

        AlertDialogExample(onDismissRequest = {
            showEditFriends = false
            viewModel.resetFriends()
        }, onConfirmation = {
            showEditFriends = false
            viewModel.resetFriends()
        }, dialogTitle = "Friends", dialogText = "", icon = Icons.Default.Edit, content = {
            Text(
                "Add a friend", style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = viewModel.addFriend.value,
                onValueChange = { viewModel.addFriend.value = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { viewModel.addFriend(viewModel.addFriend.value) },

                ) {
                Text("Request")
            }

            if (viewModel.addFriendMessage.value != null) {
                Text(
                    text = viewModel.addFriendMessage.value!!,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Divider()
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "View all friend requests", style = MaterialTheme.typography.headlineSmall
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Name", style = MaterialTheme.typography.bodySmall)
                Text("Email", style = MaterialTheme.typography.bodySmall)
                Text("Accept Friend", style = MaterialTheme.typography.bodySmall)
            }
            reqFriends.forEach { user ->
                UserComponent(
                    user = user,
                    onEditClicked = { user.id.let { viewModel.acceptFriend(it) } },
                    icon = Icons.Default.Check
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Divider()
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "View all your friends", style = MaterialTheme.typography.headlineSmall
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Name", style = MaterialTheme.typography.bodySmall)
                Text("Email", style = MaterialTheme.typography.bodySmall)
                Text("Remove Friend", style = MaterialTheme.typography.bodySmall)
            }

            friends.forEach { user ->
                UserComponent(
                    user = user,
                    onEditClicked = { user.id.let { viewModel.deleteFriend(it) } },
                    icon = Icons.Default.Delete
                )
            }

        })
    }

}


