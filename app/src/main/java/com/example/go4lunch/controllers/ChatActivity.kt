package com.example.go4lunch.controllers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.go4lunch.R
import com.example.go4lunch.adapters.ChatAdapter
import com.example.go4lunch.helpers.MessageHelper
import com.example.go4lunch.helpers.UserHelper
import com.example.go4lunch.models.Message
import com.example.go4lunch.models.User
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet_restaurant.*

class ChatActivity : AppCompatActivity(), ChatAdapter.IListener {

    private lateinit var chatAdapter: ChatAdapter
    private lateinit var modelCurrentUser: User
    private lateinit var textViewRecyclerViewEmpty: TextView
    private lateinit var receiverId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val intent = intent
        receiverId = intent.getStringExtra("receiverId").toString()

        configureRecyclerView()
        getCurrentUserFromFirestore()
        setOnClickEvents()
    }

    override fun onDataChanged() { // Hide recycler view when no chates
        activity_chat_text_view_recycler_view_empty.visibility = if (chatAdapter.itemCount == 0) View.VISIBLE else View.GONE
    }

    private fun onClickSendMessage() {
        if (!TextUtils.isEmpty(activity_chat_message_edit_text.text) && modelCurrentUser != null) {
            MessageHelper.createMessageForChat(activity_chat_message_edit_text.text.toString(), receiverId, modelCurrentUser)

            activity_chat_message_edit_text.setText("") // Clear text input
        }
    }

    private fun onClickAddFile() {
        // Not used
    }

    private fun getCurrentUserFromFirestore() {
        UserHelper.getUser(getCurrentUser().uid).addOnSuccessListener { documentSnapshot ->
            modelCurrentUser = documentSnapshot.toObject(User::class.java)!!
        }
    }

    private fun configureRecyclerView() {
        //Configure Adapter & RecyclerView
        chatAdapter = ChatAdapter(generateOptionsForAdapter(MessageHelper.getAllMessagesForChat(receiverId, getCurrentUser().uid)), Glide.with(this), this, getCurrentUser().uid)

        activity_mentor_chat_recycler_view.layoutManager = LinearLayoutManager(this)
        activity_mentor_chat_recycler_view.adapter = chatAdapter
    }

    private fun generateOptionsForAdapter(query: Query): FirestoreRecyclerOptions<Message> {
        return FirestoreRecyclerOptions.Builder<Message>()
            .setQuery(query, Message::class.java)
            .setLifecycleOwner(this)
            .build()
    }

    private fun getCurrentUser(): FirebaseUser {
        return FirebaseAuth.getInstance().currentUser!!
    }

    private fun setOnClickEvents() {
        activity_chat_send_button.setOnClickListener { onClickSendMessage() }
        activity_chat_add_file_button.setOnClickListener { onClickAddFile() }
    }
}