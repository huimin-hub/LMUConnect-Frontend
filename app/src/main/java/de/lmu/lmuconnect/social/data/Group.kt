package de.lmu.lmuconnect.social.data

import com.google.gson.annotations.SerializedName
import java.util.*

sealed class Group {
    abstract val id: String
    abstract val name: String
    //abstract val profileUrl: String
    abstract val member: List<ProfileInfo>
    abstract val type: Type
    abstract val message : List<Message>
    abstract val lastTime : String
    abstract val lastMessage : String

    data class Basic(
        @SerializedName("id")
        override val id : String,

        @SerializedName("name")
        override val name : String,

        @SerializedName("member")
        override val member : List<ProfileInfo>,

        @SerializedName("type")
        override val type : Type,

        @SerializedName("message")
        override val message : List<Message>,

        @SerializedName("lastTime")
        override val lastTime : String,

        @SerializedName("lastMessage")
        override val lastMessage : String
    ) : Group()


    enum class Type(private var value: String) {
        @SerializedName("OTHERS")
        OTHERS("Others"),

        @SerializedName("LECTURE")
        LECTURE("Lecture"),

        @SerializedName("INTERNSHIP")
        INTERNSHIP("Practical course"),

        @SerializedName("SEMINAR")
        SEMINAR("Seminar"),

        @SerializedName("TUTORIAL")
        TUTORIAL("Tutorial"),

        @SerializedName("EXERCISE_COURSE")
        EXERCISE_COURSE("Exercise course");

        override fun toString(): String {
            return value
        }
    }
}
