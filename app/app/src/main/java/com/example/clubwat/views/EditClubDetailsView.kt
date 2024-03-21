package com.example.clubwat.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.clubwat.R
import com.example.clubwat.model.Category
import com.example.clubwat.ui.theme.LightYellow
import com.example.clubwat.ui.theme.Orange
import com.example.clubwat.viewmodels.EditClubDetailsViewModel

@Composable
fun EditClubDetailsView(
    viewModel: EditClubDetailsViewModel = hiltViewModel(),
    navController: NavController,
    clubId: String?
) {
    var allValuesError by viewModel.allValuesError

    LaunchedEffect(Unit) {
        if (clubId != null) {
            viewModel.getClub(clubId)
            viewModel.getCategories()
        }
    }

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
                modifier = Modifier
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
                    label = { Text("New Title") },
                    placeholder = { Text(viewModel.getClubTitle()) }
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = viewModel.description,
                    onValueChange = { newText ->
                        viewModel.description = newText
                    },
                    label = { Text("New Description") },
                    placeholder = { Text(viewModel.getClubDescription()) }
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = viewModel.membershipFee,
                    onValueChange = { newText ->
                        viewModel.membershipFee = newText
                    },
                    label = { Text("New Membership Fee") },
                    placeholder = { Text(viewModel.getClubMembershipFee())}
                )
                Spacer(Modifier.height(32.dp))
                Text(
                    text="New Categories",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                MultiSelectCategoryList(viewModel)
                Spacer(Modifier.height(16.dp))
                Button(
                    colors = ButtonDefaults.buttonColors(Orange),
                    onClick = {
                        if (clubId != null && (
                                    viewModel.title != "" ||
                                    viewModel.description != "" ||
                                    viewModel.membershipFee != "" ||
                                    viewModel.selectedCategories.value.isNotEmpty())) {
                            viewModel.updateClub(clubId)
                            navController.popBackStack()
                        } else {
                            allValuesError = "Please fill in at least one value"
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text("Submit")
                }
                if (allValuesError != null) {
                    androidx.compose.material3.Text(
                        text = allValuesError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    )
}

@Composable
fun MultiSelectCategoryList(viewModel: EditClubDetailsViewModel) {
    fun onCategorySelected(categoryId: Int, isSelected: Boolean) {
        viewModel.selectedCategories.value = if (isSelected) {
            if (viewModel.selectedCategories.value.size < 5) viewModel.selectedCategories.value + categoryId else viewModel.selectedCategories.value
        } else {
            viewModel.selectedCategories.value - categoryId
        }
    }

    LazyColumn(
        modifier = Modifier.padding(16.dp).height(200.dp),
    ) {
        items(viewModel.categories) { category ->
            CategoryItem(
                category = category,
                isSelected = viewModel.selectedCategories.value.contains(category.id),
                onCategorySelected = ::onCategorySelected
            )
            HorizontalDivider()
        }
    }
}

@Composable
fun CategoryItem(category: Category, isSelected: Boolean, onCategorySelected: (Int, Boolean) -> Unit) {
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
