package de.lmu.lmuconnect.study.courses.data

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Event(
    @SerializedName("group")
    val group: String?,

    @SerializedName("weekday")
    val weekday: Int,

    @SerializedName("start_time")
    val startTime: Float,

    @SerializedName("end_time")
    val endTime: Float,

    @SerializedName("is_ct")
    val isCT: Boolean,

    @SerializedName("frequency")
    val frequency: Frequency,

    @SerializedName("start_date")
    val startDate: Date,

    @SerializedName("end_date")
    val endDate: Date,

    @SerializedName("location")
    val location: String,

    @SerializedName("roomfinder_link")
    val roomfinderLink: String?,

    @SerializedName("type")
    val type: Course.Type
) {
    enum class Frequency(private var value: String) {
        @SerializedName("weekly")
        WEEKLY("Weekly"),

        @SerializedName("biweekly")
        BIWEEKLY("Biweekly"),

        @SerializedName("single")
        SINGLE("Once"),

        @SerializedName("continuous")
        CONTINUOUS("Continuous");

        override fun toString(): String {
            return value
        }
    }
}
