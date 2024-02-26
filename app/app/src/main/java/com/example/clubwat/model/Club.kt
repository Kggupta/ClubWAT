package com.example.clubwat.model

data class Club(
    val id: String,
    val title: String,
    val description: String,
    val membershipFee: Double = 0.0,
    var isApproved: Boolean = false,
)
