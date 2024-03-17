package com.example.clubwat.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clubwat.model.ClubDetails
import com.example.clubwat.model.DeleteDiscussionMessageRequest
import com.example.clubwat.model.MessageData
import com.example.clubwat.model.NetworkResult
import com.example.clubwat.model.ProcessedData
import com.example.clubwat.model.SendDiscussionMessageRequest
import com.example.clubwat.repository.DiscussionRepository
import com.example.clubwat.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClubDiscussionViewModel @Inject constructor(
    val userRepository: UserRepository,
    private val discussionRepository: DiscussionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(DiscussionUiState.initial)
    val uiState: StateFlow<DiscussionUiState> = _uiState

    fun fetchUpdatedPosts(clubId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val response = discussionRepository.getMessages(
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
                    // Can be used to handle errors in future...
                    delay(500)
                    fetchUpdatedPosts(clubId) // try again after 5 seconds
                }
            }
        }
    }
    private fun startPolling(clubId: String) {
        viewModelScope.launch {
            while (true) { // ONLY IN VM SCOPE
                delay(60000) // delay 60s then refetch
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
                            clubDetails = response.data,
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
                SendDiscussionMessageRequest(
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

    fun deleteMessage(
        messageId: Int,
        clubId: Int?) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val response = discussionRepository.deleteMessage(DeleteDiscussionMessageRequest(
                clubId,
                messageId
            ),
                userRepository.currentUser.value?.userId?.toString() ?: ""
            )) {
                is NetworkResult.Success -> {
                    val messageToDelete = _uiState.value.posts.find { it.messageData.id == messageId }
                    val updatedState = _uiState.value.posts.toMutableList().apply { this.remove(messageToDelete) }

                    _uiState.emit(
                        _uiState.value.copy(
                            posts = updatedState
                        )
                    )
                }
                is NetworkResult.Error -> {
                    // Can be used to handle errors in future...
                }
            }
        }
    }

    private fun processData(posts: List<MessageData>): List<ProcessedData> {
        val mutablePostList = mutableListOf<ProcessedData>()
        posts.forEach { post ->
            if (post.user.email == userRepository.currentUser.value?.email?.value) { // me identifier
                mutablePostList.add(ProcessedData(isMe = true, messageData = post))
            } else { // someone else
                mutablePostList.add(ProcessedData(isMe = false, messageData = post))
            }
        }
        return mutablePostList.reversed()
    }

    data class DiscussionUiState(
        val posts: List<ProcessedData>,
        val clubDetails: ClubDetails?,
        val isLoading: Boolean
    ) {
        companion object {
            val initial = DiscussionUiState(
                posts = emptyList(),
                clubDetails = null,
                isLoading = true // start loading
            )
        }
    }
}
