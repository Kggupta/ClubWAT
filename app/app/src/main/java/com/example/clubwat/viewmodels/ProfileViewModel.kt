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
import com.example.clubwat.model.UserInterests
import com.example.clubwat.repository.UserRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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

    var faculty by mutableStateOf<Interest?>(null)
    var ethicity by mutableStateOf<Interest?>(null)
    var religion by mutableStateOf<Interest?>(null)
    var program by mutableStateOf<Interest?>(null)
    var hobby by mutableStateOf<Interest?>(null)

    var faculties by  mutableStateOf<List<Interest>?>(null)
    var ethnicities by  mutableStateOf<List<Interest>?>(null)
    var religions by  mutableStateOf<List<Interest>?>(null)
    var programs by  mutableStateOf<List<Interest>?>(null)
    var hobbies by mutableStateOf<List<Interest>?>(null)



    init {
        getID(userRepository.currentUser.value?.userId.toString())
        getInterests()
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

    private var oldPasswordStored = userRepository.currentUser.value?.password



    // friends and friendRequest list

    // this is an example of how it will look
    val friends1 = listOf(
        User(
            userId = "1",
            firstName = mutableStateOf("John"),
            lastName = mutableStateOf("Doe"),
            email = mutableStateOf("john.doe@example.com"),
            password = mutableStateOf("password123")
        ),
        User(
            userId = "2",
            firstName = mutableStateOf("Jane"),
            lastName = mutableStateOf("Doe"),
            email = mutableStateOf("jane.doe@example.com"),
            password = mutableStateOf("password456")
        )
        // Add more users as needed
    )


    // send to api as: (id, name, type)
    //    where 'type' would be the categories you've got already (program, hobbies, ethnicity, religion).

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
            println("GET USER INTERESTS")
            try {
                val obj = URL(BuildConfig.GET_INTERESTS)
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                con.setRequestProperty("Authorization", "Bearer " + userRepository.currentUser.value?.userId.toString())
                val responseCode = con.responseCode
                println("GET INTERESTS Response Code :: $responseCode")
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = con.inputStream.bufferedReader().use { it.readText() }
                    println(response)
                    val gson = Gson()
                    val type = object : TypeToken<List<Interest>>() {}.type
                    val interestsResponse: List<Interest> = gson.fromJson(response, type)

                    // val interestsResponse = Gson().fromJson(response, UserInterests::class.java)
                    println("IN HEREEE 1111")


                    faculty = interestsResponse.find { it.type == "faculty" }
                    ethicity = interestsResponse.find { it.type == "ethnicity" }
                    religion = interestsResponse.find { it.type == "religion" }
                    program = interestsResponse.find { it.type == "program" }
                    hobby = interestsResponse.find { it.type == "hobby" }
                    println("IN HEREEE")
                    println(faculty!!.id)


                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun editInterests(facultyInput: Int, ethnicityInput: Int, religionInput: Int, programInput: Int, hobbiesInput: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val gson = Gson()
                val requestBody = mapOf(
                    "faculty" to facultyInput,
                    "ethnicity" to ethnicityInput,
                    "religion" to religionInput,
                    "program" to programInput,
                    "hobbies" to listOf(hobbiesInput)
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
                    println("EDIT INTERESTS Response Code: $responseCode")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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
                val url = URL("http://10.0.2.2:3000/api/v1/user/change-password")
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
                        println("Password successfully updated")
                    } else {
                        println("Failed to update password: $responseCode")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    }

    // FRIENDS PROFILE FUNCTIONS
    fun getFriends() {
        viewModelScope.launch(Dispatchers.IO) {
            println("GET FRIENDS")
            try {
                println(userRepository.currentUser.value?.userId.toString())
                val obj = URL(BuildConfig.GET_FRIEND_URL + "?id=" + "$userId")
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
                val obj = URL(BuildConfig.GET_FRIEND_URL + "?requests?id=" + "$userId")
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
            println("INSIDE ADD FRIEND")
            println(email)
            println("IDDDD")
            println(userId)
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
                    println(responseCode)
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
                val obj = URL(BuildConfig.GET_FRIEND_URL + "/$id")
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
        viewModelScope.launch(Dispatchers.IO) {
            println("ACCEPT FRIEND")
            println(userId)
            println(id)
            try {
                val url = URL(BuildConfig.GET_NOTIFICATIONS_URL + "approve-request")
                println(url)
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "PUT"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Authorization", "Bearer " + userRepository.currentUser.value?.userId.toString())

                    val body = """
                    {
                        "friend_id": $id
                    }
                """.trimIndent()

                    OutputStreamWriter(outputStream).use { it.write(body) }
                    val responseCode = responseCode
                    println("ACCPET FRIEND Response: $responseCode")
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        _reqFriends.value = _reqFriends.value.filter { it.id != id.toInt() }.toMutableList()
                        println("doneee")
                    }
                }

                // getFriends()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    // LOGOUT
    fun logout() {
        userRepository.resetUser()
    }

}