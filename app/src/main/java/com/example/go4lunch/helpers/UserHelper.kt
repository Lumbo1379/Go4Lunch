package com.example.go4lunch.helpers

import android.util.Log
import com.example.go4lunch.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*

class UserHelper {

    companion object {
        private val COLLECTION_NAME = "users"

        // --Collection Reference--

        fun getUsersCollection(): CollectionReference {
            return FirebaseFirestore.getInstance().collection(COLLECTION_NAME)
        }

        // --Create--

        fun createUser(uid: String, displayName: String, urlPicture: String): Task<Void> {
            val userToCreate = User(uid, displayName, urlPicture)

            return getUsersCollection()
                .document(uid).set(userToCreate)
        }

        fun createTestUserAccounts() {

            val testUsers = mutableListOf<User>()
            testUsers.add(User("testUser1", "Simon Cowell", "https://cached.imagescaler.hbpl.co.uk/resize/scaleWidth/743/cached.offlinehbpl.hbpl.co.uk/news/OMC/6897A22E-A6A1-4B8F-B0E2E8E37EFA4D3E.jpg"))
            testUsers.add(User("testUser2", "Idris Elba", "https://cdn.britannica.com/41/188641-050-AB88F70B/Idris-Elba-British.jpg"))
            testUsers.add(User("testUser3", "Octavia Spencer", "https://www.biography.com/.image/t_share/MTIwNjA4NjM0MTI4OTkxNzU2/octavia-spencer-20724237-1-402.jpg"))

            testUsers.forEach {
                createUser(it.uid, it.displayName, it.urlPicture).addOnFailureListener {
                    Log.i("User Helper", it.message)
                }
            }
        }

        // --Get--

        fun getUser(uid: String): Task<DocumentSnapshot> {
            return getUsersCollection()
                .document(uid).get()
        }

        fun getUsers(): Query {
            return getUsersCollection()
                .orderBy("displayName")
        }

        // --Update--

        fun updateUsername(username: String, uid:String): Task<Void> {
            return getUsersCollection()
                .document(uid).update("username", username)
        }

        // --Delete--

        fun deleteUser(uid: String): Task<Void> {
            return getUsersCollection()
                .document(uid).delete()
        }
    }
}