package com.nguyen.composeyelp.data.model

import com.google.gson.annotations.SerializedName

data class YelpResult(val total: Int, val businesses: List<Business>)

// Gson skips Kotlin's null checks, so fields Yelp may omit are nullable
data class Business(
    val id: String,
    val name: String,
    val rating: Double,
    val price: String?,
    @SerializedName("review_count") val reviewCount: Int,
    val distance: Double,
    @SerializedName("image_url") val imageUrl: String?,
    val categories: List<Category>?,
    val location: Location?
) {
    fun toDistance(): String {
        val miles = "%.2f".format(distance * 0.000621371)
        return "$miles mi"
    }
}

data class Category(val title: String)

data class Location(val address1: String?)
