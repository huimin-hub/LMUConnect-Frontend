package de.lmu.lmuconnect.social.personal

import org.matrix.android.sdk.api.session.room.model.RoomSummary

interface PersonalItemAdapterEventHandler {
    fun handlePersonalItemClickEvent(personal: RoomSummary)
}