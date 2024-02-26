import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clubwat.BuildConfig
import com.example.clubwat.model.Club
import com.example.clubwat.model.UserRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL

class HomeViewModel(private val userRepository: UserRepository) : ViewModel() {
    val allClubs = MutableStateFlow<List<Club>>(emptyList())

    fun getAllClubs() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val obj = URL(BuildConfig.GET_ALL_CLUBS_FOR_USER)
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                con.setRequestProperty("Authorization", "Bearer " + userRepository.currentUser.value?.userId.toString())
                val responseCode = con.responseCode
                println("Response Code :: $responseCode")
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = con.inputStream.bufferedReader().use { it.readText() }
                    val clubsList: List<Club> = Gson().fromJson(response, object : TypeToken<List<Club>>() {}.type)
                    val sortedClubsList = clubsList.sortedBy { it.title }
                    allClubs.value = sortedClubsList
                    println(clubsList)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
