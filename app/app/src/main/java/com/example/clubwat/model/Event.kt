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
    val startDate: Date,
    @SerializedName("end_date")
    var endDate: Date,
    @SerializedName("club_id")
    var clubId: Int
)

enum class EventType(val value: Int) {
    ATTEND(0),
    BOOKMARK(1),
    JOINED(2);
}

