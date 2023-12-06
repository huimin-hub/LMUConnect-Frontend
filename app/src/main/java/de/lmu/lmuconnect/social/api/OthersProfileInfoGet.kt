package de.lmu.lmuconnect.social.api

import com.google.gson.annotations.SerializedName



data class OthersProfileInfoGetResponse(

        @SerializedName("id")
        val id: String,

        @SerializedName("name")
        val name: String,

        @SerializedName("email")
        val email: String,

        @SerializedName("school")
        val school: String,

        @SerializedName("major")
        val major: String,

        @SerializedName("degree")
        val degree: String,

        @SerializedName("phone")
        val phone: String,

        @SerializedName("picture")
        val picture: String,

        @SerializedName("social")
        val social: Social
) {
        data class Social(
                @SerializedName("discord")
                val discord: String = "",

                @SerializedName("github")
                val github: String = "",

                @SerializedName("instagram")
                val instagram: String = ""
        )
}
