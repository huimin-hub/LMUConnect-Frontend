package de.lmu.lmuconnect.auth.api

import com.google.gson.annotations.SerializedName

data class AuthSignupPostRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String
)

data class AuthSignupPostResponse(
    @SerializedName("auth_token")
    val authToken: String
)