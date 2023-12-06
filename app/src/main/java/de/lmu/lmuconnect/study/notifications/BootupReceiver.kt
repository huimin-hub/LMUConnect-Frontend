package de.lmu.lmuconnect.study.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.*


class BootupReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val pendingIntent = PendingIntent.getBroadcast(context, 0, Intent(context, BootupReceiver::class.java), PendingIntent.FLAG_IMMUTABLE)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis,
            1000 * 60 * 60 * 24, // 1 day
            pendingIntent
        )

        NotificationHandler.createNotificationChannel(context)

        NotificationHandler.fetchEventsToday(context)
    }
}