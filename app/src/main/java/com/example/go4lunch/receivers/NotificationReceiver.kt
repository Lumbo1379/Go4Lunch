package com.example.go4lunch.receivers

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.go4lunch.R
import com.example.go4lunch.controllers.MainActivity
import com.example.go4lunch.controllers.NotificationsActivity
import com.example.go4lunch.helpers.UserHelper
import com.example.go4lunch.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.lang.Exception
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NotificationReceiver : BroadcastReceiver() {

    private lateinit var mContext: Context

    override fun onReceive(context: Context?, intent: Intent?) {
        mContext = context!!

        if (intent!!.action == "android.intent.action.BOOT_COMPLETED") { // If phone has just booted
            // If phone has just booted
            val preferences = context!!.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

            if (preferences.getBoolean(NotificationsActivity.PREF_KEY_NOTIFICATIONS_ENABLED, false)) { // Reset alarm if one was set
                NotificationsActivity.setAlarm(preferences, context)
            }
        } else {
            checkForLunch()
        }
    }

    private fun checkForLunch() {
        UserHelper.getUser(getCurrentUser().uid).addOnSuccessListener { documentSnapshot ->
            val modelCurrentUser = documentSnapshot.toObject(User::class.java)!!

            if (checkIfToday(modelCurrentUser.lunchUpdateDate)) {
                UserHelper.getUsers(modelCurrentUser.restaurantId).get().addOnSuccessListener { documentSnapshot ->
                    val workmates = documentSnapshot.toObjects(User::class.java)

                    var workmatesGoing = ""

                    for (workmate in workmates) {
                        if (checkIfToday(workmate.lunchUpdateDate)) {
                            if (workmatesGoing == "") {
                                workmatesGoing += " " + workmate.displayName
                            } else {
                                workmatesGoing += ", " + workmate.displayName
                            }
                        }
                    }

                    sendLunchNotification(modelCurrentUser.restaurantName, modelCurrentUser.restaurantAddress, workmatesGoing) // Send notification with where, street and with who
                }
            }
        }
    }

    private fun sendLunchNotification(name: String, address: String, workmates: String) {
        val builder = NotificationCompat.Builder(mContext, NotificationsActivity.CHANNEL_ID)
            .setSmallIcon(R.drawable.map_restaurant_pin)
            .setContentTitle("Today's Lunch")
            .setContentText("You're having lunch today!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(NotificationCompat.BigTextStyle().bigText("You're going to lunch at $name $address with$workmates"))
            .setAutoCancel(true) // Remove notification when tapped


        val notificationManager = NotificationManagerCompat.from(mContext)

        notificationManager.notify(1, builder.build())
    }

    private fun checkIfToday(date: String): Boolean {
        return try {
            val lunchDate = LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            val todayDate = LocalDateTime.now()

            lunchDate.dayOfWeek == todayDate.dayOfWeek && lunchDate.dayOfMonth == todayDate.dayOfMonth && lunchDate.year == todayDate.year
        } catch(e: Exception) {
            false
        }
    }

    private fun getCurrentUser(): FirebaseUser {
        return FirebaseAuth.getInstance().currentUser!!
    }
}