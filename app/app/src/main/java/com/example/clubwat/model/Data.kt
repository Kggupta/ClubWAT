package com.example.clubwat.model

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("club_id")
    val clubId: Int,
    @SerializedName("create_date")
    val createDate: String,
    val id: Int,
    val message: String,
    val user: UserX,
    @SerializedName("user_id")
    val userId: Int
)

data class ProcessedData(
    val isMe: Boolean = false,
    val messageData: Data
)