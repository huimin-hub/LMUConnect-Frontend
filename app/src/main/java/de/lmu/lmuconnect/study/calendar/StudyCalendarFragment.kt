package de.lmu.lmuconnect.study.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.alamkanak.weekview.WeekView
import de.lmu.lmuconnect.R
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "StudyCalendarFragment"

class StudyCalendarFragment : Fragment(), CalendarEventHandler {

    private lateinit var titleTextView: TextView
    private lateinit var backImgButton: ImageButton
    private lateinit var forwardImgButton: ImageButton
    private lateinit var progressBarLoad: ProgressBar

    private lateinit var nextBackDate: Calendar
    private lateinit var nextForwardDate: Calendar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_study_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titleTextView = view.findViewById(R.id.tv_study_calendar_title)
        backImgButton = view.findViewById(R.id.imgbtn_study_calendar_back)
        forwardImgButton = view.findViewById(R.id.imgbtn_study_calendar_forwards)
        progressBarLoad = view.findViewById(R.id.progress_study_calendar_load)

        val calendarView: WeekView = view.findViewById(R.id.weekView)
        val calendarEventAdapter = CalendarEventAdapter(this)
        calendarView.adapter = calendarEventAdapter
        calendarView.setTimeFormatter { i -> "$i:00" }
        val weekdays = arrayOf("M", "T", "W", "T", "F", "S", "S")
        calendarView.setDateFormatter { calendar -> weekdays[calendar.get(Calendar.DAY_OF_WEEK) - 1] }

        onCalendarRangeChanged(Calendar.getInstance(), Calendar.getInstance())
        nextBackDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -1) }
        nextForwardDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 1) }

        backImgButton.setOnClickListener {
            calendarView.scrollToDate(nextBackDate)
        }
        forwardImgButton.setOnClickListener {
            calendarView.scrollToDate(nextForwardDate)
        }
    }

    override fun onCalendarRangeChanged(firstVisibleDate: Calendar, lastVisibleDate: Calendar) {
        if (firstVisibleDate == lastVisibleDate) {
            titleTextView.text = SimpleDateFormat(getString(R.string.study_calendar_title_date_format), Locale.getDefault()).format(firstVisibleDate.time)

            nextBackDate = (firstVisibleDate.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, -1) }
            nextForwardDate = (firstVisibleDate.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, 1) }
        } else {
            // TODO
        }
    }

    override fun onCalendarLoadingChanged(isLoading: Boolean) {
        if (isLoading) {
            backImgButton.visibility = View.INVISIBLE
            forwardImgButton.visibility = View.INVISIBLE
            progressBarLoad.visibility = View.VISIBLE
        } else {
            backImgButton.visibility = View.VISIBLE
            forwardImgButton.visibility = View.VISIBLE
            progressBarLoad.visibility = View.INVISIBLE
        }
    }
}