package com.example.clubwat.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAddAlt1
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavController
import com.example.clubwat.R
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
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Profile",
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp
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
        val facultyList = listOf<String>("Arts", "Engineering", "Environment", "Health", "Mathematics", "Science")
        val ethnicityList = listOf<String>("African",
            "African American",
            "Asian",
            "Asian American",
            "Caucasian",
            "European",
            "Hispanic",
            "Latino",
            "Middle Eastern",
            "Native American",
            "Pacific Islander",
            "South Asian",
            "Caribbean",
            "Mixed Ethnicity",
            "Other")
        val religionList = listOf<String>("Buddhism",
            "Christianity",
            "Hinduism",
            "Islam",
            "Judaism",
            "Sikhism",
            "Baha'i",
            "Confucianism",
            "Jainism",
            "Shinto",
            "Taoism",
            "Zoroastrianism",
            "Paganism",
            "Atheism",
            "Agnosticism",
            "Other")
        var faculty by rememberSaveable { mutableStateOf(0) }
        var ethnicity by rememberSaveable { mutableStateOf(0) }
        var religion by rememberSaveable { mutableStateOf(0) }
        var buttonModifier = Modifier.width(100.dp)

        AlertDialogExample(
            onDismissRequest = { showEditInterests = false },
            onConfirmation = {
                showEditInterests = false
                viewModel.editInterests(facultyList[faculty], ethnicityList[ethnicity], religionList[religion])
            },
            dialogTitle = "Edit Interests",
            dialogText = "Here you can edit your interests.",
            icon = Icons.Default.Edit,
            content = {
                Text("Select Faculty")
                DropdownList(itemList = facultyList, selectedIndex = faculty, modifier = buttonModifier, onItemClick = {faculty = it})
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = viewModel.program.value,
                    onValueChange = { viewModel.program.value = it },
                    label = { Text("Program") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = viewModel.currentInput.value,
                    onValueChange = { viewModel.currentInput.value = it },
                    label = { Text("Hobbies") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { viewModel.addHobbies() })
                )
                Row(
                    modifier = Modifier.padding(8.dp),
                ) {
                    viewModel.hobbies.value.forEach { hobby ->
                        Chips(
                            text = hobby,
                            onDismiss = { viewModel.removeHobby(hobby) },
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Select Ethnicity")
                DropdownList(itemList = ethnicityList, selectedIndex = ethnicity, modifier = buttonModifier, onItemClick = {ethnicity = it})
                Spacer(modifier = Modifier.height(16.dp))
                Text("Select Religion")
                DropdownList(itemList = religionList, selectedIndex = religion, modifier = buttonModifier, onItemClick = {religion = it})
                Spacer(modifier = Modifier.height(16.dp))
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

// taken from https://medium.com/@2018.itsuki/android-kotlin-jetpack-compose-dropdown-selectable-list-menu-b7ad86ba6a5a
@Composable
fun DropdownList(itemList: List<String>, selectedIndex: Int, modifier: Modifier, onItemClick: (Int) -> Unit) {

    var showDropdown by rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {

        // button
        Box(
            modifier = modifier
                .background(Color.LightGray)
                .clickable { showDropdown = true },
//            .clickable { showDropdown = !showDropdown },
            contentAlignment = Alignment.Center
        ) {
            Text(text = itemList[selectedIndex], modifier = Modifier.padding(3.dp))
        }

        // dropdown list
        Box() {
            if (showDropdown) {
                Popup(
                    alignment = Alignment.TopCenter,
                    properties = PopupProperties(
                        excludeFromSystemGesture = true,
                    ),
                    // to dismiss on click outside
                    onDismissRequest = { showDropdown = false }
                ) {

                    Column(
                        modifier = modifier
                            .heightIn(max = 90.dp)
                            .verticalScroll(state = scrollState)
                            .border(width = 1.dp, color = Color.Gray),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {

                        itemList.onEachIndexed { index, item ->
                            if (index != 0) {
                                Divider(thickness = 1.dp, color = Color.LightGray)
                            }
                            Box(
                                modifier = Modifier
                                    .background(Color.White)
                                    .fillMaxWidth()
                                    .clickable {
                                        onItemClick(index)
                                        showDropdown = !showDropdown
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = item,)
                            }
                        }

                    }
                }
            }
        }
    }

}




@Composable
fun Chips(
    text: String,
    onDismiss: () -> Unit,
    modifier: Modifier
) {
    var enabled by remember { mutableStateOf(true) }
    if (!enabled) return

    InputChip(
        modifier = modifier,
        onClick = {
            onDismiss()
            enabled = !enabled
        },
        label = { Text(text) },
        selected = enabled,
        trailingIcon = {
            Icon(
                Icons.Default.Close,
                contentDescription = "Localized description",
                Modifier.size(InputChipDefaults.AvatarSize)
            )
        },
    )
}

