package com.example.go4lunch.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import com.example.go4lunch.R
import com.example.go4lunch.models.Message
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class ChatAdapter : FirestoreRecyclerAdapter<Message, MessageRecyclerViewAdapter> {
    interface IListener {
        fun onDataChanged()
    }

    private val callback: IListener
    private val glide: RequestManager
    private val idCurrentUser: String

    constructor(options: FirestoreRecyclerOptions<Message>, glide: RequestManager, callback: IListener, idCurrentUser: String) : super(options) {
        this.callback = callback
        this.glide = glide
        this.idCurrentUser = idCurrentUser
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageRecyclerViewAdapter {
        return MessageRecyclerViewAdapter(LayoutInflater.from(parent.context).inflate(R.layout.list_row_chat, parent, false))
    }

    override fun onBindViewHolder(holder: MessageRecyclerViewAdapter, position: Int, model: Message) {
        holder.updateWithMessage(model, idCurrentUser, glide)
    }

    override fun onDataChanged() {
        super.onDataChanged()
        callback.onDataChanged()
    }
}