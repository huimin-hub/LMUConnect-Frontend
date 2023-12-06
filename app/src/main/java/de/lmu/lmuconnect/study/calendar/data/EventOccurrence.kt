package de.lmu.lmuconnect.study.calendar.data

import com.google.gson.annotations.SerializedName
import de.lmu.lmuconnect.study.courses.data.Course
import java.util.Date

data class EventOccurrence(
    @SerializedName("course_id")
    val courseId: String,

    @SerializedName("course_name")
    val courseName: String,

    @SerializedName("group")
    val group: String?,

    @SerializedName("start_time")
    val startTime: Float,

    @SerializedName("end_time")
    val endTime: Float,

    @SerializedName("is_ct")
    val isCT: Boolean,

    @SerializedName("date")
    val date: Date,

    @SerializedName("location")
    val location: String,

    @SerializedName("roomfinder_link")
    val roomfinderLink: String?,

    @SerializedName("type")
    val type: Course.Type
)
