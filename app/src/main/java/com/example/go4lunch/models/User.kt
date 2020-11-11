package com.example.go4lunch.models

data class User (
    val uid: String,
    val displayName: String,
    val urlPicture: String,
    val restaurantId: String,
    val restaurantName: String,
    val lunchUpdateDate: String,
    val restaurantAddress: String
) {
    constructor() : this("", "", "", "", "", "", "")
}