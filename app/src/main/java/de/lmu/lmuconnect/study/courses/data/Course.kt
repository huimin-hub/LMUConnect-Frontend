package de.lmu.lmuconnect.study.courses.data

import com.google.gson.annotations.SerializedName
import de.lmu.lmuconnect.R
import java.util.*

sealed class Course {
    abstract val id: String
    abstract val name: String
    abstract val term: String
    abstract val lecturer: String
    abstract val type: Type

    data class Basic(
        @SerializedName("id")
        override val id : String,

        @SerializedName("name")
        override val name : String,

        @SerializedName("term")
        override val term : String,

        @SerializedName("lecturer")
        override val lecturer : String,

        @SerializedName("type")
        override val type : Type
    ) : Course()

    data class Info(
        @SerializedName("id")
        override val id : String,

        @SerializedName("name")
        override val name : String,

        @SerializedName("term")
        override val term : String,

        @SerializedName("language")
        val language : String,

        @SerializedName("lecturer")
        override val lecturer : String,

        @SerializedName("type")
        override val type : Type,

        @SerializedName("max_participants")
        val maxParticipants : Int,

        @SerializedName("organisational_unit")
        val organisationalUnit : String,

        @SerializedName("lsf_id")
        val lsfId : String,

        @SerializedName("events")
        val events : List<Event>
    ) : Course() {
        fun getPrettyLanguage(): String {
            return Locale(language).displayName
        }
    }


    enum class Type(var stringRes: Int, var iconResId: Int) {
        @SerializedName("LECTURE")
        LECTURE(R.string.study_course_type_lecture, R.drawable.study_lecture),

        @SerializedName("INTERNSHIP")
        INTERNSHIP(R.string.study_course_type_internship, R.drawable.study_practical),

        @SerializedName("SEMINAR")
        SEMINAR(R.string.study_course_type_seminar, R.drawable.study_seminar),

        @SerializedName("TUTORIAL")
        TUTORIAL(R.string.study_course_type_tutorial, R.drawable.study_tutorial),

        @SerializedName("EXERCISE_COURSE")
        EXERCISE_COURSE(R.string.study_course_type_exercise_course, R.drawable.study_exercise);
    }

    data class Detail(
        val iconRes: Int,
        val title: String,
        val content: String
    )

    fun getPrettyTerm(): String {
        val sb = StringBuilder(term) // Example: wise2223
        sb.insert(2, ' ') // wi se2223
        sb.insert(5, ' ') // wi se 2223
        if (sb.length > 8) sb.insert(8, '/') // wi se 22/23
        val parts = sb.toString().split(' ')
        return "${capitalize(parts[0])}${capitalize(parts[1])} ${parts[2]}"
    }

    private fun capitalize(str: String): String = str.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(
            Locale.getDefault()
        ) else it.toString()
    }
}