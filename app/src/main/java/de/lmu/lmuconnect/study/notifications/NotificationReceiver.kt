package de.lmu.lmuconnect.study.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import de.lmu.lmuconnect.R

const val notificationId = 1
const val channelId = "event_channel"
const val titleExtra = ""
const val messageExtra = ""

class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val notification = NotificationCompat.Builder(context!!, channelId)
            .setSmallIcon(R.drawable.logo_small)
            .setContentTitle(intent?.getStringExtra(titleExtra))
            .setContentText(intent?.getStringExtra(messageExtra))
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, notification)
    }
}