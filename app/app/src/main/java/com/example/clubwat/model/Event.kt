package com.example.clubwat.model

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
    val startDate: Date,
    var endDate: Date,
    var clubId: Int
)
