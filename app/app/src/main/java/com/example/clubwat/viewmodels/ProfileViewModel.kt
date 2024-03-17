package com.example.clubwat.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auth0.jwt.JWT
import com.example.clubwat.BuildConfig
import com.example.clubwat.model.User
import com.example.clubwat.model.UserProfile
import com.example.clubwat.model.Interest
import com.example.clubwat.model.InterestsResponse
import com.example.clubwat.repository.UserRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {

    var firstName = userRepository.currentUser.value?.firstName
    var lastName = userRepository.currentUser.value?.lastName
    var userId = 0



    var oldPassword = mutableStateOf("")
    var newPassword = mutableStateOf("")
    var addFriend = mutableStateOf("")
    var passwordError = mutableStateOf<String?>(null)
    var passwordSuccess = mutableStateOf<String?>(null)
    var emailError = mutableStateOf<String?>(null)
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
        getID(userRepository.currentUser.value?.userId.toString())
        getInterests()
        getUserInterests()
        getFriendsReq()
        getFriends()
    }

    private fun getID(userToken: String) {
        try {
            val decodedJWT = JWT.decode(userToken)
            decodedJWT?.let { jwt ->
                userId = jwt.getClaim("id").asInt()
            }
        } catch (e: Exception) {
            println(e.printStackTrace())
        }
    }

    var addFriendMessage = mutableStateOf<String?>(null)

    private val _friends: MutableStateFlow<MutableList<UserProfile>> = MutableStateFlow(arrayListOf())
    var friends = _friends.asStateFlow()

    private val _reqFriends: MutableStateFlow<MutableList<UserProfile>> = MutableStateFlow(arrayListOf())
    var req_friends = _reqFriends.asStateFlow()

    // friends and friendRequest list

    // INTERESTS PROFILE FUNCTIONS
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

    fun resetInterests() {
        fillAllfields.value = ""
    }

    // PASSWORD PROFILE FUNCTIONS
    private fun isValidPassword(password: String): Boolean {
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,}$"
        return password.matches(passwordPattern.toRegex())
    }

    fun validatePasswordAndSignUp(password: String): Boolean {
        if (!isValidPassword(password)) {
            passwordError.value = "Please ensure password is 8 characters long, contains one symbol, and has both uppercase and lowercase letters."
            return false
        } else {
            passwordError.value = null
            return true
        }
    }

    fun editPassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = URL(BuildConfig.PASSWORD_CHANGE)
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "PUT"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    // Assuming userRepository.currentUser.value?.token contains the correct authorization token
                    setRequestProperty("Authorization", "Bearer " + userRepository.currentUser.value?.userId.toString())

                    val jsonBody = """{
                    "oldPassword": "$oldPassword",
                    "password": "$newPassword"
                }"""

                    OutputStreamWriter(outputStream).use { it.write(jsonBody) }
                    val responseCode = responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        passwordSuccess.value = "Password Changed!"
                        passwordError.value = null
                        println("Password successfully updated")
                        val response = inputStream.bufferedReader().use { it.readText() }
                        val jsonResponse = JSONObject(response)
                        val token = jsonResponse.optString("data", null.toString())
                        println(token)
                        userRepository.setUserId(token)
                    } else {
                        passwordError.value = "Error password could not be changed"
                        passwordSuccess.value = null
                        println("Failed to update password: $responseCode")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    }

    fun profileReset() {
        passwordSuccess.value = null
        passwordError.value = null
        newPassword.value  = ""
        oldPassword.value = ""
    }

    // FRIENDS PROFILE FUNCTIONS
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
                    val friends: MutableList<UserProfile> = Gson().fromJson(response, object : TypeToken<List<UserProfile>>() {}.type)
                    _friends.value = friends
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

    fun addFriend(email: String) {
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
                            "user": {
                                "id": $userId
                            },
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
                    _friends.value = _friends.value.filter { it.id != id }.toMutableList()
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
                    // Ensure the Authorization token is correct and valid.
                    setRequestProperty("Authorization", "Bearer " + userRepository.currentUser.value?.userId.toString())

                    val jsonBody = """
                {
                    "friend_id": $id
                }
                """.trimIndent()

                    OutputStreamWriter(outputStream).use { it.write(jsonBody) }
                    val responseCode = responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        withContext(Dispatchers.Main) {
                            // Update _reqFriends value on the UI thread
                            _reqFriends.value = _reqFriends.value.filter { it.id != id }.toMutableList()
                            getFriends()

                            println("FRIEND ADDED successfully updated")
                        }
                    } else {
                        println("Failed to add friend: $responseCode")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    fun resetFriends() {
        addFriendMessage.value = null
        addFriend.value = ""
    }

    // LOGOUT
    fun logout() {
        userRepository.resetUser()
    }

}