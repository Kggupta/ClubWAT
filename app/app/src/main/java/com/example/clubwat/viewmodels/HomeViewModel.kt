package com.example.clubwat.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clubwat.BuildConfig
import com.example.clubwat.model.Club
import com.example.clubwat.model.Event
import com.example.clubwat.model.EventResponse
import com.example.clubwat.model.EventWrapper
import com.example.clubwat.repository.UserRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val userRepository: UserRepository,
) : ViewModel() {
    val allClubs = MutableStateFlow<List<Club>>(emptyList())
    val allEvents = MutableStateFlow<List<EventWrapper>>(emptyList())
    val spotlight = MutableStateFlow<Event?>(null)

    fun getAllClubs() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val obj = URL(BuildConfig.GET_ALL_CLUBS_FOR_USER)
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

    fun getAllEvents() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val obj = URL(BuildConfig.GET_ALL_EVENTS_FOR_USER)
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                con.setRequestProperty("Authorization", "Bearer " + userRepository.currentUser.value?.userId.toString())
                val responseCode = con.responseCode
                println("Response Code :: $responseCode")
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = con.inputStream.bufferedReader().use { it.readText() }
                    val eventResponse: EventResponse = Gson().fromJson(response, EventResponse::class.java)
                    val eventWrappersList: List<EventWrapper> = eventResponse.data
                    allEvents.value = eventWrappersList
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getSpotlight() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val obj = URL(BuildConfig.GET_SPOTLIGHTS)
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                con.setRequestProperty("Authorization", "Bearer " + userRepository.currentUser.value?.userId.toString())
                val responseCode = con.responseCode
                println("Response Code :: $responseCode")
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = con.inputStream.bufferedReader().use { it.readText() }
                    spotlight.value = Gson().fromJson(response, Event::class.java)
                } else {
                    spotlight.value = null
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun dismissSpotlight() {
        if (spotlight.value == null) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val obj = URL(BuildConfig.GET_SPOTLIGHTS + "/${spotlight.value!!.id}/dismiss")
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "PUT"
                con.setRequestProperty("Authorization", "Bearer " + userRepository.currentUser.value?.userId.toString())
                val responseCode = con.responseCode
                println("Response Code :: $responseCode")
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    spotlight.value = null
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
