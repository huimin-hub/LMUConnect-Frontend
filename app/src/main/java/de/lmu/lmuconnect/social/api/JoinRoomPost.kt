package de.lmu.lmuconnect.social.api

import com.google.gson.annotations.SerializedName

data class JoinRoomPostRequest(
    @SerializedName("email")
    val email: String,

    @SerializedName("roomId")
    val roomId: String
)
