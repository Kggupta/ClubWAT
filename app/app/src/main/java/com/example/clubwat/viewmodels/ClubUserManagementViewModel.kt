package com.example.clubwat.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clubwat.BuildConfig
import com.example.clubwat.model.ClubMemberData
import com.example.clubwat.model.ClubMemberList
import com.example.clubwat.repository.UserRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL

class ClubUserManagementViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _members: MutableStateFlow<MutableList<ClubMemberData>> = MutableStateFlow(arrayListOf())
    var members = _members.asStateFlow()

    private val _isClientClubCreator: MutableStateFlow<Boolean> = MutableStateFlow(false);
    var isClientClubCreator = _isClientClubCreator.asStateFlow()

    private val _isClientClubAdmin: MutableStateFlow<Boolean> = MutableStateFlow(false);
    var isClientClubAdmin = _isClientClubAdmin.asStateFlow()

    fun approveUser(userId: Int, clubId: String) {
        if (!isClientClubAdmin.value) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val obj = URL("${BuildConfig.CLUB_ADMIN_URL}${clubId}/members/$userId/approve")
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "PUT"
                con.setRequestProperty("Authorization", "Bearer " + userRepository.currentUser.value?.userId.toString())
                val responseCode = con.responseCode
                println("Response Code :: $responseCode")
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    getMembers(clubId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun promoteUser(userId: Int, clubId: String) {
        if (!isClientClubCreator.value) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val obj = URL("${BuildConfig.CLUB_ADMIN_URL}${clubId}/members/$userId/promote")
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "PUT"
                con.setRequestProperty("Authorization", "Bearer " + userRepository.currentUser.value?.userId.toString())
                val responseCode = con.responseCode
                println("Response Code :: $responseCode")
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    getMembers(clubId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun demoteUser(userId: Int, clubId: String) {
        if (!isClientClubCreator.value) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val obj = URL("${BuildConfig.CLUB_ADMIN_URL}${clubId}/members/$userId/demote")
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "PUT"
                con.setRequestProperty("Authorization", "Bearer " + userRepository.currentUser.value?.userId.toString())
                val responseCode = con.responseCode
                println("Response Code :: $responseCode")
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    getMembers(clubId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun removeUser(userId: Int, clubId: String) {
        if (!isClientClubAdmin.value) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val obj = URL("${BuildConfig.CLUB_ADMIN_URL}${clubId}/members/$userId")
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "DELETE"
                con.setRequestProperty("Authorization", "Bearer " + userRepository.currentUser.value?.userId.toString())
                val responseCode = con.responseCode
                println("Response Code :: $responseCode")
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    getMembers(clubId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getMembers(clubId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val obj = URL(BuildConfig.CLUB_ADMIN_URL + clubId + "/members")
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                con.setRequestProperty("Authorization", "Bearer " + userRepository.currentUser.value?.userId.toString())
                val responseCode = con.responseCode
                println("Response Code :: $responseCode")
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = con.inputStream.bufferedReader().use { it.readText() }
                    val clubMemberList = Gson().fromJson(response, ClubMemberList::class.java)

                    _members.value = clubMemberList.memberList.toMutableList()
                    _isClientClubAdmin.value = clubMemberList.isIAdmin
                    _isClientClubCreator.value = clubMemberList.isICreator
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}