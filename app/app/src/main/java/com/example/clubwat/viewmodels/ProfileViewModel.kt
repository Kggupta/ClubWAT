package com.example.clubwat.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.clubwat.model.User
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
    var oldPassword = mutableStateOf("")
    var newPassword = mutableStateOf("")
    var addFriend = mutableStateOf("")
    var passwordError = mutableStateOf<String?>(null)
    var passwordSuccess = mutableStateOf<String?>(null)
    var emailError = mutableStateOf<String?>(null)


    private var oldPasswordStored = userRepository.currentUser.value?.password

    var faculties = mutableStateOf<List<String>>(listOf())
    var religions = mutableStateOf<List<String>>(listOf())

    // this is an example of how it will look
    val friends = listOf(
        User(
            userId = "1",
            firstName = mutableStateOf("John"),
            lastName = mutableStateOf("Doe"),
            email = mutableStateOf("john.doe@example.com"),
            password = mutableStateOf("password123")
        ),
        User(
            userId = "2",
            firstName = mutableStateOf("Jane"),
            lastName = mutableStateOf("Doe"),
            email = mutableStateOf("jane.doe@example.com"),
            password = mutableStateOf("password456")
        )
        // Add more users as needed
    )


    // send to api as: (id, name, type)
    //    where 'type' would be the categories you've got already (program, hobbies, ethnicity, religion).



    fun addHobbies() {
        if (currentInput.value.isNotBlank()) {
            hobbies.value = hobbies.value + currentInput.value.trim()
            currentInput.value = ""
        }
        println("ADDED")
        println(faculty.value)
    }

    fun removeHobby(program: String) {
        val index = hobbies.value.indexOf(program)
        if (index != -1) {
            hobbies.value = hobbies.value.toMutableList().apply {
                removeAt(index)
            }
        }
    }

    fun addFriend(email: String) {
        // api to add friend
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

    fun getHobbies() {

    }

    private fun isValidPassword(password: String): Boolean {
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,}$"
        return password.matches(passwordPattern.toRegex())
    }

    fun validatePasswordAndSignUp(password: String): Boolean {
        if (!isValidPassword(password)) {
            passwordError.value = "Please ensure password is 8 characters long, contains one symbol, and has both uppercase and lowercase letters."
            return false
        } else {
            passwordError.value = null
            return true
        }

    }

    fun editPassword() {
        if (oldPasswordStored != oldPassword) {
            passwordError.value += "  The old password is incorrect, unable to make change"
        }
        if (validatePasswordAndSignUp(newPassword.value)) {
            passwordSuccess.value = "Password has been updated"
            // call api to update password
        }


    }



    // Handling edit action
    fun deleteFriend(userID: String) {

    }

    fun getFaculties() {
        // call api to get all faculties and store in arr
    }

    fun getEthicity() {

    }

    fun getReligion() {

    }

    fun logout() {
        userRepository.resetUser()
    }

}