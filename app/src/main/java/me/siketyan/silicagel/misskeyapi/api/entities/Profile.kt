package me.siketyan.silicagel.misskeyapi.api.entities

import com.google.gson.annotations.SerializedName

class Profile(
        @SerializedName("location")
        val location: String,

        @SerializedName("birthday")
        val birthday: String) {
}