package de.lmu.lmuconnect.social.data

import com.stfalcon.chatkit.commons.models.IMessage
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.model.message.MessageContent
import java.util.*

data class ChatEventMessageWrapper(private val timelineEvent: TimelineEvent) : IMessage {

    override fun getId() = timelineEvent.localId.toString()

    override fun getText(): String {
        return when (timelineEvent.root.getClearType()) {
            EventType.MESSAGE -> formatMessage(timelineEvent)
            else -> "Event of type ${timelineEvent.root.getClearType()} not rendered yet"
        }
    }

    private fun formatMessage(timelineEvent: TimelineEvent): String {
        // You can use the toModel extension method to serialize the json map to one of the sdk defined content.
        val messageContent = timelineEvent.root.getClearContent().toModel<MessageContent>() ?: return ""
        return messageContent.body
    }

    override fun getUser() = ChatEventSenderWrapper(timelineEvent.senderInfo)


    override fun getCreatedAt() = Date(timelineEvent.root.originServerTs ?: 0)
}