package com.example.go4lunch.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import com.example.go4lunch.R
import com.example.go4lunch.models.Message
import com.example.go4lunch.models.User
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class WorkmatesAdapter: FirestoreRecyclerAdapter<User, WorkmatesRecyclerViewAdapter> {
    interface IListener {
        fun onDataChanged()
    }

    private val callback: IListener
    private val glide: RequestManager

    constructor(options: FirestoreRecyclerOptions<User>, glide: RequestManager, callback: IListener) : super(options) {
        this.callback = callback
        this.glide = glide
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkmatesRecyclerViewAdapter {
        return WorkmatesRecyclerViewAdapter(LayoutInflater.from(parent.context).inflate(R.layout.list_row_workmate, parent, false))
    }

    override fun onBindViewHolder(holder: WorkmatesRecyclerViewAdapter, position: Int, model: User) {
        holder.updateWithWorkmate(model, glide)
    }

    override fun onDataChanged() {
        super.onDataChanged()
        callback.onDataChanged()
    }
}