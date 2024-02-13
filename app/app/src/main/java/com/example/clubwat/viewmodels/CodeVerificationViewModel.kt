package com.example.clubwat.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clubwat.model.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class CodeVerificationViewModel(private val userRepository: UserRepository): ViewModel() {
    var firstName = userRepository.currentUser?.firstName
    var lastName = userRepository.currentUser?.lastName
    var email = userRepository.currentUser?.email
    var password = userRepository.currentUser?.password
    var code = mutableStateOf("")
    fun validateVerificationCode(verificationCode: Int) {

    }

    fun register() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = URL("http://10.0.2.2:3000/api/v1/user/register")
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    println(email?.value)
                    println(code)
                    println(password)

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
                    println(responseCode)
                    if (responseCode == HttpURLConnection.HTTP_OK) {
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
        }
    }

    fun sendVerificationCode() {}
}