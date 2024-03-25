package com.example.clubwat.repository

import com.example.clubwat.BuildConfig
import com.example.clubwat.model.Category
import com.example.clubwat.model.CreateClubRequest
import com.example.clubwat.model.NetworkResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

interface CreateClubRepository {
    suspend fun createClub(request: CreateClubRequest, userId: String): NetworkResult<Any>

    suspend fun getCategories(userId: String): NetworkResult<List<Category>>
}

class CreateClubRepositoryImpl : CreateClubRepository {
    override suspend fun createClub(request: CreateClubRequest, userId: String): NetworkResult<Any> {
        try {
            val obj = URL(BuildConfig.GET_CLUB_URL)
            val con = obj.openConnection() as HttpURLConnection
            con.doOutput = true
            con.doInput = true
            con.requestMethod = "POST"
            con.setRequestProperty("Authorization", "Bearer $userId")
            con.setRequestProperty("Content-Type", "application/json")

            val jsonObject = JSONObject()
            jsonObject.put("title", request.title)
            jsonObject.put("description", request.description)
            jsonObject.put("membership_fee", request.membershipFee)
            jsonObject.put("categories", JSONArray(request.categories))
            val jsonObjectString = jsonObject.toString()

            //Send the JSON we created
            val outputStreamWriter = OutputStreamWriter(con.outputStream)
            outputStreamWriter.write(jsonObjectString)
            outputStreamWriter.flush()

            val responseCode = con.responseCode
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
    override suspend fun getCategories(userId: String): NetworkResult<List<Category>> {
        try {
            val obj = URL(BuildConfig.GET_ALL_CATEGORIES)
            val con = obj.openConnection() as HttpURLConnection
            con.requestMethod = "GET"
            con.setRequestProperty("Authorization", "Bearer $userId")
            val responseCode = con.responseCode
            return if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = con.inputStream.bufferedReader().use { it.readText() }
                NetworkResult.Success(
                    Gson().fromJson(response, object : TypeToken<List<Category>>() {}.type)
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
}