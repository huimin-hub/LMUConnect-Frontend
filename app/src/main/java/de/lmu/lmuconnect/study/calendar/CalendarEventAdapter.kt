package de.lmu.lmuconnect.study.calendar

import android.content.Intent
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.LineHeightSpan
import android.text.style.StyleSpan
import android.util.Log
import android.util.TypedValue
import android.widget.Toast
import com.alamkanak.weekview.WeekView
import com.alamkanak.weekview.WeekViewEntity
import de.lmu.lmuconnect.R
import de.lmu.lmuconnect.general.api.ApiClient
import de.lmu.lmuconnect.general.api.SessionManager
import de.lmu.lmuconnect.study.calendar.api.StudyEventsGetResponse
import de.lmu.lmuconnect.study.calendar.data.EventOccurrence
import de.lmu.lmuconnect.study.courses.data.Course
import de.lmu.lmuconnect.study.courses.info.CourseInfoActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

private const val TAG = "CalendarEventAdapter"

class CalendarEventAdapter(val calendarEventHandler: CalendarEventHandler) :
    WeekView.PagingAdapter<EventOccurrence>() {
    override fun onCreateEntity(item: EventOccurrence): WeekViewEntity {
        val color =
            if (item.type == Course.Type.LECTURE) context.getColor(R.color.lmu_green)
            else context.getColor(R.color.lmu_black)

        val textColor =
            if (item.type == Course.Type.LECTURE) context.getColor(R.color.lmu_green_20)
            else context.getColor(R.color.lmu_grayII)

        val style = WeekViewEntity.Style.Builder()
            .setBackgroundColor(color)
            .setTextColor(textColor)
            .build()

        var courseName = item.courseName
        if (item.group != null) courseName += " - ${context.getString(R.string.study_event_group)} ${item.group}"

        val titleSpannable = SpannableString("$courseName\n ")
        titleSpannable.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            titleSpannable.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        titleSpannable.setSpan(
            ForegroundColorSpan(context.getColor(R.color.lmu_grayIV)),
            0,
            titleSpannable.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        titleSpannable.setSpan(
            AbsoluteSizeSpan(spToPx(14f)),
            0,
            titleSpannable.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        titleSpannable.setSpan( // This is the only way I found of adding space after the title
            LineHeightSpan.Standard(spToPx(6f)),
            titleSpannable.length - 1,
            titleSpannable.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return WeekViewEntity.Event.Builder(item)
            .setId((0L..100000000000000L).random())
            .setTitle(titleSpannable)
            .setStartTime(timeFloatAndDateToCalendar(item.startTime, item.date))
            .setEndTime(timeFloatAndDateToCalendar(item.endTime, item.date))
            .setSubtitle(item.location)
            .setStyle(style)
            .build()
    }

    override fun onLoadMore(startDate: Calendar, endDate: Calendar) {
        // TODO: Improve performance: Paging adapter fetches 3 months which is way too much

        calendarEventHandler.onCalendarLoadingChanged(true)
        val startDateStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(startDate.time)
        val endDateStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(endDate.time)
        Log.d(TAG, "Fetching events from $startDateStr until $endDateStr")

        ApiClient.getApiService()
            .studyEventsGet(startDateStr, endDateStr, SessionManager(context).fetchAuthToken())
            .enqueue(object : Callback<StudyEventsGetResponse> {
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

                            submitList(events)
                        }
                        else -> Toast.makeText(
                            context,
                            response.code().toString() + " - " + response.message().uppercase(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    calendarEventHandler.onCalendarLoadingChanged(false)
                }

                override fun onFailure(call: Call<StudyEventsGetResponse>, t: Throwable) {
                    Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                    Log.e(TAG, t.message!!)
                    calendarEventHandler.onCalendarLoadingChanged(false)
                }
            })
    }

    override fun onRangeChanged(firstVisibleDate: Calendar, lastVisibleDate: Calendar) {
        super.onRangeChanged(firstVisibleDate, lastVisibleDate)
        calendarEventHandler.onCalendarRangeChanged(firstVisibleDate, lastVisibleDate)
    }

    override fun onEventClick(data: EventOccurrence) {
        super.onEventClick(data)

        val intent = Intent(context, CourseInfoActivity::class.java)
        intent.putExtra("courseId", data.courseId)
        context.startActivity(intent)
    }

    private fun spToPx(sp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp,
            context.resources.displayMetrics
        ).toInt()
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