package com.example.go4lunch.adapters

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.example.go4lunch.R
import com.example.go4lunch.controllers.ChatActivity
import com.example.go4lunch.models.Message
import com.example.go4lunch.models.User
import kotlinx.android.synthetic.main.list_row_chat.view.*
import kotlinx.android.synthetic.main.list_row_workmate.view.*

class WorkmatesRecyclerViewAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun updateWithWorkmate(workmate: User, glide: RequestManager) {

        // Update status TextView
        itemView.list_row_workmate_text_status.text = workmate.displayName

        //Update profile picture ImageView
        glide.load(workmate.urlPicture)
            .apply(RequestOptions.circleCropTransform())
            .into(itemView.list_row_workmate_image_portrait)

        itemView.setOnLongClickListener {
            val intent = Intent(itemView.context, ChatActivity::class.java)
            intent.putExtra("receiverId", workmate.uid)
            itemView.context.startActivity(intent)

            true
        }

        itemView.setOnClickListener {

        }
    }
}