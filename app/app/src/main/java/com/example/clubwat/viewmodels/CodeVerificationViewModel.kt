package com.example.clubwat.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clubwat.model.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class CodeVerificationViewModel(private val userRepository: UserRepository): ViewModel() {
    private var firstName = userRepository.currentUser?.firstName
    private var lastName = userRepository.currentUser?.lastName
    private var email = userRepository.currentUser?.email
    private var password = userRepository.currentUser?.password
    var code = mutableStateOf("")

    fun register(callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            var registered = false
            try {
                val url = URL("http://10.0.2.2:3000/api/v1/user/register")
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")

                    val requestBody = """
                        {
                            "email": "${email?.value}",
                            "password": "${password?.value}",
                            "first_name": "${firstName?.value}",
                            "last_name": "${lastName?.value}",
                            "code": ${code.value}
                        }
                    """.trimIndent()

                    OutputStreamWriter(outputStream).use { it.write(requestBody) }

                    val responseCode = responseCode
                    registered = responseCode == HttpURLConnection.HTTP_CREATED
                    println(responseCode)
                    if (registered) {
                        // Handle the response
                        val response = inputStream.bufferedReader().use { it.readText() }
                        println("Registration Successful: $response")
                    } else {
                        // Handle error
                        val response = errorStream.bufferedReader().use { it.readText() }
                        println("Registration Failed: $response")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            withContext(Dispatchers.Main) {
                callback(registered)
            }
        }
    }

    fun sendVerificationEmail() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                println(email?.value)
                val url = URL("http://10.0.2.2:3000/api/v1/user/registration-email-verification")
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")

                    val body = """{"email": "${email?.value}"}"""

                    OutputStreamWriter(outputStream).use { it.write(body) }
                    val responseCode = responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val response = inputStream.bufferedReader().use { it.readText() }
                        println("Response: $response")
                    } else {
                        val response = errorStream.bufferedReader().use { it.readText() }
                        println("Error Response: $response")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
