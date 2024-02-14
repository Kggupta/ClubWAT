package com.example.clubwat.model

import androidx.compose.runtime.MutableState

class UserRepository {
    var currentUser: User? = null

    fun createUser(firstName: MutableState<String>, lastName: MutableState<String>, email: MutableState<String>, password: MutableState<String>) {
        currentUser = User(null, firstName, lastName, email, password)
    }
}
