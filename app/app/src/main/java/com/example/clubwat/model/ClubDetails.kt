package com.example.clubwat.model

data class ClubDetails(
    val id: Int,
    val title: String,
    val description: String,
    val members: List<ClubMember>,
    val adminIds: List<Int>,
    val categories: List<Category>,
    val events: List<Event>,
    val membershipFee: Float,
    var isJoined: Boolean,
    var isJoinPending: Boolean,
    var isClubAdmin: Boolean = false,
    var isCreator: Boolean = false,
    var likeCount: Int,
    var isClientLikedClub: Boolean = false
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
