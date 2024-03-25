package com.example.clubwat.repository

import com.example.clubwat.BuildConfig
import com.example.clubwat.model.ClubDetails
import com.example.clubwat.model.ClubDiscussion
import com.example.clubwat.model.DeleteDiscussionMessageRequest
import com.example.clubwat.model.NetworkResult
import com.example.clubwat.model.SendDiscussionMessageRequest
import com.google.gson.Gson
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

interface DiscussionRepository {
    suspend fun getClub(clubId: String, userId: String): NetworkResult<ClubDetails>

    suspend fun getMessages(clubId: String, userId: String): NetworkResult<ClubDiscussion>

    suspend fun sendMessage(
        request: SendDiscussionMessageRequest,
        bearer: String
    ): NetworkResult<Any>

    suspend fun deleteMessage(
        request: DeleteDiscussionMessageRequest,
        bearer: String
    ): NetworkResult<Any>
}

class DiscussionRepositoryImpl : DiscussionRepository {
    override suspend fun getClub(clubId: String, userId: String): NetworkResult<ClubDetails> {
        try {
            val obj = URL(BuildConfig.GET_CLUB_URL + clubId)
            val con = obj.openConnection() as HttpURLConnection
            con.requestMethod = "GET"
            con.setRequestProperty("Authorization", "Bearer $userId")
            val responseCode = con.responseCode
            println("Response Code :: $responseCode")
            return if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = con.inputStream.bufferedReader().use { it.readText() }
                NetworkResult.Success(
                    Gson().fromJson(response, ClubDetails::class.java)
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

    override suspend fun getMessages(
        clubId: String,
        userId: String
    ): NetworkResult<ClubDiscussion> {
        try {
            val obj = URL(BuildConfig.CLUB_DISCUSSION_URL + clubId)
            val con = obj.openConnection() as HttpURLConnection
            con.requestMethod = "GET"
            con.setRequestProperty("Authorization", "Bearer $userId")
            val responseCode = con.responseCode
            println("Response Code :: $responseCode")
            return if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = con.inputStream.bufferedReader().use { it.readText() }
                NetworkResult.Success(
                    Gson().fromJson(response, ClubDiscussion::class.java)
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

    override suspend fun sendMessage(
        request: SendDiscussionMessageRequest,
        bearer: String
    ): NetworkResult<Any> {
        try {
            val obj = URL(BuildConfig.CLUB_DISCUSSION_URL + request.club_id)
            val con = obj.openConnection() as HttpURLConnection
            con.doOutput = true
            con.doInput = true
            con.requestMethod = "POST"
            con.setRequestProperty("Authorization", "Bearer $bearer")
            con.setRequestProperty("Content-Type", "application/json")

            val jsonObject = JSONObject()
            jsonObject.put("club_id", request.club_id)
            jsonObject.put("message", request.message)
            val jsonObjectString = jsonObject.toString()

            //Send the JSON we created
            val outputStreamWriter = OutputStreamWriter(con.outputStream)
            outputStreamWriter.write(jsonObjectString)
            outputStreamWriter.flush()

            val responseCode = con.responseCode
            println("Response Code :: $responseCode")
            return if (responseCode == HttpURLConnection.HTTP_OK) {
                NetworkResult.Success(true)
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

    override suspend fun deleteMessage(
        request: DeleteDiscussionMessageRequest,
        bearer: String
    ): NetworkResult<Any> {
        try {
            val obj =
                URL(BuildConfig.CLUB_DISCUSSION_URL + request.club_id + "/" + request.message_id)
            val con = obj.openConnection() as HttpURLConnection
            con.requestMethod = "DELETE"
            con.setRequestProperty("Authorization", "Bearer $bearer")

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