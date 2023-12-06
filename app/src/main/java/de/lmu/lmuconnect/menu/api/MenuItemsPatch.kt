package de.lmu.lmuconnect.menu.api

import com.google.gson.annotations.SerializedName

data class MenuItemsPatchRequest(
    @SerializedName("itemId")
    val itemId: String,

    @SerializedName("dataChange")
    val dataChange: DataChange
) {
    data class DataChange(
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