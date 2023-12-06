package de.lmu.lmuconnect.social.api

import com.google.gson.annotations.SerializedName

data class ProfilePictureGetRequest(
    @SerializedName("email")
    val email: String
)
