package de.lmu.lmuconnect.study.courses.api

import com.google.gson.annotations.SerializedName

data class StudyCoursesRegistrationRequest(
    @SerializedName("courseId")
    val courseId: String
)