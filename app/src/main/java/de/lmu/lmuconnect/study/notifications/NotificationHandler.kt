package de.lmu.lmuconnect.study.notifications

import android.app.*
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import de.lmu.lmuconnect.general.api.ApiClient
import de.lmu.lmuconnect.general.api.SessionManager
import de.lmu.lmuconnect.study.calendar.api.StudyEventsGetResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.math.roundToInt

private const val TAG = "NotificationHandler"

class NotificationHandler {
    companion object {
        fun scheduleNotification(context: Context, title: String, message: String, time: Long) {
            val intent = Intent(context, NotificationReceiver::class.java)
            intent.putExtra(titleExtra, title)
            intent.putExtra(messageExtra, message)

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
            )

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                time,
                pendingIntent
            )
        }

        fun createNotificationChannel(context: Context) {
            val name = "Notification Channel"
            val desc = "A Description of Channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = desc
            val notificationManager = context.getSystemService(Activity.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        fun fetchEventsToday(context: Context) {
            ApiClient.getApiService().studyEventsTodayGet(
                SessionManager(context).fetchAuthToken()
            ).enqueue(object : Callback<StudyEventsGetResponse> {
                override fun onResponse(
                    call: Call<StudyEventsGetResponse>,
                    response: Response<StudyEventsGetResponse>
                ) {
                    when (response.code()) {
                        200 -> {
                            // OK
                            val events = response.body()?.events
                            if (events == null) {
                                Log.e(TAG, "Response body not existent!")
                                Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show()
                                return
                            }

                            for (event in events) {
                                val time = timeFloatAndDateToCalendar(event.startTime, event.date).apply {
                                    add(Calendar.MINUTE, -15)
                                }.timeInMillis
                                if (time > Calendar.getInstance().timeInMillis)
                                    scheduleNotification(context, event.courseName, "15 min until event", time)
                            }
                        }
                        else -> Toast.makeText(
                            context,
                            response.code().toString() + " - " + response.message().uppercase(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<StudyEventsGetResponse>, t: Throwable) {
                    Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                    Log.e(TAG, t.message!!)
                }
            })
        }

        private fun timeFloatAndDateToCalendar(time: Float, date: Date): Calendar {
            val hours = time.toInt()
            val minutes = ((time - hours) * 60).roundToInt()

            return Calendar.getInstance().apply {
                setTime(date)
                set(Calendar.HOUR_OF_DAY, hours)
                set(Calendar.MINUTE, minutes)
            }
        }
    }
}