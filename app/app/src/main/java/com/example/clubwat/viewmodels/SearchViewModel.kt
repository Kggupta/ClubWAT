import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clubwat.BuildConfig
import com.example.clubwat.model.Club
import com.example.clubwat.model.Event
import com.example.clubwat.model.UserRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.time.Instant
import java.util.Date

class SearchViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _clubs: MutableStateFlow<MutableList<Club>> = MutableStateFlow(arrayListOf())
    val clubs = _clubs.asStateFlow()

    private val _events: MutableStateFlow<MutableList<Event>> = MutableStateFlow(arrayListOf())
    val events = _events.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    fun onSearchQueryChanged(search: String, isClub: Boolean) {
        _searchQuery.value = search
        if (isClub) {
            searchClubs(search)
        } else {
            searchEvents(search)
        }
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
                    val clubs: MutableList<Club> = Gson().fromJson(response, object : TypeToken<List<Club>>() {}.type)

                    _clubs.value = clubs
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun searchEvents(search: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                var query = BuildConfig.SEARCH_EVENT_URL
                if (search.isNotEmpty()) {
                    query += "?searchQuery=$search"
                }
                val obj = URL(query)
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                con.setRequestProperty("Authorization", "Bearer " + userRepository.currentUser.value?.userId.toString())
                val responseCode = con.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = JSONObject(con.inputStream.bufferedReader().use { it.readText() })
                    val jsonResponse = response.getJSONArray("data")
                    val events = emptyList<Event>().toMutableList()

                    for (i in 0 until jsonResponse.length()) {
                        val jsonObject: JSONObject = jsonResponse.get(i) as JSONObject
                        val id = jsonObject.optString("id", null.toString())
                        val title = jsonObject.optString("title", "No Name")
                        val description = jsonObject.optString("description", "None")
                        val clubId = jsonObject.optInt("club_id", 0)
                        val startDate = Date.from(Instant.parse(jsonObject.optString("start_date", "None")))
                        val endDate = Date.from(Instant.parse(jsonObject.optString("end_date", "None")))

                        events.add(Event(id, title, description, startDate, endDate, clubId))
                    }

                    _events.value = events
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
