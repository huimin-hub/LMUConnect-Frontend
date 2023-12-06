package de.lmu.lmuconnect.auth.api

import com.google.gson.annotations.SerializedName

data class AuthLoginPostRequest(
    @SerializedName("email")
    var email: String,

    @SerializedName("password")
    var password: String
)

data class AuthLoginPostResponse(
    @SerializedName("auth_token")
    var authToken: String,

    @SerializedName("matrixName")
    var matrixName: String,

    @SerializedName("matrixPassword")
    var matrixPassword: String,

    @SerializedName("matrixId")
    var matrixId: String
)