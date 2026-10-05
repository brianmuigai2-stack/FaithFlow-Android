package com.example.faithflow_bible.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.faithflow_bible.MainActivity
import com.example.faithflow_bible.R
import com.example.faithflow_bible.data.BibleRepository
import com.example.faithflow_bible.data.SampleVerses

class VerseBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            scheduleHourlyNotification(context)
        }
    }

    fun scheduleHourlyNotification(context: Context) {
        // Schedule the first notification at the next hour boundary, then repeat hourly
        // Using exact alarms for precise hourly timing
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
        val notificationIntent = Intent(context, VerseNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val now = System.currentTimeMillis()
        val nextHour = ((now / 3600000) + 1) * 3600000 // Next hour boundary

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    android.app.AlarmManager.RTC_WAKEUP,
                    nextHour,
                    pendingIntent
                )
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                android.app.AlarmManager.RTC_WAKEUP,
                nextHour,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                android.app.AlarmManager.RTC_WAKEUP,
                nextHour,
                pendingIntent
            )
        }
    }
}

class VerseNotificationReceiver : BroadcastReceiver() {

    private val CHANNEL_ID = "verse_of_the_hour"

    override fun onReceive(context: Context, intent: Intent) {
        // Show the notification now
        showVerseNotification(context)

        // Schedule the next one
        VerseBootReceiver().scheduleHourlyNotification(context)
    }

    private fun showVerseNotification(context: Context) {
        val verse = SampleVerses.verseOfTheHour(context)
        val repo = BibleRepository.get(context)
        val translation = repo.translation

        createNotificationChannel(context)

        val openIntent = Intent(context, MainActivity::class.java)
        openIntent.action = Intent.ACTION_VIEW
        openIntent.putExtra("open_verse", true)
        openIntent.putExtra("book", verse.reference.split(" ")[0].lowercase())
        val pendingIntent = PendingIntent.getActivity(
            context, 0, openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_faithflow_logo)
            .setContentTitle(verse.reference)
            .setContentText("\"${verse.text}\"")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("\"${verse.text}\"")
                    .setSummaryText("— ${verse.reference} (${translation.code}) · ${verse.tag}")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1, notification)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Verse of the Hour",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Hourly Bible verse notifications"
                enableVibration(false)
                setShowBadge(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}