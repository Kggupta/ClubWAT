package com.example.clubwat.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.clubwat.repository.UserRepository
import com.example.clubwat.viewmodels.EditClubDetailsViewModel
import com.example.clubwat.viewmodels.EventDetailsViewModel

class EditClubDetailsViewModelFactory(private val userRepository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditClubDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditClubDetailsViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
