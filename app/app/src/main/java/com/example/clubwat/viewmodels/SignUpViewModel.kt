import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class SignUpViewModel : ViewModel() {
    var firstName = mutableStateOf("")
    var lastName = mutableStateOf("")
    var email = mutableStateOf("")
    var password = mutableStateOf("")
    var code = mutableStateOf("")
    var passwordError = mutableStateOf<String?>(null)
    var allValuesError = mutableStateOf<String?>(null)

    // Function to handle sign up logic
    fun signUp() {
        viewModelScope.launch {
        }
    }

    fun validatePasswordAndSignUp(password: String) {
        if (!isValidPassword(password)) {
            passwordError.value = "Please ensure password is 8 characters long, contains one symbol, and has both uppercase and lowercase letters."
        } else {
            passwordError.value = null
        }
    }

    fun areAllValuesFilled(firstName: String, lastName: String, email: String, password:String) {
        if (firstName != "" && lastName != "" && email != "" && password != "") {
            allValuesError.value = null
        } else {
            allValuesError.value = "Please fill in all values."
        }
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
        return password.matches(passwordPattern.toRegex())
    }

    fun sendVerificationEmail() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                println(email.value)
                val url = URL("http://10.0.2.2:3000/api/v1/user/registration-email-verification")
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")

                    val body = """{"email": "${email.value}"}"""

                    OutputStreamWriter(outputStream).use { it.write(body) }
                    // Read the response
                    val responseCode = responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Handle the response
                        val response = inputStream.bufferedReader().use { it.readText() }
                        println("Response: $response")
                    } else {
                        // Handle error
                        val response = errorStream.bufferedReader().use { it.readText() }
                        println("Error Response: $response")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun register() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = URL("https://localhost:3000/api/v1/user/register")
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")

                    val requestBody = """
                        {
                            "email": "${email.value}",
                            "password": "${password.value}",
                            "first_name": "${firstName.value}",
                            "last_name": "${lastName.value}",
                            "code": ${code.value}
                        }
                    """.trimIndent()

                    OutputStreamWriter(outputStream).use { it.write(requestBody) }

                    val responseCode = responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Handle the response
                        val response = inputStream.bufferedReader().use { it.readText() }
                        println("Registration Successful: $response")
                    } else {
                        // Handle error
                        val response = errorStream.bufferedReader().use { it.readText() }
                        println("Registration Failed: $response")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}