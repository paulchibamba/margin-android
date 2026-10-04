package com.paulchibamba.margin.feature.reminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.paulchibamba.margin.MainActivity
import com.paulchibamba.margin.R
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.domain.tracking.SessionEntry
import com.paulchibamba.margin.domain.usecase.ReviewReminder
import com.paulchibamba.margin.feature.tracking.EntryIntent.withEntry
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ReviewReminderNotifier @Inject constructor(@ApplicationContext private val context: Context) {

    private val notifications = NotificationManagerCompat.from(context)

    fun show(reminder: ReviewReminder) {
        if (!notifications.areNotificationsEnabled()) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        createChannel()
        notifications.notify(NOTIFICATION_ID, notificationFor(reminder))
    }

    private fun createChannel() {
        val channel = NotificationChannel(CHANNEL_ID, "Review reminders", NotificationManager.IMPORTANCE_DEFAULT)
        channel.description = "A daily nudge when reviews are due"
        notifications.createNotificationChannel(channel)
    }

    private fun notificationFor(reminder: ReviewReminder) = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_stat_margin)
        .setColor(MarginColors.Lime.toArgb())
        .setContentTitle(ReviewReminderLabels.title(reminder))
        .setContentText(ReviewReminderLabels.text(reminder))
        .setContentIntent(openFeed())
        .setAutoCancel(true)
        .build()

    private fun openFeed(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            .withEntry(SessionEntry.DUE_NOTIFICATION)
        return PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
    }

    private companion object {
        const val CHANNEL_ID = "review_reminders"
        const val NOTIFICATION_ID = 1
    }
}
