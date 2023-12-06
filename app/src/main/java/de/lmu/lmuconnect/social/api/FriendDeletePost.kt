package de.lmu.lmuconnect.social.api

import com.google.gson.annotations.SerializedName

data class FriendDeleteRequest(
    @SerializedName("friendId")
    val friendId: String
)