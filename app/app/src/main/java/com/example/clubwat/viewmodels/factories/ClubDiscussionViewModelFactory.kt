package com.example.clubwat.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.clubwat.repository.DiscussionRepository
import com.example.clubwat.repository.UserRepository
import com.example.clubwat.viewmodels.ClubDiscussionViewModel

class ClubDiscussionViewModelFactory(private val userRepository: UserRepository, private val discussionRepository: DiscussionRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClubDiscussionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ClubDiscussionViewModel(userRepository, discussionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}