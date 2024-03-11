package com.example.clubwat.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clubwat.BuildConfig
import com.example.clubwat.model.Club
import com.example.clubwat.repository.UserRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL

class ForYouViewModel(private val userRepository: UserRepository) : ViewModel() {
    val allClubs = MutableStateFlow<List<Club>>(emptyList())

    fun getAllRecommendedClubs() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val obj = URL(BuildConfig.FOR_YOU_URL)
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                con.setRequestProperty("Authorization", "Bearer " + userRepository.currentUser.value?.userId.toString())
                val responseCode = con.responseCode
                println("Response Code :: $responseCode")
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = con.inputStream.bufferedReader().use { it.readText() }
                    val clubsList: List<Club> = Gson().fromJson(response, object : TypeToken<List<Club>>() {}.type)
                    allClubs.value = clubsList
                    println(clubsList)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}