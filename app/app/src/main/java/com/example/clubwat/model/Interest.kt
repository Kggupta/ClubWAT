package com.example.clubwat.model

data class Interest(
    val id: Int,
    val type: String,
    val name: String,
)

data class InterestsResponse(
    val faculties: List<Interest>?,
    val ethnicities: List<Interest>?,
    val religions: List<Interest>?,
    val programs: List<Interest>?,
    val hobbies: List<Interest>?
)


data class UserInterests(
    val faculties: Interest,
    val ethnicities: Interest,
    val religions: Interest,
    val programs: Interest,
    val hobbies: List<Interest>?
)