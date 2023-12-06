package de.lmu.lmuconnect.menu.data

data class MenuItem(
    val itemId : String,
    val itemIconUrl : String,
    val itemName : String,
    val itemLink : String,
    var isFavourite : Boolean
)
