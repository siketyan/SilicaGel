package me.siketyan.silicagel.misskeyapi.api.entities

import com.google.gson.annotations.SerializedName

class Twitter(
        @SerializedName("userId")
        val userId: String? = "",

        @SerializedName("screenName")
        val screenName: String? = "") {
}