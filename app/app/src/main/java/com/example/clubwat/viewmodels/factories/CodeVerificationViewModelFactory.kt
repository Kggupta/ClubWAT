package com.example.clubwat.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.clubwat.model.UserRepository
import com.example.clubwat.viewmodels.CodeVerificationViewModel

class CodeVerificationViewModelFactory(private val userRepository: UserRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CodeVerificationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CodeVerificationViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
