package com.example.clubwat.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class EventResponse(
    val data: List<EventWrapper>
)

data class EventWrapper(
    val event: Event,
    val type: Int? = null
)

data class Event(
    val id: String,
    val title: String,
    val description: String,
    @SerializedName("start_date")
    val startDate: String,
    @SerializedName("end_date")
    var endDate: String,
    @SerializedName("club_id")
    var clubId: Int,
    var location: String,
    @SerializedName("private_flag")
    var privateFlag: Boolean,
    var isAttending: Boolean = false,
    var isBookmarked: Boolean = false
)

enum class EventType(val value: Int) {
    ATTEND(0),
    BOOKMARK(1),
    JOINED(2);
}

