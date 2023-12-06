package de.lmu.lmuconnect.social.data

import com.google.gson.annotations.SerializedName
import java.util.*

sealed class GroupChat {
    abstract val id: String
    abstract val name: String
    abstract val profileUrl: String
    abstract val lastTime: String
    abstract val lastMessage: String

    data class Basic(
        @SerializedName("id")
        override val id : String,

        @SerializedName("name")
        override val name : String,

        @SerializedName("profileUrl")
        override val profileUrl : String,

        @SerializedName("lastTime")
        override val lastTime : String,

        @SerializedName("lastMessage")
        override val lastMessage : String

    ) : GroupChat()

    data class Content(
        @SerializedName("id")
        override val id : String,

        @SerializedName("name")
        override val name : String,

        @SerializedName("profileUrl")
        override val profileUrl : String,

        @SerializedName("lastTime")
        override val lastTime : String,

        @SerializedName("lastMessage")
        override val lastMessage : String,

        @SerializedName("message")
        val events : List<Message>,

        @SerializedName("member")
        val member : List<ProfileInfo>
    ) : GroupChat()
    }
