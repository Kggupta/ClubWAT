import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clubwat.model.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {
    var email = mutableStateOf("")
    var password = mutableStateOf("")
    var allValuesError = mutableStateOf<String?>(null)

    fun areAllValuesFilled(email: String, password:String) {
        if (email != "" && password != "") {
            allValuesError.value = null
        } else {
            allValuesError.value = "Please fill in all values."
        }
    }

    fun login(callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            var loggedIn = false
            try {
                val url = URL("http://10.0.2.2:3000/api/v1/user/login")
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")

                    val body = """{"email": "${email.value}", "password": "${password.value}"}"""
                    OutputStreamWriter(outputStream).use { it.write(body) }

                    val responseCode = responseCode
                    loggedIn = responseCode == HttpURLConnection.HTTP_OK
                    if (loggedIn) {
                        val response = inputStream.bufferedReader().use { it.readText() }
                        println("Response: $response")
                    } else {
                        val response = errorStream.bufferedReader().use { it.readText() }
                        println("Error Response: $response")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            withContext(Dispatchers.Main) {
                callback(loggedIn)
            }
        }
    }
}
