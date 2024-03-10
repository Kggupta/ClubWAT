package com.example.clubwat.model

data class ClubMemberList(
    val isICreator: Boolean,
    val isIAdmin: Boolean,
    val memberList: List<ClubMemberData>
)

data class ClubMemberData(
    val userId: Int,
    var isApproved: Boolean,
    var isClubAdmin: Boolean,
    val isClubCreator: Boolean,
    val firstName: String,
    val lastName: String,
    val email: String
)