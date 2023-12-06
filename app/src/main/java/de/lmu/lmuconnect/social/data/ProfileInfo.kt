package de.lmu.lmuconnect.social.data

import de.lmu.lmuconnect.social.api.ProfileInfoGetResponse

data class ProfileInfo(
    val name: String,
    val email: String,
    val degree: String,
    val school: String,
    val picture: String,
    val major: String,
    val phone: String,
    val social: ProfileInfoGetResponse.Social
)
