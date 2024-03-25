package com.example.clubwat.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clubwat.BuildConfig
import com.example.clubwat.model.Notification
import com.example.clubwat.repository.UserRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class InboxViewModel @Inject constructor(
    val userRepository: UserRepository,
) : ViewModel() {
    private val _notifications: MutableStateFlow<MutableList<Notification>> =
        MutableStateFlow(arrayListOf())
    var notifications = _notifications.asStateFlow()

    fun deleteNotification(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val obj = URL(BuildConfig.GET_NOTIFICATIONS_URL + "/$id")
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "DELETE"
                con.setRequestProperty(
                    "Authorization",
                    "Bearer " + userRepository.currentUser.value?.userId.toString()
                )

                val responseCode = con.responseCode
                println("Response Code :: $responseCode")
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    getNotifications()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getNotifications() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val obj = URL(BuildConfig.GET_NOTIFICATIONS_URL)
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                con.setRequestProperty(
                    "Authorization",
                    "Bearer " + userRepository.currentUser.value?.userId.toString()
                )
                val responseCode = con.responseCode
                println("Response Code :: $responseCode")
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = con.inputStream.bufferedReader().use { it.readText() }
                    val notifications: MutableList<Notification> =
                        Gson().fromJson(response, object : TypeToken<List<Notification>>() {}.type)
                    _notifications.value = notifications
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}