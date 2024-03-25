package com.example.clubwat.repository

import com.example.clubwat.BuildConfig
import com.example.clubwat.model.ApproveClubRequest
import com.example.clubwat.model.Club
import com.example.clubwat.model.NetworkResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.net.HttpURLConnection
import java.net.URL

interface ApproveClubRepository {
    suspend fun getUnapprovedClubs(userId: String): NetworkResult<List<Club>>
    suspend fun updateClubApprovalStatus(request: ApproveClubRequest, userId: String): NetworkResult<Any>
}

class ApproveClubRepositoryImpl : ApproveClubRepository {
    override suspend fun getUnapprovedClubs(userId: String): NetworkResult<List<Club>> {
        try {
            val obj = URL(BuildConfig.GET_UNAPPROVED_CLUBS_URL)
            val con = obj.openConnection() as HttpURLConnection
            con.requestMethod = "GET"
            con.setRequestProperty("Authorization", "Bearer $userId")
            val responseCode = con.responseCode
            println("Response Code :: $responseCode")
            return if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = con.inputStream.bufferedReader().use { it.readText() }
                NetworkResult.Success(
                    Gson().fromJson(response, object: TypeToken<List<Club>>() {}.type)
                )
            } else {
                NetworkResult.Error(
                    message = "API error code: $responseCode",
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return NetworkResult.Error(
            message = "API response error",
        )
    }

    override suspend fun updateClubApprovalStatus(request: ApproveClubRequest, userId: String): NetworkResult<Any> {
        try {
            val obj = URL(BuildConfig.GET_CLUB_URL + request.club_id + "/approval-status/" + request.approval_status)
            val con = obj.openConnection() as HttpURLConnection
            con.doOutput = true
            con.requestMethod = "PUT"
            con.setRequestProperty("Authorization", "Bearer $userId")

            val responseCode = con.responseCode
            return if (responseCode == HttpURLConnection.HTTP_OK) {
                NetworkResult.Success(true)
            } else {
                NetworkResult.Error("API error code: $responseCode")
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return NetworkResult.Error(
            message = "API response error",
        )
    }
}