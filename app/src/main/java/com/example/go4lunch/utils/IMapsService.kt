package com.example.go4lunch.utils

import com.example.go4lunch.models.Places
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface IMapsService {

    @GET("place/nearbysearch/json")
    fun getPlaces(@Query("location") location: String, @Query("radius") radius: Int, @Query("type") type: String, @Query("keyword") keyword: String, @Query("key") key: String): Call<Places>
}