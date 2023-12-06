package de.lmu.lmuconnect.social.api

import com.google.gson.annotations.SerializedName

data class GroupMemberGetRequest(
    @SerializedName("matrix_ids")
    val matrixIds: List<String>
)

data class GroupMemberGetResponse(
    @SerializedName("memberList")
    val memberList: List<String>
)