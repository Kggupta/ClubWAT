package com.example.clubwat.viewmodels

import androidx.lifecycle.ViewModel
import com.example.clubwat.repository.UserRepository

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {

    var firstName = userRepository.currentUser.value?.firstName
    var lastName = userRepository.currentUser.value?.lastName
    
    fun editInterests() {

    }

    fun editProfile() {

    }

    fun editFriends() {

    }

    fun logout() {
        userRepository.resetUser()
    }

}