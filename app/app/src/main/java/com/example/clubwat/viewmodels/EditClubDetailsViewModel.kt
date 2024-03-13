package com.example.clubwat.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clubwat.BuildConfig
import com.example.clubwat.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class EditClubDetailsViewModel(private val userRepository: UserRepository): ViewModel() {
    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var membershipFee by mutableStateOf("")
    var categories = arrayListOf<Int>()

    fun updateClub(clubId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = URL("${BuildConfig.GET_CLUB_URL}${clubId}")
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "PUT"
                    doOutput = true
                    setRequestProperty("Authorization", "Bearer ${userRepository.currentUser.value?.userId}")
                    setRequestProperty("Content-Type", "application/json")

                    val body = JSONObject().apply {
                        put("title", title)
                        put("description", description)
                        put("membership_fee", membershipFee.toInt())
//                        put("categories", JSONArray(categories))
                    }.toString()

                    print(body)

                    OutputStreamWriter(outputStream).use { it.write(body) }

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val response = inputStream.bufferedReader().use { it.readText() }
                        println("Response: $response")
                    } else {
                        val errorResponse = errorStream.bufferedReader().use { it.readText() }
                        println("Error: $errorResponse")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
