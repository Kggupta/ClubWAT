package com.example.clubwat.model

import androidx.compose.runtime.MutableState

data class ClubDetails(
    val id: Int,
    val title: String,
    val description: String,
    val members: List<ClubMember>,
    val adminIds: List<Int>,
    val categories: List<Category>,
    val membershipFee: Float,
    var isJoined: Boolean,
    var isJoinPending: Boolean
)

data class Category(
    val id: Int,
    val type: String,
    val name: String
)
data class ClubMember(
    val userId: Int,
    val isApproved: Boolean
)