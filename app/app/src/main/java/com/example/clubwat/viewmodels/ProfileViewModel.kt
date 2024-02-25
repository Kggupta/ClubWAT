package com.example.clubwat.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.clubwat.model.UserRepository

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {

    var firstName = userRepository.currentUser.value?.firstName
    var lastName = userRepository.currentUser.value?.lastName
    var faculty = mutableStateOf("")
    var program = mutableStateOf("")
    var hobbies = mutableStateOf<List<String>>(listOf())
    var ethnicity = mutableStateOf("")
    var religion = mutableStateOf("")
    var currentInput = mutableStateOf("")


    fun addHobbies() {
        if (currentInput.value.isNotBlank()) {
            hobbies.value = hobbies.value + currentInput.value.trim()
            currentInput.value = ""
        }
        println("ADDED")
        println(faculty.value)
    }

    fun removeHobby(program: String) {
        hobbies.value = hobbies.value.filter { it != program }
    }

    fun editInterests(facultyInput: String, ethnicityInput: String, religionInput: String) {
        faculty.value = facultyInput
        ethnicity.value = ethnicityInput
        religion.value = religionInput
        println("faculty input")
        println(faculty.value)
    }


    fun editProfile() {

    }

    fun editFriends() {

    }

    fun logout() {
        userRepository.resetUser()
    }

}