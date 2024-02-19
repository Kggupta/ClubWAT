package com.example.clubwat.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf


class UserRepository {
    private val _currentUser = mutableStateOf<User?>(null)
    val currentUser: State<User?> get() = _currentUser

    fun createUser(firstName: MutableState<String>, lastName: MutableState<String>, email: MutableState<String>, password: MutableState<String>) {
        _currentUser.value = User("", firstName = firstName, lastName = lastName, email = email, password = password)
    }

    fun resetUser() {
        _currentUser.value = User(
            userId = null,
            firstName = mutableStateOf(""),
            lastName = mutableStateOf(""),
            email = mutableStateOf(""),
            password = mutableStateOf("")
        )
    }

    fun setUserId(newUserId: String) {
        val user = _currentUser.value
        if (user != null) {
            _currentUser.value = user.copy(userId = newUserId)
        }
    }
}

