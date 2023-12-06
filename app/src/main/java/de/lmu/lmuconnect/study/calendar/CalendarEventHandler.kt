package de.lmu.lmuconnect.study.calendar

import java.util.*

interface CalendarEventHandler {
    fun onCalendarRangeChanged(firstVisibleDate: Calendar, lastVisibleDate: Calendar)

    fun onCalendarLoadingChanged(isLoading: Boolean)
}