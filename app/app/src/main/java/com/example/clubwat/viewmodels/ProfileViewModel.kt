package com.example.clubwat.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clubwat.BuildConfig
import com.example.clubwat.model.UserProfile
import com.example.clubwat.repository.UserRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    val userRepository: UserRepository,
) : ViewModel() {
    private var _profile = MutableStateFlow<UserProfile?>(null)
    var profile = _profile.asStateFlow()

    fun getUserProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val obj = URL(BuildConfig.GET_OWN_PROFILE)
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
                    _profile.value = Gson().fromJson(response, UserProfile::class.java)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val obj = URL(BuildConfig.USER_ROUTE)
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "DELETE"
                con.setRequestProperty(
                    "Authorization",
                    "Bearer " + userRepository.currentUser.value?.userId.toString()
                )
                val responseCode = con.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    logout()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun downloadData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val obj = URL(BuildConfig.USER_ROUTE + "/profile/self/download-data")
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                con.setRequestProperty(
                    "Authorization",
                    "Bearer " + userRepository.currentUser.value?.userId.toString()
                )
                val responseCode = con.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    println("Downloaded data")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // LOGOUT
    fun logout() {
        userRepository.resetUser()
    }

}