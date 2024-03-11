package com.example.clubwat.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auth0.jwt.JWT
import com.example.clubwat.BuildConfig
import com.example.clubwat.model.User
import com.example.clubwat.model.UserProfile
import com.example.clubwat.repository.UserRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {

    var firstName = userRepository.currentUser.value?.firstName
    var lastName = userRepository.currentUser.value?.lastName
    var userId = 0
    var faculty = mutableStateOf("")
    var program = mutableStateOf("")
    var hobbies = mutableStateOf<List<String>>(listOf())
    var ethnicity = mutableStateOf("")
    var religion = mutableStateOf("")
    var currentInput = mutableStateOf("")
    var oldPassword = mutableStateOf("")
    var newPassword = mutableStateOf("")
    var addFriend = mutableStateOf("")
    var passwordError = mutableStateOf<String?>(null)
    var passwordSuccess = mutableStateOf<String?>(null)
    var emailError = mutableStateOf<String?>(null)

    init {
        getID(userRepository.currentUser.value?.userId.toString())
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

    var faculties = mutableStateOf<List<String>>(listOf())
    var religions = mutableStateOf<List<String>>(listOf())

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
    fun getFaculties() {
    }

    fun getEthicity() {

    }

    fun getReligion() {

    }

    fun addHobbies() {
        if (currentInput.value.isNotBlank()) {
            hobbies.value = hobbies.value + currentInput.value.trim()
            currentInput.value = ""
        }
        println("ADDED")
        println(faculty.value)
    }

    fun removeHobby(program: String) {
        val index = hobbies.value.indexOf(program)
        if (index != -1) {
            hobbies.value = hobbies.value.toMutableList().apply {
                removeAt(index)
            }
        }
    }

    fun editInterests(facultyInput: String, ethnicityInput: String, religionInput: String) {
        // PUT update
        faculty.value = facultyInput
        ethnicity.value = ethnicityInput
        religion.value = religionInput
        println("faculty input")
        println(faculty.value)
    }

    fun getHobbies() {

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

    fun editPassword() {
        if (oldPasswordStored != oldPassword) {
            passwordError.value += "  The old password is incorrect, unable to make change"
        }
        if (validatePasswordAndSignUp(newPassword.value)) {
            passwordSuccess.value = "Password has been updated"
            // call api to update password
        }


    }

    // FRIENDS PROFILE FUNCTIONS
    fun getFriends() {
        viewModelScope.launch(Dispatchers.IO) {
            println("GET FRIENDS")
            try {
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
                    _friends.value = _friends.value.filter { it.id != id.toInt() }.toMutableList()
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

    fun acceptFriend(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = URL(BuildConfig.GET_NOTIFICATIONS_URL + "club")
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "PUT"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Authorization", "Bearer " + userRepository.currentUser.value?.userId.toString())

                    val body = """{"source_friend_id": "$userId",
                        "destination_friend_id": "$id",
                        "is_accepeted": "${true}"}"""

                    OutputStreamWriter(outputStream).use { it.write(body) }
                    val responseCode = responseCode
                    println("Response: $responseCode")
                }
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