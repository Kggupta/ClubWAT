import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clubwat.BuildConfig
import com.example.clubwat.model.Club
import com.example.clubwat.model.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class SearchViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _clubs: MutableStateFlow<MutableList<Club>> = MutableStateFlow(arrayListOf())
    val clubs = _clubs.asStateFlow()

    fun onSearchQueryChanged(search: String) {
        _searchQuery.value = search
        searchClubs(search)
    }

    fun onIsSearchingChanged(isSearching: Boolean) {
        _isSearching.value = isSearching
    }

    private fun searchClubs(search: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                var query = BuildConfig.SEARCH_CLUB_URL
                if (search.isNotEmpty()) {
                    query += "?searchQuery=$search"
                }
                val obj = URL(query)
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                con.setRequestProperty("Authorization", "Bearer " + userRepository.currentUser.value?.userId.toString())
                val responseCode = con.responseCode
                println("Response Code :: $responseCode")
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = con.inputStream.bufferedReader().use { it.readText() }
                    val jsonResponse = JSONArray(response)
                    val clubs = emptyList<Club>().toMutableList()

                    for (i in 0 until jsonResponse.length()) {
                        val jsonObject: JSONObject = jsonResponse.get(i) as JSONObject
                        val id = jsonObject.optString("id", null.toString())
                        val title = jsonObject.optString("title", "No Name")
                        val description = jsonObject.optString("description", "None")

                        clubs.add(Club(id, title, description))
                    }

                    _clubs.value = clubs
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
