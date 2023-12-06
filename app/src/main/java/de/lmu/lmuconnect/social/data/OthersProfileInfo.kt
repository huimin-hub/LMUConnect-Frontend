package de.lmu.lmuconnect.social.data

import de.lmu.lmuconnect.social.api.OthersProfileInfoGetResponse


data class OthersProfileInfo(
    val id: String,
    val name: String,
    val email: String,
    val degree: String,
    val school: String,
    val picture: String,
    val major: String,
    val phone: String,
    val social: OthersProfileInfoGetResponse.Social
)
