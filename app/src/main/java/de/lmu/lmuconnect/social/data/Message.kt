package de.lmu.lmuconnect.social.data

data class Message(
    val sender: ProfileInfo,
    val message: String,
    val time: String,
    val read: Boolean
)
