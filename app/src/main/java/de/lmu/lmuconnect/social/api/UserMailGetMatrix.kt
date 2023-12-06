package de.lmu.lmuconnect.social.api

import com.google.gson.annotations.SerializedName

data class UserMailGetMatrixResponse(
    @SerializedName("email")
    val email: String
)