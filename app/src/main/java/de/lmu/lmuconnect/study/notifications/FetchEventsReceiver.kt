package de.lmu.lmuconnect.study.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

private const val TAG = "FetchEventsReceiver"

class FetchEventsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        NotificationHandler.fetchEventsToday(context)
    }
}