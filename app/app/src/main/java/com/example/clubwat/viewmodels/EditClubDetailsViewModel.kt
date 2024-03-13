package com.example.clubwat.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clubwat.BuildConfig
import com.example.clubwat.model.Category
import com.example.clubwat.model.ClubDetails
import com.example.clubwat.repository.UserRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class EditClubDetailsViewModel(private val userRepository: UserRepository): ViewModel() {
    private var _club = MutableStateFlow<ClubDetails?>(null)
    var club = _club.asStateFlow()

    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var membershipFee by mutableStateOf("")
    var categories = listOf<Category>()
    var selectedCategories = mutableStateOf(setOf<Int>())

    var allValuesError = mutableStateOf<String?>(null)

    fun getClubTitle():String {
        if (_club.value == null) return ""
        return _club.value!!.title
    }

    fun getClubDescription(): String {
        if (_club.value == null) return ""
        return _club.value!!.description
    }

    fun getClubMembershipFee(): String {
        if (_club.value == null) return ""
        return _club.value!!.membershipFee.toString()
    }

    private fun getClubCategories(): List<Category> {
        if (_club.value == null) return listOf()
        return _club.value!!.categories
    }

    fun getCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val obj = URL(BuildConfig.GET_ALL_CATEGORIES)
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                con.setRequestProperty("Authorization", "Bearer " + userRepository.currentUser.value?.userId.toString())
                val responseCode = con.responseCode
                println("Response Code :: $responseCode")
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = con.inputStream.bufferedReader().use { it.readText() }
                    val categoriesList: List<Category> = Gson().fromJson(response, object : TypeToken<List<Category>>() {}.type)
                    categories = categoriesList
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

    fun updateClub(clubId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = URL("${BuildConfig.GET_CLUB_URL}${clubId}")
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "PUT"
                    doOutput = true
                    setRequestProperty("Authorization", "Bearer ${userRepository.currentUser.value?.userId}")
                    setRequestProperty("Content-Type", "application/json")
                    val categoriesJsonArray = JSONArray()
                    if (selectedCategories.value.isNotEmpty()) {
                        selectedCategories.value.forEach { categoryId ->
                            categoriesJsonArray.put(categoryId)
                        }
                    } else {
                        getClubCategories().forEach { category ->
                            categoriesJsonArray.put(category.id)
                        }
                    }
                    val body = JSONObject().apply {
                        put("title", title.takeIf { it.isNotBlank() } ?: getClubTitle())
                        put("description", description.takeIf { it.isNotBlank() } ?: getClubDescription())
                        put("membership_fee", membershipFee.toIntOrNull() ?: getClubMembershipFee().toDouble())
                        put("categories", categoriesJsonArray)
                    }.toString()
                    OutputStreamWriter(outputStream).use { it.write(body) }

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val response = inputStream.bufferedReader().use { it.readText() }
                        println("Response: $response")
                    } else {
                        val errorResponse = errorStream.bufferedReader().use { it.readText() }
                        println("Error: $errorResponse")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
