package com.example.clubwat.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clubwat.BuildConfig
import com.example.clubwat.model.UserProfile
import com.example.clubwat.repository.UserRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class ManageFriendsViewModel @Inject constructor(
    val userRepository: UserRepository
) : ViewModel() {

    private val _friends: MutableStateFlow<MutableList<UserProfile>> = MutableStateFlow(arrayListOf())
    var friends = _friends.asStateFlow()

    private val _reqFriends: MutableStateFlow<MutableList<UserProfile>> = MutableStateFlow(arrayListOf())
    var req_friends = _reqFriends.asStateFlow()

    var addFriendMessage = mutableStateOf("")
    var friendEmail = mutableStateOf("")
    fun getFriends() {
        viewModelScope.launch(Dispatchers.IO) {
            println("GET FRIENDS")
            try {
                println(userRepository.currentUser.value?.userId.toString())
                val obj = URL(BuildConfig.GET_FRIEND_URL)
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                con.setRequestProperty("Authorization", "Bearer " + userRepository.currentUser.value?.userId.toString())
                val responseCode = con.responseCode
                println("GET FRIENDS Response Code :: $responseCode")
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = con.inputStream.bufferedReader().use { it.readText() }
                    _friends.value = Gson().fromJson(response, object : TypeToken<List<UserProfile>>() {}.type)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getFriendsReq() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                println("GET FRIEND REQUEST")
                val obj = URL(BuildConfig.GET_FRIEND_URL + "requests")
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                con.setRequestProperty("Authorization", "Bearer " + userRepository.currentUser.value?.userId.toString())
                val responseCode = con.responseCode
                println("Response Code :: $responseCode")
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = con.inputStream.bufferedReader().use { it.readText() }
                    _reqFriends.value = Gson().fromJson(response, object : TypeToken<List<UserProfile>>() {}.type)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun sendFriendRequest(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            var created = false
            try {
                val url = URL(BuildConfig.GET_FRIEND_URL)
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Authorization", "Bearer " + userRepository.currentUser.value?.userId.toString())


                    val requestBody = """
                        {
                            "email": "$email"
                        }
                    """.trimIndent()

                    OutputStreamWriter(outputStream).use { it.write(requestBody) }

                    val responseCode = responseCode
                    created = responseCode == HttpURLConnection.HTTP_OK
                    if (created) {
                        // Handle the response
                        val response = inputStream.bufferedReader().use { it.readText() }

                        addFriendMessage.value = "Friend request sent!"
                        println("Friend req Successful: $response")
                    } else {
                        // Handle error
                        addFriendMessage.value = "Error could not send request."
                        val response = errorStream.bufferedReader().use { it.readText() }
                        println("Request Failed: $response")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteFriend(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val obj = URL(BuildConfig.GET_FRIEND_URL + "$id")
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "DELETE"
                con.setRequestProperty("Authorization", "Bearer " + userRepository.currentUser.value?.userId.toString())

                val responseCode = con.responseCode
                println("Response Code :: $responseCode")
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    getFriends()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun acceptFriend(id: Int) {
        val url = URL(BuildConfig.GET_FRIEND_URL + "approve-request")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "PUT"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Authorization", "Bearer " + userRepository.currentUser.value?.userId.toString())

                    val jsonBody = """
                {
                    "friend_id": $id
                }
                """.trimIndent()

                    OutputStreamWriter(outputStream).use { it.write(jsonBody) }
                    val responseCode = responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        getFriends()
                        getFriendsReq()
                    } else {
                        println("Failed to add friend: $responseCode")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}