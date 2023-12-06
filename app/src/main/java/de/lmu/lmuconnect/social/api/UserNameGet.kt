package de.lmu.lmuconnect.social.api

import com.google.gson.annotations.SerializedName

data class UserNameGetResponse(
    @SerializedName("name")
    val name: String
)