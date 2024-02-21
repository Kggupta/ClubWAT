package com.example.clubwat.viewmodels

import android.R.array
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clubwat.BuildConfig
import com.example.clubwat.model.ClubDetails
import com.example.clubwat.model.UserRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import androidx.compose.ui.graphics.Color
import com.example.clubwat.ui.theme.LightOrange
import kotlinx.coroutines.flow.update
import java.io.OutputStreamWriter

class ClubDetailsViewModel(private val userRepository: UserRepository) : ViewModel() {
    private var _club = MutableStateFlow<ClubDetails?>(null)
    var club = _club.asStateFlow()

    fun getClubTitle():String {
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
                        it!!.copy(isJoined = false, isJoinPending = false)
                    }
                } else {
                    if (_club.value!!.membershipFee > 0) {
                        _club.update {
                            it!!.copy(isJoined = false, isJoinPending = true)
                        }
                    } else {
                        _club.update {
                            it!!.copy(isJoined = true, isJoinPending = false)
                        }
                    }
                }
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