package com.example.clubwat.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clubwat.BuildConfig
import com.example.clubwat.model.Interest
import com.example.clubwat.model.InterestsResponse
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
class UserInterestsViewModel @Inject constructor(
    val userRepository: UserRepository
) : ViewModel() {
    var fillAllfields = mutableStateOf<String?>(null)

    // users interests
    private var _userFaculty = MutableStateFlow<Interest?>(null)
    var userFaculty = _userFaculty.asStateFlow()

    private var _userEthnicity = MutableStateFlow<Interest?>(null)
    var userEthnicity = _userEthnicity.asStateFlow()

    private var _userReligion = MutableStateFlow<Interest?>(null)
    var userReligion = _userReligion.asStateFlow()

    private var _userProgram = MutableStateFlow<Interest?>(null)
    var userProgram = _userProgram.asStateFlow()

    var selectedUserHobbies = mutableStateOf(setOf<Int>())


    // all categories
    private val _faculties: MutableStateFlow<MutableList<Interest>> =
        MutableStateFlow(arrayListOf())
    var faculties = _faculties.asStateFlow()

    private val _ethnicities: MutableStateFlow<MutableList<Interest>> =
        MutableStateFlow(arrayListOf())
    var ethnicities = _ethnicities.asStateFlow()

    private val _religions: MutableStateFlow<MutableList<Interest>> =
        MutableStateFlow(arrayListOf())
    var religions = _religions.asStateFlow()

    private val _programs: MutableStateFlow<MutableList<Interest>> = MutableStateFlow(arrayListOf())
    var programs = _programs.asStateFlow()

    private val _hobbies: MutableStateFlow<MutableList<Interest>> = MutableStateFlow(arrayListOf())
    var hobbies = _hobbies.asStateFlow()

    init {
        getInterests()
        getUserInterests()
    }

    fun updateUserFaculty(interest: Interest) {
        _userFaculty.value = interest.copy()
    }

    fun updateUserEthnicity(interest: Interest) {
        _userEthnicity.value = interest.copy()
    }

    fun updateUserReligion(interest: Interest) {
        _userReligion.value = interest.copy()
    }

    fun updateUserProgram(interest: Interest) {
        _userProgram.value = interest.copy()
    }

    fun getInterests() {
        viewModelScope.launch(Dispatchers.IO) {
            println("GET INTERESTS")
            try {
                val obj = URL(BuildConfig.GET_INTERESTS + "/all")
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                con.setRequestProperty(
                    "Authorization",
                    "Bearer " + userRepository.currentUser.value?.userId.toString()
                )
                val responseCode = con.responseCode
                println("GET INTERESTS Response Code :: $responseCode")
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = con.inputStream.bufferedReader().use { it.readText() }
                    val interestsResponse = Gson().fromJson(response, InterestsResponse::class.java)

                    _faculties.value = interestsResponse.faculties!!.toMutableList()
                    _ethnicities.value = interestsResponse.ethnicities!!.toMutableList()
                    _religions.value = interestsResponse.religions!!.toMutableList()
                    _programs.value = interestsResponse.programs!!.toMutableList()
                    _hobbies.value = interestsResponse.hobbies!!.toMutableList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getUserInterests() {
        viewModelScope.launch(Dispatchers.IO) {
            println("GET USER INTERESTS before")

            try {
                val obj = URL(BuildConfig.GET_INTERESTS)
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                con.setRequestProperty(
                    "Authorization",
                    "Bearer " + userRepository.currentUser.value?.userId.toString()
                )
                val responseCode = con.responseCode
                println("GET INTERESTS Response Code :: $responseCode")
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = con.inputStream.bufferedReader().use { it.readText() }
                    val gson = Gson()
                    val type = object : TypeToken<List<Interest>>() {}.type
                    val interestsResponse: List<Interest> = gson.fromJson(response, type)

                    _userFaculty.value = interestsResponse.find { it.type == "faculty" }
                    _userEthnicity.value = interestsResponse.find { it.type == "ethnicity" }
                    _userReligion.value = interestsResponse.find { it.type == "religion" }
                    _userProgram.value = interestsResponse.find { it.type == "program" }
                    selectedUserHobbies.value =
                        interestsResponse.filter { it.type == "hobby" }.map { it.id }.toSet()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveInterests() {
        if (userFaculty.value == null ||
            userEthnicity.value == null ||
            userReligion.value == null ||
            userProgram.value == null
        ) {
            fillAllfields.value = "Fill All Interests"
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val gson = Gson()
                val requestBody = mapOf(
                    "faculty" to userFaculty.value!!.id,
                    "ethnicity" to userEthnicity.value!!.id,
                    "religion" to userReligion.value!!.id,
                    "program" to userProgram.value!!.id,
                    "hobbies" to selectedUserHobbies.value.toList()
                )
                val requestBodyString = gson.toJson(requestBody)

                val url = URL(BuildConfig.GET_INTERESTS)
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "PUT"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty(
                        "Authorization",
                        "Bearer " + userRepository.currentUser.value?.userId.toString()
                    )

                    OutputStreamWriter(outputStream).use { it.write(requestBodyString) }
                    val responseCode = responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        fillAllfields.value = "Success! Interests are saved."
                        getUserInterests()
                    } else {
                        fillAllfields.value = "Error, could not save interests."
                        println("Failed to add to interests: $responseCode")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}