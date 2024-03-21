package com.example.clubwat.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clubwat.BuildConfig
import com.example.clubwat.model.Event
import com.example.clubwat.model.UserProfile
import com.example.clubwat.repository.UserRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class EventDetailsViewModel @Inject constructor(
    val userRepository: UserRepository,
) : ViewModel() {
    private var _event = MutableStateFlow<Event?>(null)
    var event = _event.asStateFlow()

    private val _friends: MutableStateFlow<MutableList<UserProfile>> = MutableStateFlow(arrayListOf())
    var friends = _friends.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ofPattern("MMM dd, h:mma")
    private var _profile = MutableStateFlow<UserProfile?>(null)
    var profile = _profile.asStateFlow()

    init {
        getUserProfile()
    }

    fun getEventTitle(): String {
        if (_event.value == null) return ""
        return _event.value!!.title
    }

    fun getEventDescription(): String {
        if (_event.value == null) return ""
        return _event.value!!.description
    }

    fun getLikeCount(): String {
        if (_event.value == null) return ""

        val formatInteger = NumberFormat.getNumberInstance()
        formatInteger.minimumFractionDigits = 0
        formatInteger.maximumFractionDigits = 0

        return formatInteger.format(event.value!!.likeCount)
    }

    fun attendEvent() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val obj = URL(BuildConfig.GET_EVENT_URL + event.value?.id + "/manage-attendance")
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "PUT"
                con.setRequestProperty("Authorization", "Bearer " + userRepository.currentUser.value?.userId.toString())
                val responseCode = con.responseCode
                println("Response Code :: $responseCode")
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    _event.update {
                        it!!.copy(isAttending = !it.isAttending)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun bookmarkEvent() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val obj = URL(BuildConfig.GET_EVENT_URL + event.value?.id + "/manage-bookmark")
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "PUT"
                con.setRequestProperty("Authorization", "Bearer " + userRepository.currentUser.value?.userId.toString())
                val responseCode = con.responseCode
                println("Response Code :: $responseCode")
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    _event.update {
                        it!!.copy(isBookmarked = !it.isBookmarked)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getStartDate(): String {
        if (event.value == null) return ""
        val dateTime = LocalDateTime.parse(
            event.value!!.startDate,
            DateTimeFormatter.ISO_DATE_TIME
        )
        return dateTime.format(formatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getEndDate(): String {
        if (event.value == null) return ""
        val dateTime = LocalDateTime.parse(
            event.value!!.endDate,
            DateTimeFormatter.ISO_DATE_TIME
        )
        return dateTime.format(formatter)
    }

    fun getLocation(): String {
        if (event.value == null) return ""
        return event.value!!.location
    }

    fun getEvent(eventId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val obj = URL(BuildConfig.GET_EVENT_URL + eventId + "/details")
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                con.setRequestProperty("Authorization", "Bearer " + userRepository.currentUser.value?.userId.toString())
                val responseCode = con.responseCode
                println("Response Code :: $responseCode")
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = con.inputStream.bufferedReader().use { it.readText() }
                    _event.value = Gson().fromJson(response, Event::class.java)
                }
            } catch (e: Exception) {
                e.printStackTrace()
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

    fun likeEvent() {
        if (_event.value == null) return
        viewModelScope.launch(Dispatchers.IO) {
            val url = URL(BuildConfig.FEEDBACK_URL + "/event/${_event.value!!.id}/${if (_event.value!!.isClientLikedEvent) "unlike" else "like"}")
            (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "PUT"
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Authorization", "Bearer " + userRepository.currentUser.value!!.userId )

                val responseCode = responseCode
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    println("Error Response: $responseCode")
                } else if (_event.value!!.isClientLikedEvent) {
                    _event.update {
                        it!!.copy(isClientLikedEvent = false, likeCount = it.likeCount - 1)
                    }
                } else {
                    _event.update {
                        it!!.copy(isClientLikedEvent = true, likeCount = it.likeCount + 1)
                    }
                }
            }
        }
    }

    fun shareEvent(userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = URL(BuildConfig.GET_NOTIFICATIONS_URL + "event")
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "PUT"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Authorization", "Bearer " + userRepository.currentUser.value?.userId.toString())

                    val body = """{"destinationUserId": $userId, "eventId": ${event.value!!.id}}"""

                    OutputStreamWriter(outputStream).use { it.write(body) }
                    val responseCode = responseCode
                    println("Response: $responseCode")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteEvent(eventID: String) {
        val id: Int = eventID.toInt()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val obj = URL(BuildConfig.GET_EVENT_URL + "delete" + "/$id")
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "DELETE"
                con.setRequestProperty("Authorization", "Bearer " + userRepository.currentUser.value?.userId.toString())

                val responseCode = con.responseCode
                println("Response Code :: $responseCode")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }



    fun getUserProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val obj = URL(BuildConfig.GET_OWN_PROFILE)
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                con.setRequestProperty("Authorization", "Bearer " + userRepository.currentUser.value?.userId.toString())
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


}