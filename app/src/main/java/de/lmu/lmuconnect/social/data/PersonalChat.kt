package de.lmu.lmuconnect.social.data

import com.google.gson.annotations.SerializedName
import java.util.*

sealed class PersonalChat {
    abstract var id: String
    abstract val sender: ProfileInfo
    abstract val lastTime: String
    abstract var lastMessage: String

    data class Basic(
        @SerializedName("id")
        override var id : String,

        @SerializedName("sender")
        override val sender : ProfileInfo,

        @SerializedName("lastTime")
        override val lastTime : String,

        @SerializedName("lastMessage")
        override var lastMessage : String

    ) : PersonalChat()

    data class Content(
        @SerializedName("id")
        override var id : String,

        @SerializedName("sender")
        override val sender : ProfileInfo,

        @SerializedName("lastTime")
        override val lastTime : String,

        @SerializedName("lastMessage")
        override var lastMessage : String,

        val senderInfo : ProfileInfo,

        @SerializedName("message")
        val events : List<Message>
    ) : PersonalChat()
    }
