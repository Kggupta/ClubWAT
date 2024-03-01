package com.example.clubwat.model

data class Data(
    val club_id: Int,
    val create_date: String,
    val id: Int,
    val message: String,
    val user: UserX,
    val user_id: Int
)

data class ProcessedData(
    val isMe: Boolean = false,
    val messageData: Data
)