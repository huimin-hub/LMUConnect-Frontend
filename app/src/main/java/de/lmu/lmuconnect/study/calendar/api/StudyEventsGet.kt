package de.lmu.lmuconnect.study.calendar.api

import com.google.gson.annotations.SerializedName
import de.lmu.lmuconnect.study.calendar.data.EventOccurrence

data class StudyEventsGetResponse(
    @SerializedName("events")
    val events: List<EventOccurrence>
)