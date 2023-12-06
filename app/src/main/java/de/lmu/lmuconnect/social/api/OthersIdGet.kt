package de.lmu.lmuconnect.social.api

import com.google.gson.annotations.SerializedName

data class OthersIdGetResponse(
    @SerializedName("othersId")
    val othersId: String
)