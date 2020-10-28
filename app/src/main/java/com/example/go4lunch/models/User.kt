package com.example.go4lunch.models

data class User (
    val uid: String,
    val displayName: String,
    val urlPicture: String
) {
    constructor() : this("", "", "")
}