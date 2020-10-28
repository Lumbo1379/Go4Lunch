package com.example.go4lunch.models

import java.util.*

data class Message(
    val message: String,
    val userSender: User,
    val urlImage: String,
    val dateCreated: String
) {
    constructor() : this("", User(), "", "")
    constructor(message: String, userSender: User) : this(message, userSender, "", "")
}