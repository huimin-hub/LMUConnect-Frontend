package de.lmu.lmuconnect.social.api

import com.google.gson.annotations.SerializedName

data class ProfileInfoPatchRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("school")
    val school: String,

    @SerializedName("major")
    val major: String,

    @SerializedName("degree")
    val degree: String,

    @SerializedName("phone")
    val phone: String,

    @SerializedName("discord")
    val discord: String,

    @SerializedName("github")
    val github: String,

    @SerializedName("ins")
    val ins: String
)