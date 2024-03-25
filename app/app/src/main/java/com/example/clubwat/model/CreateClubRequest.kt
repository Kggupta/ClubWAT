package com.example.clubwat.model

data class CreateClubRequest(
    val title: String,
    val description: String,
    val membershipFee: Float,
    val categories: Set<Int>,
)