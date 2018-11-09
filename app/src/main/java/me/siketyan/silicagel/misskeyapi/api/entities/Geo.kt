package me.siketyan.silicagel.misskeyapi.api.entities

import com.google.gson.annotations.SerializedName

class Geo(
        @SerializedName("coordinates")
        val coordinates: kotlin.collections.List<Long>,

        @SerializedName("altitude")
        val altitude: Long,

        @SerializedName("accuracy")
        val accuracy: Long,

        @SerializedName("altitudeAccuracy")
        val altitudeAccuracy: Long,

        @SerializedName("heading")
        val heading: Long,

        @SerializedName("speed")
        val speed: Long) {
}