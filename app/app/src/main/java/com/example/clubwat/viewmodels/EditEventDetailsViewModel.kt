package com.example.clubwat.viewmodels

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
import java.text.ParseException
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
    var errorMessage = mutableStateOf("")

    private fun resetForm() {
        title.value = ""
        description.value = ""
        errorMessage.value = ""
        location.value = ""
        startDate.value = Calendar.getInstance()
        endDate.value = Calendar.getInstance()
    }

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

    fun getEventStartDate(): String {
        if (_event.value == null) return ""
        return _event.value!!.startDate
    }

    fun getEventEndDate(): String {
        if (_event.value == null) return ""
        return _event.value!!.endDate
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
                        _event.value?.startDate?.let {
                            parseDateString(it)?.let { parsedStartDate ->
                                startDate.value = parsedStartDate
                            }
                        }
                        _event.value?.endDate?.let {
                            parseDateString(it)?.let { parsedEndDate ->
                                endDate.value = parsedEndDate
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun parseDateString(dateString: String): Calendar? {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        formatter.timeZone = TimeZone.getDefault()
        val calendar = Calendar.getInstance()
        try {
            calendar.time = formatter.parse(dateString) ?: return null
        } catch (e: ParseException) {
            e.printStackTrace()
            return null
        }
        return calendar
    }


     fun formatDateTime(calendar: Calendar): String {
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
                        put("title", title.value.takeIf { it.isNotBlank() } ?: getEventTitle())
                        put("description", description.value.takeIf { it.isNotBlank() } ?: getEventDescription())
                        put("start_date", formatDateTime(startDate.value))
                        put("end_date", formatDateTime(endDate.value))
                        put("location", location.value.takeIf { it.isNotBlank() } ?: getEventLocation())
                    }
                    println(jsonObject.toString())
                    OutputStreamWriter(outputStream).use { it.write(jsonObject.toString()) }

                    val responseCode = responseCode
                    println(responseCode)
                    isEventAdded = responseCode == HttpURLConnection.HTTP_OK
                    if (!isEventAdded) {
                        throw Exception()
                    }
                }
            } catch (e: Exception) {
                errorMessage.value = "Internal server error!"
                e.printStackTrace()
            }
            withContext(Dispatchers.Main) {
                if (isEventAdded) {
                    resetForm()
                }
                callback(isEventAdded)
            }
        }
    }
}
