package com.example.clubwat.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clubwat.BuildConfig
import com.example.clubwat.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class AddEventViewModel(private val userRepository: UserRepository) : ViewModel() {
    var title = mutableStateOf("")
    var description = mutableStateOf("")
    var errorMessage = mutableStateOf("")
    var startDateTime = mutableStateOf(Calendar.getInstance())
    var endDateTime = mutableStateOf(Calendar.getInstance())
    var location = mutableStateOf("Remote")
    var isPrivate = mutableStateOf(false)

    private fun formatDateTime(calendar: Calendar): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        return formatter.format(calendar.time)
    }

    private fun checkIsFormValid(): Pair<Boolean, String> {
        if (title.value.isBlank() || description.value.isBlank()) {
            return Pair(false, "Input is missing!")
        } else if (startDateTime.value >= endDateTime.value) {
            return Pair(false, "Start date must be earlier than end date!")
        }
        return Pair(true, "")
    }

    private fun resetForm() {
        title.value = ""
        description.value = ""
        errorMessage.value = ""
        location.value = "Remote"
        isPrivate.value = false
        startDateTime.value = Calendar.getInstance()
        endDateTime.value = Calendar.getInstance()
    }

    fun addEvent(clubId: String?, callback: (Boolean) -> Unit) {
        val (isValid, errorMsg) = this.checkIsFormValid()
        if (!isValid) {
            errorMessage.value = errorMsg
            callback(false)
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            var isEventAdded = false
            try {
                val url = URL(String.format(BuildConfig.ADD_EVENT_URL, clubId))
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Authorization", "Bearer " + userRepository.currentUser.value!!.userId )

                    val body = ("""{"title": "${title.value.trim()}",""" +
                            """ "description": "${description.value.trim()}",""" +
                            """ "location": "${location.value}",""" +
                            """ "private_flag": ${isPrivate.value},""" +
                            """ "start_date": "${formatDateTime(startDateTime.value)}",""" +
                            """ "end_date": "${formatDateTime(endDateTime.value)}"}""").trimMargin()
                    OutputStreamWriter(outputStream).use { it.write(body) }

                    val responseCode = responseCode
                    isEventAdded = responseCode == HttpURLConnection.HTTP_CREATED
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