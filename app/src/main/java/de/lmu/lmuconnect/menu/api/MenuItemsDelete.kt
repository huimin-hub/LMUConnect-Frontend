package de.lmu.lmuconnect.menu.api

import com.google.gson.annotations.SerializedName

data class MenuItemsDeleteRequest(
    @SerializedName("id")
    val id: String
)