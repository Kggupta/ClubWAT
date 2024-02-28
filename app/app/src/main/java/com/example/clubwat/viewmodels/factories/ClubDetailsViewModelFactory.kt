package com.example.clubwat.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.clubwat.model.UserRepository
import com.example.clubwat.viewmodels.ClubDetailsViewModel

class ClubDetailsViewModelFactory(private val userRepository: UserRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClubDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ClubDetailsViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
