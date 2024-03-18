package com.example.clubwat.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
    var faculty by mutableStateOf<Interest?>(null)
    var ethicity by mutableStateOf<Interest?>(null)
    var religion by mutableStateOf<Interest?>(null)
    var program by mutableStateOf<Interest?>(null)
    var hobby by mutableStateOf<List<Interest>?>(null)

    var facultyID by mutableStateOf(0)
    var ethicityID by mutableStateOf(0)
    var religionID by  mutableStateOf(0)
    var programID by  mutableStateOf(0)
    var hobbyID by mutableStateOf(listOf<Int>())

    // all categories
    var faculties by  mutableStateOf<List<Interest>?>(null)
    var ethnicities by  mutableStateOf<List<Interest>?>(null)
    var religions by  mutableStateOf<List<Interest>?>(null)
    var programs by  mutableStateOf<List<Interest>?>(null)
    var hobbies by mutableStateOf<List<Interest>?>(null)

    init {
        getInterests()
        getUserInterests()
    }

    fun getInterests() {
        viewModelScope.launch(Dispatchers.IO) {
            println("GET INTERESTS")
            try {
                val obj = URL(BuildConfig.GET_INTERESTS + "/all")
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                con.setRequestProperty("Authorization", "Bearer " + userRepository.currentUser.value?.userId.toString())
                val responseCode = con.responseCode
                println("GET INTERESTS Response Code :: $responseCode")
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = con.inputStream.bufferedReader().use { it.readText() }
                    val interestsResponse = Gson().fromJson(response, InterestsResponse::class.java)

                    faculties = interestsResponse.faculties
                    ethnicities = interestsResponse.ethnicities
                    religions = interestsResponse.religions
                    programs = interestsResponse.programs
                    hobbies = interestsResponse.hobbies

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
                con.setRequestProperty("Authorization", "Bearer " + userRepository.currentUser.value?.userId.toString())
                val responseCode = con.responseCode
                println("GET INTERESTS Response Code :: $responseCode")
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = con.inputStream.bufferedReader().use { it.readText() }
                    val gson = Gson()
                    val type = object : TypeToken<List<Interest>>() {}.type
                    val interestsResponse: List<Interest> = gson.fromJson(response, type)

                    faculty = interestsResponse.find { it.type == "faculty" }
                    ethicity = interestsResponse.find { it.type == "ethnicity" }
                    religion = interestsResponse.find { it.type == "religion" }
                    program = interestsResponse.find { it.type == "program" }
                    hobby = interestsResponse.filter { it.type == "hobby" }

                    facultyID = faculty!!.id
                    ethicityID = ethicity!!.id
                    religionID = religion!!.id
                    programID = program!!.id
                    hobbyID = interestsResponse.filter { it.type == "hobby" }.map { it.id }


                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun editInterests(facultyInput: Int, ethnicityInput: Int, religionInput: Int, programInput: Int, hobbiesInput: List<Int>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val gson = Gson()
                val requestBody = mapOf(
                    "faculty" to facultyInput,
                    "ethnicity" to ethnicityInput,
                    "religion" to religionInput,
                    "program" to programInput,
                    "hobbies" to hobbiesInput
                )
                val requestBodyString = gson.toJson(requestBody)

                val url = URL(BuildConfig.GET_INTERESTS)
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "PUT"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty(
                        "Authorization", "Bearer " + userRepository.currentUser.value?.userId.toString()
                    )

                    OutputStreamWriter(outputStream).use { it.write(requestBodyString) }
                    val responseCode = responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        fillAllfields.value = "Success! Interests are saved."
                        getUserInterests()
                    } else {
                        if ((facultyInput == 0) ||  (ethnicityInput == 0) || (religionInput == 0) || (hobbiesInput.isEmpty()) || (programInput == 0)) {
                            fillAllfields.value = "Please fill all fields"
                        } else {
                            fillAllfields.value = "Error, could not save interests."
                            println("Failed to add to interests: $responseCode")
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}