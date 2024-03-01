package com.example.clubwat.repository

import com.example.clubwat.BuildConfig
import com.example.clubwat.model.ClubDetails
import com.example.clubwat.model.DiscussionData
import com.example.clubwat.model.NetworkResult
import com.example.clubwat.model.SendDiscusionMessageRequest
import com.google.gson.Gson
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

interface DiscussionRepository {
    suspend fun getClub(clubId: String, userId: String): NetworkResult<ClubDetails>

    suspend fun getPosts(clubId: String, userId: String): NetworkResult<DiscussionData>

    suspend fun sendMessage(request: SendDiscusionMessageRequest, bearer: String): NetworkResult<Any>
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

    override suspend fun getPosts(clubId: String, userId: String): NetworkResult<DiscussionData> {
        try {
            val obj = URL("http://10.0.2.2:3000/api/v1/club/discussion/$clubId")
            val con = obj.openConnection() as HttpURLConnection
            con.requestMethod = "GET"
            con.setRequestProperty("Authorization", "Bearer $userId")
            val responseCode = con.responseCode
            println("Response Code :: $responseCode")
            return if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = con.inputStream.bufferedReader().use { it.readText() }
                NetworkResult.Success(
                    Gson().fromJson(response, DiscussionData::class.java)
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

    override suspend fun sendMessage(request: SendDiscusionMessageRequest, bearer: String): NetworkResult<Any> {
        try {
            val obj = URL("http://10.0.2.2:3000/api/v1/club/discussion/${request.club_id}")
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

}