package com.example.clubwat.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clubwat.model.ApproveClubRequest
import com.example.clubwat.model.Club
import com.example.clubwat.model.NetworkResult
import com.example.clubwat.repository.ApproveClubRepository
import com.example.clubwat.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApproveClubViewModel @Inject constructor(
    val userRepository: UserRepository,
    private val approveClubRepository: ApproveClubRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ApproveClubViewModel.ApproveClubUiState.initial)
    val uiState: StateFlow<ApproveClubViewModel.ApproveClubUiState> = _uiState


    fun fetchClubs() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val response = approveClubRepository.getUnapprovedClubs(
                userRepository.currentUser.value?.userId.toString()
            )) {
                is NetworkResult.Success -> {
                    _uiState.emit(
                        _uiState.value.copy(
                            clubs = response.data ?: emptyList(),
                            isLoading = false
                        )
                    )
                }

                is NetworkResult.Error -> {
                    // Can be used to handle errors in future...
                    _uiState.emit(
                        _uiState.value.copy(
                            isLoading = false
                        )
                    )
                }
            }
        }
    }

    fun updateClub(clubId: Int, approval: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val response = approveClubRepository.updateClubApprovalStatus(ApproveClubRequest(
                clubId,
                if (approval) "approve" else "delete"
            ),
                userRepository.currentUser.value?.userId?.toString() ?: ""
            )) {
                is NetworkResult.Success -> {
                    val clubToDelete = _uiState.value.clubs.find { it.id.toInt() == clubId }
                    val updatedState = _uiState.value.clubs.toMutableList().apply { this.remove(clubToDelete) }

                    _uiState.emit(
                        _uiState.value.copy(
                            clubs = updatedState
                        )
                    )
                }
                is NetworkResult.Error -> {
                    // Can be used to handle errors in future...
                }
            }
        }
    }
    data class ApproveClubUiState(
        val clubs: List<Club>,
        val isLoading: Boolean,
    ) {
        companion object {
            val initial = ApproveClubUiState(
                clubs = emptyList(),
                isLoading = true
            )
        }
    }
}
