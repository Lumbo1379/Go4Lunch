package com.example.go4lunch.adapters

import android.view.View
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.example.go4lunch.R
import com.example.go4lunch.models.Message
import kotlinx.android.synthetic.main.list_row_chat.view.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class MessageRecyclerViewAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun updateWithMessage(message: Message, currentUserId: String, glide: RequestManager) {

        // Check if current user is the sender
        val isCurrentUser = message.userSender.uid == currentUserId

        // Update message TextView
        itemView.activity_chat_item_message_container_text_message_container_text_view.text = message.message
        itemView.activity_chat_item_message_container_text_message_container_text_view.textAlignment = if (isCurrentUser) View.TEXT_ALIGNMENT_TEXT_END else View.TEXT_ALIGNMENT_TEXT_START

        // Update date TextView
        if (message.dateCreated.isNotEmpty()) {
            itemView.activity_chat_item_message_container_text_view_date.text = convertDateToHour(message.dateCreated)
        }

        //Update profile picture ImageView
        glide.load(message.userSender.urlPicture)
            .apply(RequestOptions.circleCropTransform())
            .into(itemView.activity_chat_item_profile_container_profile_image)

        //Update image sent ImageView
        if (message.urlImage.isNotEmpty()) {
            glide.load(message.urlImage)
                .into(itemView.activity_chat_item_message_container_image_sent_cardview_image)

            itemView.activity_chat_item_message_container_image_sent_cardview_image.visibility = View.VISIBLE
        } else {
            itemView.activity_chat_item_message_container_image_sent_cardview_image.visibility = View.GONE
        }

        //Update Message Bubble Color Background
        itemView.activity_chat_item_message_container_text_message_container.setBackgroundColor(if (isCurrentUser) itemView.resources.getColor(R.color.colorAccent) else itemView.resources.getColor(R.color.colorPrimary))

        //Update all views alignment depending is current user or not
        updateDesignDependingUser(isCurrentUser)
    }

    private fun updateDesignDependingUser(isSender: Boolean) {
        // PROFILE CONTAINER
        val paramsLayoutHeader = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )

        paramsLayoutHeader.addRule(if (isSender) RelativeLayout.ALIGN_PARENT_RIGHT else RelativeLayout.ALIGN_PARENT_LEFT)
        itemView.activity_chat_item_profile_container.layoutParams = paramsLayoutHeader

        // MESSAGE CONTAINER
        val paramsLayoutContent = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        paramsLayoutContent.addRule(
            if (isSender) RelativeLayout.LEFT_OF else RelativeLayout.RIGHT_OF,
            R.id.activity_chat_item_profile_container
        )
        itemView.activity_chat_item_message_container.layoutParams = paramsLayoutContent

        // CARDVIEW IMAGE SEND
        val paramsImageView = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        paramsImageView.addRule(
            if (isSender) RelativeLayout.ALIGN_LEFT else RelativeLayout.ALIGN_RIGHT,
            R.id.activity_chat_item_message_container_text_message_container
        )
        itemView.activity_chat_item_message_container_image_sent_cardview.layoutParams = paramsImageView

        itemView.activity_chat_item_root_view.requestLayout()
    }

    private fun convertDateToHour(date: String): String? {
        val d = LocalDate.parse(date, DateTimeFormatter.ISO_DATE)
        val dfTime = SimpleDateFormat("HH:mm")

        return dfTime.format(d)
    }
}