package com.example.go4lunch.utils

import com.example.go4lunch.models.restaurant.PlaceDetails
import com.example.go4lunch.models.restaurant.Places
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface IMapsService {

    @GET("place/nearbysearch/json")
    fun getPlaces(@Query("location") location: String, @Query("radius") radius: Int, @Query("type") type: String, @Query("keyword") keyword: String, @Query("key") key: String): Call<Places>

    @GET("place/details/json")
    fun getPlaceDetails(@Query("place_id") id: String, @Query("fields") fields: String, @Query("key") key: String): Call<PlaceDetails>
}