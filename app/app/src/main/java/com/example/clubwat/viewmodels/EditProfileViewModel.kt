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
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject


@HiltViewModel
class EditProfileViewModel @Inject constructor(
    val userRepository: UserRepository
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

    fun updateNotificationPreferences(value: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = URL(BuildConfig.GET_OWN_PROFILE + "/notification")
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "PUT"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty(
                        "Authorization",
                        "Bearer " + userRepository.currentUser.value!!.userId
                    )

                    val jsonObject = JSONObject().apply {
                        put("notificationFlag", value)
                    }
                    OutputStreamWriter(outputStream).use { it.write(jsonObject.toString()) }

                    val responseCode = responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val response = inputStream.bufferedReader().use { it.readText() }
                        _profile.value = Gson().fromJson(response, UserProfile::class.java)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}