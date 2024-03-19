package com.example.clubwat.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clubwat.BuildConfig
import com.example.clubwat.model.ClubDetails
import com.example.clubwat.model.UserProfile
import com.example.clubwat.repository.UserRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class ClubDetailsViewModel(private val userRepository: UserRepository) : ViewModel() {
    private var _club = MutableStateFlow<ClubDetails?>(null)
    var club = _club.asStateFlow()

    private val _friends: MutableStateFlow<MutableList<UserProfile>> = MutableStateFlow(arrayListOf())
    var friends = _friends.asStateFlow()

    fun getClubTitle(): String {
        if (_club.value == null) return ""
        return _club.value!!.title
    }

    fun getClubDescription(): String {
        if (_club.value == null) return ""
        return _club.value!!.description
    }

    fun updateClubMembership() {
        if (_club.value == null) return
        viewModelScope.launch(Dispatchers.IO) {
            val url = URL(BuildConfig.GET_CLUB_URL + _club.value!!.id + "/manage-membership")
            (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "PUT"
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Authorization", "Bearer " + userRepository.currentUser.value!!.userId )

                val responseCode = responseCode
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    println("Error Response: $responseCode")
                } else if (_club.value!!.isJoined || _club.value!!.isJoinPending) {
                    _club.update {
                        it!!.copy(isJoined = false, isJoinPending = false, isClubAdmin = false)
                    }
                } else {
                    if (_club.value!!.membershipFee > 0) {
                        _club.update {
                            it!!.copy(isJoined = false, isJoinPending = true, isClubAdmin = false)
                        }
                    } else {
                        _club.update {
                            it!!.copy(isJoined = true, isJoinPending = false, isClubAdmin = false)
                        }
                    }
                }
            }
        }
    }

    fun likeClub() {
        if (_club.value == null) return
        viewModelScope.launch(Dispatchers.IO) {
            val url = URL(BuildConfig.FEEDBACK_URL + "/club/${_club.value!!.id}/${if (_club.value!!.isClientLikedClub) "unlike" else "like"}")
            (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "PUT"
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Authorization", "Bearer " + userRepository.currentUser.value!!.userId )

                val responseCode = responseCode
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    println("Error Response: $responseCode")
                } else if (_club.value!!.isClientLikedClub) {
                    _club.update {
                        it!!.copy(isClientLikedClub = false, likeCount = it.likeCount - 1)
                    }
                } else {
                    _club.update {
                        it!!.copy(isClientLikedClub = true, likeCount = it.likeCount + 1)
                    }
                }
            }
        }
    }

    fun getFriends() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val obj = URL(BuildConfig.GET_FRIEND_URL)
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                con.setRequestProperty("Authorization", "Bearer " + userRepository.currentUser.value?.userId.toString())
                val responseCode = con.responseCode
                println("Response Code :: $responseCode")
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = con.inputStream.bufferedReader().use { it.readText() }
                    val friends: MutableList<UserProfile> = Gson().fromJson(response, object : TypeToken<List<UserProfile>>() {}.type)
                    _friends.value = friends
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun shareClub(userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = URL(BuildConfig.GET_NOTIFICATIONS_URL + "club")
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "PUT"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Authorization", "Bearer " + userRepository.currentUser.value?.userId.toString())

                    val body = """{"destinationUserId": $userId, "clubId": ${club.value!!.id}}"""

                    OutputStreamWriter(outputStream).use { it.write(body) }
                    val responseCode = responseCode
                    println("Response: $responseCode")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getClub(clubId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val obj = URL(BuildConfig.GET_CLUB_URL + clubId)
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                con.setRequestProperty("Authorization", "Bearer " + userRepository.currentUser.value?.userId.toString())
                val responseCode = con.responseCode
                println("Response Code :: $responseCode")
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = con.inputStream.bufferedReader().use { it.readText() }
                    _club.value = Gson().fromJson(response, ClubDetails::class.java)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}