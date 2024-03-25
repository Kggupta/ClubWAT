package com.example.clubwat.model

data class ClubDiscussion(
    val data: List<MessageData>
)

data class ApproveClub(
    val data: List<Club>
)

data class Categories(
    val data: List<Category>
)