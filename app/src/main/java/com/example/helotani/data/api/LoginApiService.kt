package com.example.helotani.data.api

import retrofit2.http.Body
import retrofit2.http.POST

// Data class untuk request login
data class LoginRequest(
    val email: String,
    val password: String
)

// Data class untuk response login
data class LoginResponse(
    val message: String,
    val user: User
)

// Data class untuk user detail
data class User(
    val id: Int,
    val email: String,
    val password: String,
    val nama_user: String,
    val created_at: String,
    val updated_at: String
)

interface LoginApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
}
