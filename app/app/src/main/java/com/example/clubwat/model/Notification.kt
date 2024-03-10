package com.example.clubwat.model

import com.google.gson.annotations.SerializedName

data class Notification(
    val id: Int,
    @SerializedName("destination_user_id")
    val destinationUserId: Int,
    @SerializedName("source_user_id")
    val sourceUserId: Int,
    @SerializedName("club_id")
    val clubId: Int?,
    @SerializedName("event_id")
    val eventId: Int?,
    val content: String,
    @SerializedName("create_date")
    val createDate: String,
    @SerializedName("source_user")
    val sourceUser: UserProfile
)
