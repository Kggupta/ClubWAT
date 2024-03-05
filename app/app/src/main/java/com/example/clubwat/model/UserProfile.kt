package com.example.clubwat.model

import com.google.gson.annotations.SerializedName

data class UserProfile(
    @SerializedName("admin_flag")
    val adminFlag: Boolean,
    val email: String,
    @SerializedName("first_name")
    val firstName: String,
    val id: Int,
    @SerializedName("last_name")
    val lastName: String,
    val password: String
)