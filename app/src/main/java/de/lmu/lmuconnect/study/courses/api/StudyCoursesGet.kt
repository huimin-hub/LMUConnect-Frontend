package de.lmu.lmuconnect.study.courses.api

import com.google.gson.annotations.SerializedName
import de.lmu.lmuconnect.study.courses.data.Course

data class StudyCoursesGetResponse(
    @SerializedName("courses")
    val items: List<Course.Basic>
)