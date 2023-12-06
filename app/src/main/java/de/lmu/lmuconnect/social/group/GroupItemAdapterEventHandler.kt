package de.lmu.lmuconnect.social.group

import org.matrix.android.sdk.api.session.room.model.RoomSummary

interface GroupItemAdapterEventHandler {
    fun handleGroupItemClickEvent(group: RoomSummary)
}