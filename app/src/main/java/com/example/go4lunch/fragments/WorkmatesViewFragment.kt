package com.example.go4lunch.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.go4lunch.R
import com.example.go4lunch.adapters.ChatAdapter
import com.example.go4lunch.adapters.RestaurantRecyclerViewAdapter
import com.example.go4lunch.adapters.WorkmatesAdapter
import com.example.go4lunch.controllers.ChatActivity
import com.example.go4lunch.controllers.MainActivity
import com.example.go4lunch.helpers.MessageHelper
import com.example.go4lunch.helpers.UserHelper
import com.example.go4lunch.models.Message
import com.example.go4lunch.models.User
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.fragment_list_view.*
import kotlinx.android.synthetic.main.fragment_workmates_view.*

class WorkmatesViewFragment : Fragment(), WorkmatesAdapter.IListener {

    private lateinit var workmatesAdapter: WorkmatesAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_workmates_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureRecyclerView()
    }

    private fun configureRecyclerView() {
        //Configure Adapter & RecyclerView
        workmatesAdapter = WorkmatesAdapter(generateOptionsForAdapter(UserHelper.getUsers()), Glide.with(this), this)

        fragment_workmates_recycler_view_workmates.layoutManager = LinearLayoutManager(context)
        fragment_workmates_recycler_view_workmates.adapter = workmatesAdapter
    }

    private fun generateOptionsForAdapter(query: Query): FirestoreRecyclerOptions<User> {
        return FirestoreRecyclerOptions.Builder<User>()
            .setQuery(query, User::class.java)
            .setLifecycleOwner(this)
            .build()
    }

    override fun onDataChanged() {

    }
}