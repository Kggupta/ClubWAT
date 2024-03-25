package com.example.clubwat.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clubwat.BuildConfig
import com.example.clubwat.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject


@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    val userRepository: UserRepository
) : ViewModel() {
    var oldPassword = mutableStateOf("")
    var newPassword = mutableStateOf("")
    var confirmNewPassword = mutableStateOf("")
    var passwordError = mutableStateOf<String?>(null)
    var passwordSuccess = mutableStateOf<String?>(null)

    private fun isValidPassword(password: String): Boolean {
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,}$"
        return password.matches(passwordPattern.toRegex())
    }

    fun validatePassword(password: String): Boolean {
        if (password != confirmNewPassword.value) {
            passwordError.value = "New passwords do not match."
            passwordSuccess.value = null
            return false
        } else if (!isValidPassword(password)) {
            passwordError.value =
                "Please ensure password is 8 characters long, contains one symbol, and has both uppercase and lowercase letters."
            return false
        } else {
            passwordError.value = null
            return true
        }
    }

    fun editPassword(oldPassword: String, newPassword: String) {
        if (!validatePassword(newPassword)) return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = URL(BuildConfig.PASSWORD_CHANGE)
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "PUT"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty(
                        "Authorization",
                        "Bearer " + userRepository.currentUser.value?.userId.toString()
                    )

                    val jsonBody = """{
                    "oldPassword": "$oldPassword",
                    "password": "$newPassword"
                }"""

                    OutputStreamWriter(outputStream).use { it.write(jsonBody) }
                    val responseCode = responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        passwordSuccess.value = "Password Changed!"
                        passwordError.value = null
                        println("Password successfully updated")
                        val response = inputStream.bufferedReader().use { it.readText() }
                        val jsonResponse = JSONObject(response)
                        val token = jsonResponse.optString("data", null.toString())
                        userRepository.setUserId(token)
                    } else {
                        passwordError.value = "The passwords don't match"
                        passwordSuccess.value = null
                        println("Failed to update password: $responseCode")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}