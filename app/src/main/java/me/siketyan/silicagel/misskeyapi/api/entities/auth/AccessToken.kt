package me.siketyan.silicagel.misskeyapi.api.entities.auth

import com.google.gson.annotations.SerializedName


class AccessToken(
        @SerializedName("accessToken")
        var accessToken: String = "") {
}
