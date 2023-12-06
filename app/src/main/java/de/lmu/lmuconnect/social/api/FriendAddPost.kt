package de.lmu.lmuconnect.social.api

import com.google.gson.annotations.SerializedName

data class FriendAddRequest(
    @SerializedName("email")
    val email: String
)

data class FriendAddResponse(
    @SerializedName("name")
    val name: String,

    @SerializedName("matrixId")
    val matrixId: String
)