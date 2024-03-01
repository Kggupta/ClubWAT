package com.example.clubwat.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clubwat.model.ClubDetails
import com.example.clubwat.model.Data
import com.example.clubwat.model.NetworkResult
import com.example.clubwat.repository.DiscussionRepository
import com.example.clubwat.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ClubDiscussionViewModel(
    val userRepository: UserRepository,
    val discussionRepository: DiscussionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(DiscussionUiState.initial)
    val uiState: StateFlow<DiscussionUiState> = _uiState

    fun fetchUpdatedPosts(clubId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val response = discussionRepository.getPosts(clubId, userRepository.currentUser.value?.userId.toString())) {
                is NetworkResult.Success -> {
                    _uiState.emit(
                        _uiState.value.copy(
                            posts = processData(response.data?.data ?: emptyList())
                        )
                    )
                }
                is NetworkResult.Error -> {
                    // Can be user to handle errors in future...
                }
            }
        }
    }

    fun fetchClubDetails(clubId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val response = discussionRepository.getClub(clubId, userRepository.currentUser.value?.userId.toString())) {
                is NetworkResult.Success -> {
                    _uiState.emit(
                        _uiState.value.copy(
                            clubDetails = response.data
                        )
                    )
                }
                is NetworkResult.Error -> {
                    // Can be user to handle errors in future...
                }
            }
        }
    }

    private fun processData(posts: List<Data>): List<Data> {
        val mutablePostList = mutableListOf<Data>()
        posts.forEach { post ->
            if (post.user.email == userRepository.currentUser.value?.email?.value) { // me identifier
                mutablePostList.add(post.copy(user_id = 999))
            } else { // someone else
                mutablePostList.add(post)
            }
        }
        return mutablePostList
    }

    data class DiscussionUiState(
        val posts: List<Data>,
        val clubDetails: ClubDetails?
    ) {
        companion object {
            val initial = DiscussionUiState(
                posts = emptyList(),
                clubDetails = null
            )
        }
    }
}
