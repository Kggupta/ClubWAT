package com.example.clubwat.model

import com.google.gson.annotations.SerializedName

data class Club(
    val id: String,
    val title: String,
    val description: String,
    val membershipFee: Double = 0.0,
    var isApproved: Boolean = false,
    @SerializedName(" common_interest_count")
    val commonInterestCount: Int = 0
)
