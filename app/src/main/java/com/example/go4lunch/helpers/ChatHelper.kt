package com.example.go4lunch.helpers

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatHelper {

    companion object {

        private val COLLECTION_NAME = "chats"

        // --Collection Reference--

        fun getChatCollection(): CollectionReference {
            return FirebaseFirestore.getInstance().collection(COLLECTION_NAME)
        }
    }
}