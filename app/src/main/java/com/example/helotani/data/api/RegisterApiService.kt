package com.example.helotani.data.api

import retrofit2.http.Body
import retrofit2.http.POST

data class RegisterRequest(
    val email:String,
    val password:String,
    val nama_user:String
)

data class RegisterResponse(
    val id: Int,
    val email: String,
    val password: String,
    val nama_user: String,
    val created_at: String,
    val updated_at: String
)

interface RegisterApiService{
    @POST("users")
    suspend fun register(@Body request: RegisterRequest):RegisterResponse
}