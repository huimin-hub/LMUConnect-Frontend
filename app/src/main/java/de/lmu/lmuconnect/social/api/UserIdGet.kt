package de.lmu.lmuconnect.social.api

import com.google.gson.annotations.SerializedName

data class UserIdGetResponse(
    @SerializedName("name")
    val name: String
)