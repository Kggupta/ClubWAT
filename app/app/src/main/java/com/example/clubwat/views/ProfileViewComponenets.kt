package com.example.clubwat.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.clubwat.model.Interest
import com.example.clubwat.model.UserProfile


@Composable
fun TextWithIcon(
    text: String, icon: ImageVector, onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(text = text)
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            imageVector = icon, contentDescription = ""
        )
    }
}

@Composable
fun UserComponent(user: UserProfile, onEditClicked: () -> Unit, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = user.firstName)
        Text(text = user.email)

        IconButton(onClick = { onEditClicked() }) {
            Icon(icon, contentDescription = "icon")
        }
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
    val scrollState = rememberScrollState()

    AlertDialog(icon = {
        Icon(icon, contentDescription = "Example Icon")
    }, title = {
        Text(text = dialogTitle)

    }, text = {
        Column(
            modifier = Modifier.verticalScroll(state = scrollState)
        ) {
            Text(text = dialogText, style = MaterialTheme.typography.headlineSmall)
            content()
        }
    }, onDismissRequest = {
        onDismissRequest()
    }, confirmButton = {}, dismissButton = {
        TextButton(onClick = {
            onDismissRequest()
        }) {
            Text("Close")
        }
    })
}

// taken from https://medium.com/@2018.itsuki/android-kotlin-jetpack-compose-dropdown-selectable-list-menu-b7ad86ba6a5a
@Composable
fun DropdownList(
    itemList: List<Interest>?, selectedIndex: Int, modifier: Modifier, onItemClick: (Int) -> Unit
) {

    var showDropdown by rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val selectedIndex = if (selectedIndex != 0) {
        itemList?.indexOfFirst { it.id == selectedIndex } ?: 0
    } else {
        0
    }


    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // button
        Box(
            modifier = modifier
                .background(Color.White)
                .fillMaxWidth()
                .clickable { showDropdown = true },
//            .clickable { showDropdown = !showDropdown },
            contentAlignment = Alignment.Center
        ) {
            itemList?.get(selectedIndex)?.let {
                val displayName = if (selectedIndex == 0) "" else it.name
                Text(text = displayName, modifier = Modifier.padding(3.dp))
            }
        }


        // dropdown list
        Box() {
            if (showDropdown) {
                Popup(alignment = Alignment.TopCenter, properties = PopupProperties(
                    excludeFromSystemGesture = true,
                ),
                    // to dismiss on click outside
                    onDismissRequest = { showDropdown = false }) {

                    Column(
                        modifier = modifier
                            .heightIn(max = 90.dp)
                            .verticalScroll(state = scrollState)
                            .border(width = 1.dp, color = Color.Gray),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {

                        itemList?.onEachIndexed { index, item ->
                            if (index != 0) {
                                Divider(thickness = 1.dp, color = Color.LightGray)
                            }
                            Box(
                                modifier = Modifier
                                    .background(Color.White)
                                    .fillMaxWidth()
                                    .clickable {
                                        onItemClick(item.id)
                                        showDropdown = !showDropdown
                                    }, contentAlignment = Alignment.Center
                            ) {
                                Text(text = item.name)
                            }
                        }

                    }
                }
            }
        }
    }

}

@Composable
fun MultiSelectDropdownList(
    itemList: List<Interest>?,
    selectedIds: List<Int>, // Use List<Int> to track multiple selections by ID
    modifier: Modifier = Modifier,
    onSelectionChange: (List<Int>) -> Unit // Callback with the updated list of selected IDs
) {
    var showDropdown by rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Button to show dropdown
        Box(
            modifier = modifier
                .background(Color.White)
                .fillMaxWidth()
                .clickable { showDropdown = true }, contentAlignment = Alignment.Center
        ) {
            // Displaying selected item names based on IDs
            Text(text = itemList?.filter { it.id in selectedIds }?.joinToString(", ") { it.name }
                ?: "", modifier = Modifier.padding(3.dp))
        }

        // Dropdown list
        if (showDropdown) {
            Popup(alignment = Alignment.TopCenter, properties = PopupProperties(
                focusable = true, dismissOnBackPress = true, dismissOnClickOutside = true
            ), onDismissRequest = { showDropdown = false }) {
                Column(
                    modifier = modifier
                        .background(Color.White)
                        .border(width = 1.dp, color = Color.Gray)
                        .heightIn(max = 200.dp)
                        .verticalScroll(state = scrollState)
                ) {
                    itemList?.forEach { item ->
                        val isSelected = item.id in selectedIds
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 16.dp)
                                .clickable {
                                    // Updating selected IDs based on current selection
                                    val newSelectedIds = selectedIds
                                        .toMutableList()
                                        .apply {
                                            if (isSelected) remove(item.id) else add(item.id)
                                        }
                                    onSelectionChange(newSelectedIds)
                                }, verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = null // We handle the state change with the Row's clickable
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(text = item.name)
                        }
                        // This divider logic can remain the same or be adapted if needed
                        if (item != itemList.last()) {
                            Divider()
                        }
                    }
                }
            }
        }
    }
}


