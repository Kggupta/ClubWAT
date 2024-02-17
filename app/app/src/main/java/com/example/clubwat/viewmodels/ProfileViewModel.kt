package com.example.clubwat.viewmodels

import androidx.lifecycle.ViewModel
import com.example.clubwat.model.UserRepository

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {
    var firstName = userRepository.currentUser?.firstName
    var lastName = userRepository.currentUser?.lastName

    var user = userRepository.currentUser

    fun logout() {
        // Resetting the values to null
        user?.userId?.value = ""
        user?.firstName?.value = ""
        user?.lastName?.value = ""
        user?.email?.value = ""
        user?.password?.value = ""

    }

}