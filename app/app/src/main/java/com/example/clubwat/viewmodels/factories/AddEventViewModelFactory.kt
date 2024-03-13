package com.example.clubwat.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.clubwat.repository.UserRepository
import com.example.clubwat.viewmodels.AddEventViewModel

class AddEventViewModelFactory(private val userRepository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddEventViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddEventViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}