package com.example.go4lunch.controllers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.go4lunch.R
import com.example.go4lunch.receivers.NotificationReceiver
import kotlinx.android.synthetic.main.activity_notifications.*
import java.util.*


class NotificationsActivity : AppCompatActivity() {

    companion object {
        val CHANNEL_ID = "NEW_ARTICLES_CHANNEL"
        val PREF_KEY_NOTIFICATIONS_ENABLED = "PREF_KEY_NOTIFICATIONS_ENABLED"
        val PREF_KEY_SPANISH_ENABLED = "PREF_KEY_SPANISH_ENABLED"
        val PREF_KEY_LANGUAGE = "PREF_KEY_LANGUAGE"

        private lateinit var mPreferences: SharedPreferences
        private lateinit var mContext: Context

        fun setAlarm(preferences: SharedPreferences, context: Context) {
            preferences.edit().putBoolean(PREF_KEY_NOTIFICATIONS_ENABLED, true).apply() // Set notification pref to enabled

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val intent = Intent(context, NotificationReceiver::class.java)
            intent.action = CHANNEL_ID

            val alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT) // Update alarm if one already exists
            val calendar = Calendar.getInstance()

            calendar.timeInMillis = System.currentTimeMillis()
            calendar[Calendar.HOUR_OF_DAY] = 12 // Set time to trigger (noon)
            calendar.add(Calendar.DATE, 1) // Add a day to not immediately send notification if after 8am when set

            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, 1000 * 60 * 60 * 24.toLong(), alarmIntent) // Set repeat frequency (every 24 hrs)
        }

        private fun cancelAlarm() {
            mPreferences.edit().putBoolean(PREF_KEY_NOTIFICATIONS_ENABLED, false).apply()

            val intent = Intent(mContext, NotificationReceiver::class.java)
            intent.action = CHANNEL_ID

            val pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_NO_CREATE)
            val alarmManager = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            if (pendingIntent != null) { // If there is an alarm to cancel
                alarmManager.cancel(pendingIntent)
            }
        }

        fun changeLang(baseContext: Context, langCode: String) { // Swap string file
            val locale = Locale(langCode)
            Locale.setDefault(locale)

            val config = Configuration()
            config.locale = locale

            baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        mPreferences = applicationContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        mContext = applicationContext

        initSwitch()
        loadPreferences()
    }

    private fun initSwitch() {
        activity_notifications_switch_spanish.setOnClickListener {
            if (activity_notifications_switch_spanish.isChecked) {
                mPreferences.edit().putBoolean(PREF_KEY_SPANISH_ENABLED, true).apply()
                mPreferences.edit().putString(PREF_KEY_LANGUAGE, "sp").apply()
                changeLang(baseContext, "sp")
            } else {
                mPreferences.edit().putBoolean(PREF_KEY_SPANISH_ENABLED, false).apply()
                mPreferences.edit().putString(PREF_KEY_LANGUAGE, "en").apply()
                changeLang(baseContext, "")
            }

            finish()
            val intent = Intent(this, NotificationsActivity::class.java)
            startActivity(intent)
        }

        activity_notifications_switch_notifications.setOnClickListener {
            if (activity_notifications_switch_notifications.isChecked) {
                setAlarm(mPreferences, applicationContext)
            } else {
                cancelAlarm()
            }
        }
    }

    private fun loadPreferences() {
        activity_notifications_switch_notifications.isChecked = mPreferences.getBoolean(PREF_KEY_NOTIFICATIONS_ENABLED, false);
        activity_notifications_switch_spanish.isChecked = mPreferences.getBoolean(PREF_KEY_SPANISH_ENABLED, false);
    }
}