package com.example.clubwat.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.clubwat.R
import com.example.clubwat.model.Interest
import com.example.clubwat.ui.theme.LightYellow
import com.example.clubwat.viewmodels.UserInterestsViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun UserInterestsView(
    viewModel: UserInterestsViewModel = hiltViewModel(),
    navController: NavController
) {
    var expanded by remember { mutableStateOf(false) }
    var ethnicityExpanded by remember {
        mutableStateOf(false)
    }
    var religionExpanded by remember {
        mutableStateOf(false)
    }
    var programExpanded by remember {
        mutableStateOf(false)
    }
    var showDialog by remember {
        mutableStateOf(false)
    }

    var fillAllFields by viewModel.fillAllfields
    val religions by viewModel.religions.collectAsState()
    val userReligion by viewModel.userReligion.collectAsState()

    val programs by viewModel.programs.collectAsState()
    val userProgram by viewModel.userProgram.collectAsState()

    val faculties by viewModel.faculties.collectAsState()
    val userFaculty by viewModel.userFaculty.collectAsState()

    val ethnicities by viewModel.ethnicities.collectAsState()
    val userEthnicity by viewModel.userEthnicity.collectAsState()

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
                        text = "Edit Interests",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.saveInterests()
                    }) {
                        Icon(imageVector = Icons.Filled.Save, contentDescription = "Save")
                    }
                },
                backgroundColor = LightYellow,
                contentColor = Color.Black
            )
        },
        content = {
            Column(
                Modifier
                    .padding(it)
                    .padding(16.dp)
                    .fillMaxSize()
                    .fillMaxWidth()
            ) {
                fillAllFields?.let { showDialog = true }
                Text(
                    text = "Faculty",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )
                ExposedDropdownMenuBox(
                    modifier = Modifier.fillMaxWidth(),
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = !expanded
                    }
                ) {
                    TextField(
                        value = if (userFaculty != null) userFaculty!!.name else "",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        modifier = Modifier.fillMaxWidth(),
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        faculties.forEach { item ->
                            DropdownMenuItem(
                                modifier = Modifier.fillMaxWidth(),
                                text = { Text(text = item.name) },
                                onClick = {
                                    viewModel.updateUserFaculty(item)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Text(
                    text = "Ethnicity",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )
                ExposedDropdownMenuBox(
                    modifier = Modifier.fillMaxWidth(),
                    expanded = ethnicityExpanded,
                    onExpandedChange = {
                        ethnicityExpanded = !ethnicityExpanded
                    }
                ) {
                    TextField(
                        value = if (userEthnicity != null) userEthnicity!!.name else "",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = ethnicityExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        modifier = Modifier.fillMaxWidth(),
                        expanded = ethnicityExpanded,
                        onDismissRequest = { ethnicityExpanded = false }
                    ) {
                        ethnicities.forEach { item ->
                            DropdownMenuItem(
                                modifier = Modifier.fillMaxWidth(),
                                text = { Text(text = item.name) },
                                onClick = {
                                    viewModel.updateUserEthnicity(item)
                                    ethnicityExpanded = false
                                }
                            )
                        }
                    }
                }


                Text(
                    text = "Religion",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )
                ExposedDropdownMenuBox(
                    modifier = Modifier.fillMaxWidth(),
                    expanded = religionExpanded,
                    onExpandedChange = {
                        religionExpanded = !religionExpanded
                    }
                ) {
                    TextField(
                        value = if (userReligion != null) userReligion!!.name else "",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = religionExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        modifier = Modifier.fillMaxWidth(),
                        expanded = religionExpanded,
                        onDismissRequest = { religionExpanded = false }
                    ) {
                        religions.forEach { item ->
                            DropdownMenuItem(
                                modifier = Modifier.fillMaxWidth(),
                                text = { Text(text = item.name) },
                                onClick = {
                                    viewModel.updateUserReligion(item)
                                    religionExpanded = false
                                }
                            )
                        }
                    }
                }

                Text(
                    text = "Program",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )
                ExposedDropdownMenuBox(
                    modifier = Modifier.fillMaxWidth(),
                    expanded = programExpanded,
                    onExpandedChange = {
                        programExpanded = !programExpanded
                    }
                ) {
                    TextField(
                        value = if (userProgram != null) userProgram!!.name else "",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = programExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        modifier = Modifier.fillMaxWidth(),
                        expanded = programExpanded,
                        onDismissRequest = { programExpanded = false }
                    ) {
                        programs.forEach { item ->
                            DropdownMenuItem(
                                modifier = Modifier.fillMaxWidth(),
                                text = { Text(text = item.name) },
                                onClick = {
                                    viewModel.updateUserProgram(item)
                                    programExpanded = false
                                }
                            )
                        }
                    }
                }

                Text(
                    text = "Hobbies",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )

                MultiSelectCategoryList(viewModel = viewModel)
            }
        }
    )

    if (showDialog) {
        AlertDialog(title = { fillAllFields?.let { Text(text = it) } },
            onDismissRequest = {
                showDialog = false
                fillAllFields = null
            }, buttons = {
                TextButton(
                    onClick = {
                        showDialog = false
                        fillAllFields = null
                    }) {
                    Text(text = "Dismiss")
                }
            })
    }
}

@Composable
fun MultiSelectCategoryList(viewModel: UserInterestsViewModel) {
    fun onCategorySelected(categoryId: Int, isSelected: Boolean) {
        viewModel.selectedUserHobbies.value = if (isSelected) {
            if (viewModel.selectedUserHobbies.value.size < 5) viewModel.selectedUserHobbies.value + categoryId else viewModel.selectedUserHobbies.value
        } else {
            viewModel.selectedUserHobbies.value - categoryId
        }
    }

    LazyColumn(
        modifier = Modifier
            .padding(16.dp)
            .height(200.dp),
    ) {
        items(viewModel.hobbies.value) { category ->
            CategoryItem(
                category = category,
                isSelected = viewModel.selectedUserHobbies.value.contains(category.id),
                onCategorySelected = ::onCategorySelected
            )
            HorizontalDivider()
        }
    }
}

@Composable
fun CategoryItem(
    category: Interest,
    isSelected: Boolean,
    onCategorySelected: (Int, Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = category.name)
        Checkbox(
            checked = isSelected,
            onCheckedChange = { isSelected ->
                onCategorySelected(category.id, isSelected)
            }
        )
    }
}
