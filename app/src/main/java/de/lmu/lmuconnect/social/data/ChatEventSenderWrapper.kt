package de.lmu.lmuconnect.social.data

import com.stfalcon.chatkit.commons.models.IUser
import org.matrix.android.sdk.api.session.room.sender.SenderInfo

data class ChatEventSenderWrapper(private val senderInfo: SenderInfo) : IUser {
    override fun getId() = senderInfo.userId

    override fun getName() = senderInfo.disambiguatedDisplayName

    override fun getAvatar() = senderInfo.avatarUrl
}
