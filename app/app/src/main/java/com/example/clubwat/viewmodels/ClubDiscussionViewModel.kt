package com.example.clubwat.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clubwat.model.ClubDetails
import com.example.clubwat.model.Data
import com.example.clubwat.model.NetworkResult
import com.example.clubwat.model.ProcessedData
import com.example.clubwat.model.SendDiscusionMessageRequest
import com.example.clubwat.repository.DiscussionRepository
import com.example.clubwat.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ClubDiscussionViewModel(
    val userRepository: UserRepository,
    private val discussionRepository: DiscussionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(DiscussionUiState.initial)
    val uiState: StateFlow<DiscussionUiState> = _uiState

    fun fetchUpdatedPosts(clubId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val response = discussionRepository.getPosts(
                clubId,
                userRepository.currentUser.value?.userId.toString()
            )) {
                is NetworkResult.Success -> {
                    _uiState.emit(
                        _uiState.value.copy(
                            posts = processData(response.data?.data ?: emptyList())
                        )
                    )
                    startPolling(clubId)
                }

                is NetworkResult.Error -> {
                    // Can be user to handle errors in future...
                    delay(500)
                    fetchUpdatedPosts(clubId) // try again after 5 seconds
                }
            }
        }
    }

    private fun startPolling(clubId: String) {
        viewModelScope.launch {
            while (true) { // ONLY IN VM SCOPE
                delay(15000) // delay 15 seconds then refetch
                fetchUpdatedPosts(clubId)
            }
        }
    }

    fun fetchClubDetails(clubId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val response = discussionRepository.getClub(
                clubId,
                userRepository.currentUser.value?.userId.toString()
            )) {
                is NetworkResult.Success -> {
                    _uiState.emit(
                        _uiState.value.copy(
                            clubDetails = response.data
                        )
                    )
                }

                is NetworkResult.Error -> {
                    // Can be used to handle errors in future...
                }
            }
        }
    }

    fun sendMessage(
        message: String,
        clubId: Int?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val response = discussionRepository.sendMessage(
                SendDiscusionMessageRequest(
                    clubId,
                    message
                ),
                userRepository.currentUser.value?.userId?.toString() ?: ""
            )) {
                is NetworkResult.Success -> {
                    fetchUpdatedPosts(clubId.toString())
                }

                is NetworkResult.Error -> {
                    // Can be used to handle errors in future...
                }
            }
        }
    }

    private fun processData(posts: List<Data>): List<ProcessedData> {
        val mutablePostList = mutableListOf<ProcessedData>()
        posts.forEach { post ->
            if (post.user.email == userRepository.currentUser.value?.email?.value) { // me identifier
                mutablePostList.add(ProcessedData(true, post))
            } else { // someone else
                mutablePostList.add(ProcessedData(messageData = post))
            }
        }
        return mutablePostList.reversed()
    }

    data class DiscussionUiState(
        val posts: List<ProcessedData>,
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
