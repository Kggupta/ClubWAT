package com.example.clubwat.viewmodels

import androidx.lifecycle.ViewModel
import com.example.clubwat.model.UserRepository

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {

    private var user = userRepository.currentUser
    var firstName = userRepository.currentUser?.firstName
    private var lastName = userRepository.currentUser?.lastName

    fun logout() {
        // Resetting the values to null
        user?.userId?.value = ""
        user?.firstName?.value = ""
        user?.lastName?.value = ""
        user?.email?.value = ""
        user?.password?.value = ""
    }

}