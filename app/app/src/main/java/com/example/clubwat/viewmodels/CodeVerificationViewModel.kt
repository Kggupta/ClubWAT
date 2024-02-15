package com.example.clubwat.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clubwat.BuildConfig
import com.example.clubwat.model.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class CodeVerificationViewModel(private val userRepository: UserRepository): ViewModel() {
    private var firstName = userRepository.currentUser?.firstName
    private var lastName = userRepository.currentUser?.lastName
    private var email = userRepository.currentUser?.email
    private var password = userRepository.currentUser?.password
    var code = mutableStateOf("")
    var verificationError = mutableStateOf<String?>(null)

    fun register(callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            var registered = false
            try {
                val url = URL(BuildConfig.REGISTER_API_URL)
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
                        val jsonResponse = JSONObject(response)
                        val token = jsonResponse.optString("data", null.toString())
                        userRepository.currentUser?.userId?.value = token
                        println("Registration Successful: $response")
                    } else {
                        // Handle error
                        verificationError.value = "The verification code is incorrect."
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
                val url = URL(BuildConfig.EMAIL_VERIFICATION_URL)
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
