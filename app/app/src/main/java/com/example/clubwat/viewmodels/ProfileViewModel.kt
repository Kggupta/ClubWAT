package com.example.clubwat.viewmodels

import androidx.lifecycle.ViewModel
import com.example.clubwat.model.UserRepository

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {

    var firstName = userRepository.currentUser.value?.firstName

    fun logout() {
        userRepository.resetUser()
    }

}