package de.lmu.lmuconnect.social.api

import com.google.gson.annotations.SerializedName

data class FriendsListGetResponse(
    @SerializedName("friends")
    val friends: List<String>
)