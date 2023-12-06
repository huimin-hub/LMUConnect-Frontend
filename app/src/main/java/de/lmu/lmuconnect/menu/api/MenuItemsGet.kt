package de.lmu.lmuconnect.menu.api

import com.google.gson.annotations.SerializedName

data class MenuItemsGetResponse(
    @SerializedName("items")
    val items: List<Item>
) {
    data class Item(
        @SerializedName("id")
        val id: String,

        @SerializedName("name")
        val name: String,

        @SerializedName("url")
        val url: String,

        @SerializedName("icon")
        val iconUrl: String,

        @SerializedName("isFavourite")
        val isFavourite: Boolean
    )
}