package de.lmu.lmuconnect.study.courses

import de.lmu.lmuconnect.study.courses.data.Course

interface CoursesItemAdapterEventHandler {
    fun handleCourseItemClickEvent(course: Course.Basic)
}