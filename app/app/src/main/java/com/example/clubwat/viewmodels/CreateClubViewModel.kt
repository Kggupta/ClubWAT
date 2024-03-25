package com.example.clubwat.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clubwat.model.Category
import com.example.clubwat.model.CreateClubRequest
import com.example.clubwat.model.NetworkResult
import com.example.clubwat.repository.CreateClubRepository
import com.example.clubwat.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateClubViewModel @Inject constructor(
    val userRepository: UserRepository,
    private val createClubRepository: CreateClubRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateClubUiState.initial)
    val uiState: StateFlow<CreateClubUiState> = _uiState

    val errorMessage = mutableStateOf("")
    val clubCreatedSuccess = mutableStateOf(false)

    private fun checkIsFormValid(): Pair<Boolean, String> {
        with(uiState.value) {
            if (title.isBlank() || description.isBlank()) {
                return Pair(false, "Title or description is missing!")
            }
            if (membershipFee.isNaN() || membershipFee < 0) {
                return Pair(false, "Invalid Membership Fee!")
            }
        }
        return Pair(true, "")
    }

    private fun resetForm() {
        val currentState = uiState.value
        _uiState.value = currentState.copy(
            title = "",
            description = "",
            membershipFee = 0.0
        )
    }

    fun onTitleChange(newTitle: String) {
        val currentState = uiState.value
        _uiState.value = currentState.copy(title = newTitle)
    }

    fun onDescriptionChange(newDescription: String) {
        val currentState = uiState.value
        _uiState.value = currentState.copy(description = newDescription)
    }

    fun onMembershipFeeChange(newMembershipFee: Double) {
        val currentState = uiState.value
        _uiState.value = currentState.copy(membershipFee = newMembershipFee)
    }

    fun createClub() {
        val (isValid, errorMsg) = this.checkIsFormValid()
        if (!isValid) {
            errorMessage.value = errorMsg
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            when (val response = createClubRepository.createClub(
                CreateClubRequest(
                    title = uiState.value.title,
                    description = uiState.value.description,
                    membershipFee = uiState.value.membershipFee.toFloat(),
                    categories = uiState.value.selectedCategoryIds,
                ),
                userRepository.currentUser.value?.userId ?: ""
            )) {
                is NetworkResult.Success -> {
                    resetForm()
                    clubCreatedSuccess.value = true
                }

                is NetworkResult.Error -> {
                    // Can be used to handle errors in future...
                }
            }
        }
    }

    fun handleCategorySelection(categoryId: Int, isSelected: Boolean) {
        val currentSelected = _uiState.value.selectedCategoryIds.toMutableSet()
        if (isSelected) {
            currentSelected.add(categoryId)
        } else {
            currentSelected.remove(categoryId)
        }
        _uiState.value = _uiState.value.copy(selectedCategoryIds = currentSelected)
    }

    fun fetchCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val response = createClubRepository.getCategories(
                userRepository.currentUser.value?.userId.toString()
            )) {
                is NetworkResult.Success -> {
                    _uiState.emit(
                        _uiState.value.copy(
                            categories = response.data ?: emptyList(),
                            isLoading = false
                        )
                    )
                }

                is NetworkResult.Error -> {
                    _uiState.emit(
                        _uiState.value.copy(
                            isLoading = false
                        )
                    )
                }
            }
        }
    }


    data class CreateClubUiState(
        val title: String,
        val description: String,
        val membershipFee: Double,
        val categories: List<Category>,
        val selectedCategoryIds: Set<Int>,
        val isLoading: Boolean
    ) {
        companion object {
            val initial = CreateClubUiState(
                title = "",
                description = "",
                membershipFee = 0.0,
                categories = emptyList(),
                selectedCategoryIds = emptySet(),
                isLoading = true
            )
        }
    }
}