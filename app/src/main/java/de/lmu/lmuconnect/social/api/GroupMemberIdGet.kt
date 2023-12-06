package de.lmu.lmuconnect.social.api

import com.google.gson.annotations.SerializedName

data class GroupMemberIdRequest(
    @SerializedName("email")
    val email: String
)

data class GroupMemberIdResponse(
    @SerializedName("matrixId")
    val matrixId: String
)