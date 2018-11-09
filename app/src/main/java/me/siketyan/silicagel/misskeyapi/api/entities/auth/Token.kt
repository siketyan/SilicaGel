package me.siketyan.silicagel.misskeyapi.api.entities.auth

import com.google.gson.annotations.SerializedName

class Token(
        @SerializedName("token")
        var Token: String = "",

        @SerializedName("url")
        var Url: String = "") {
}
