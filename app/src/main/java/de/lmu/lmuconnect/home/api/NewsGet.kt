package de.lmu.lmuconnect.home.api

import com.google.gson.annotations.SerializedName

data class NewsGetResponse(
    @SerializedName("news")
    val items: List<Item>
) {
    data class Item(
        @SerializedName("_id")
        val id: String,

        @SerializedName("title")
        val title: String,

        @SerializedName("abstract")
        val abstract: String,

        @SerializedName("link")
        val link: String,

        @SerializedName("date")
        val date: String,

        @SerializedName("imageUrl")
        val imageUrl: String
    )
}