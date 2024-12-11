package com.example.helotani.data.api

import retrofit2.Response
import retrofit2.http.GET

interface DescriptionApiService {
    @GET(".json")
    suspend fun getDiseaseDesc():Response<List<DescriptionResponse>>
}