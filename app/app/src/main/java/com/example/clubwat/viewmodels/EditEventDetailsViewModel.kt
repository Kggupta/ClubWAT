package com.example.clubwat.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clubwat.BuildConfig
import com.example.clubwat.model.Event
import com.example.clubwat.repository.UserRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class EditEventDetailsViewModel(private val userRepository: UserRepository): ViewModel() {
    private var _event = MutableStateFlow<Event?>(null)
    var event = _event.asStateFlow()

    var title = mutableStateOf("")
    var description = mutableStateOf("")
    var location = mutableStateOf("")
    var startDate = mutableStateOf(Calendar.getInstance())
    var endDate = mutableStateOf(Calendar.getInstance())

    fun getEventTitle(): String {
        if (_event.value == null) return ""
        return _event.value!!.title
    }

    fun getEventDescription(): String {
        if (_event.value == null) return ""
        return _event.value!!.description
    }

    fun getEventLocation(): String {
        if (_event.value == null) return ""
        return _event.value!!.location
    }

    fun getEvent(eventId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val obj = URL(BuildConfig.GET_EVENT_URL + eventId + "/details")
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                con.setRequestProperty("Authorization", "Bearer " + userRepository.currentUser.value?.userId.toString())
                val responseCode = con.responseCode
                println("Response Code :: $responseCode")
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = con.inputStream.bufferedReader().use { it.readText() }
                    _event.value = Gson().fromJson(response, Event::class.java)
                    withContext(Dispatchers.Main) {
                        _event.value?.let { event ->
                            startDate.value = parseDateString(event.startDate)
                            endDate.value = parseDateString(event.endDate)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun parseDateString(dateString: String): Calendar {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        val calendar = Calendar.getInstance()
        calendar.time = formatter.parse(dateString)!!
        return calendar
    }

    private fun formatDateTime(calendar: Calendar): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        return formatter.format(calendar.time)
    }

    fun updateEvent(callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            var isEventAdded = false
            try {
                val url = URL(String.format(BuildConfig.EDIT_EVENT_URL, _event.value?.clubId, _event.value?.id))
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "PUT"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Authorization", "Bearer " + userRepository.currentUser.value!!.userId )

                    val jsonObject = JSONObject().apply {
                        put("title", title.takeIf { it.value.isNotBlank() } ?: getEventTitle())
                        put("description", description.takeIf { it.value.isNotBlank() } ?: getEventDescription())
                        put("start_date", formatDateTime(startDate.value))
                        put("end_date", formatDateTime(endDate.value))
                        put("location", location.takeIf { it.value.isNotBlank() } ?: getEventLocation())
                    }
                    OutputStreamWriter(outputStream).use { it.write(jsonObject.toString()) }

                    val responseCode = responseCode
                    isEventAdded = responseCode == HttpURLConnection.HTTP_CREATED
                    if (!isEventAdded) {
                        throw Exception()
                    }
                }
            } catch (e: Exception) {
//                errorMessage.value = "Internal server error!"
                e.printStackTrace()
            }
            withContext(Dispatchers.Main) {
//                if (isEventAdded) {
//                    resetForm()
//                }
                callback(isEventAdded)
            }
        }
    }
}
