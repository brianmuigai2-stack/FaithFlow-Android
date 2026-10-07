package com.example.faithflow_bible.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.faithflow_bible.MainActivity
import com.example.faithflow_bible.R
import com.example.faithflow_bible.data.BibleRepository
import com.example.faithflow_bible.data.ReaderPrefs
import com.example.faithflow_bible.data.VerseRepository
import java.util.concurrent.TimeUnit

private const val WORK_NAME = "verse_of_the_hour"
private const val CHANNEL_ID = "verse_of_the_hour"
private const val NOTIFICATION_ID = 1001

// ── WorkManager worker ────────────────────────────────────────────────────────

class VerseNotificationWorker(
    private val ctx: Context,
    params: WorkerParameters
) : Worker(ctx, params) {

    override fun doWork(): Result {
        val prefs = ReaderPrefs(ctx)
        if (!prefs.notificationsEnabled) return Result.success()
        showNotification(ctx)
        return Result.success()
    }
}

// ── Schedule / cancel helpers ─────────────────────────────────────────────────

fun scheduleHourlyNotifications(context: Context) {
    val request = PeriodicWorkRequestBuilder<VerseNotificationWorker>(1, TimeUnit.HOURS)
        .build()
    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        WORK_NAME,
        ExistingPeriodicWorkPolicy.KEEP,   // don't reset the timer if already scheduled
        request
    )
}

fun cancelHourlyNotifications(context: Context) {
    WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
}

// ── Boot receiver — re-schedules after device restart ────────────────────────

class VerseBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefs = ReaderPrefs(context)
            if (prefs.notificationsEnabled) {
                scheduleHourlyNotifications(context)
            }
        }
    }

    // Keep these as instance methods so existing call-sites in SettingsViewModel compile
    fun scheduleHourlyNotification(context: Context) = scheduleHourlyNotifications(context)
    fun cancelSchedule(context: Context) = cancelHourlyNotifications(context)
}

// ── Notification display ──────────────────────────────────────────────────────

fun showNotification(context: Context) {
    val verse = VerseRepository(context).getVerseOfTheHour()
    val translation = BibleRepository.get(context).translation

    ensureChannel(context)

    val openIntent = Intent(context, MainActivity::class.java).apply {
        action = Intent.ACTION_MAIN
        addCategory(Intent.CATEGORY_LAUNCHER)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    val pendingIntent = PendingIntent.getActivity(
        context, NOTIFICATION_ID, openIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_faithflow_logo)
        .setContentTitle("\uD83D\uDCD6 ${verse.reference}")
        .setContentText(verse.text)
        .setStyle(
            NotificationCompat.BigTextStyle()
                .bigText("\u201C${verse.text}\u201D")
                .setSummaryText("${verse.reference} (${translation.code})" +
                    if (verse.theme.isNotBlank()) " \u00B7 ${verse.theme}" else "")
        )
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
        .build()

    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    manager.notify(NOTIFICATION_ID, notification)
}

private fun ensureChannel(context: Context) {
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
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(channel)
    }
}
