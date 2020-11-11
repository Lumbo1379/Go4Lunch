package com.example.go4lunch.helpers

import com.example.go4lunch.models.Message
import com.example.go4lunch.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query

class MessageHelper {
    companion object {

        // --Get--

        fun getAllMessagesForChat(receiverId: String, senderId: String): Query {
            return if (isChatOwner(receiverId, senderId)) {
                ChatHelper.getChatCollection()
                    .document(receiverId)
                    .collection(senderId)
                    .orderBy("dateCreated")
                    .limit(50)
            } else {
                ChatHelper.getChatCollection()
                    .document(senderId)
                    .collection(receiverId)
                    .orderBy("dateCreated")
                    .limit(50)
            }
        }

        // --Create--

        fun createMessageForChat(textMessage: String, receiverId: String , userSender: User): Task<DocumentReference> {
            val message = Message(textMessage, userSender)

            return if (isChatOwner(receiverId, userSender.uid)) {
                ChatHelper.getChatCollection()
                    .document(receiverId)
                    .collection(userSender.uid)
                    .add(message)
            } else {
                ChatHelper.getChatCollection()
                    .document(userSender.uid)
                    .collection(receiverId)
                    .add(message)
            }
        }

        private fun isChatOwner(receiverId: String, senderId: String): Boolean { // Determines if the receiver is the owner of the chat or not
            var ids = listOf(receiverId, senderId)
            ids = ids.sorted()

            return receiverId == ids[0]
        }
    }
}