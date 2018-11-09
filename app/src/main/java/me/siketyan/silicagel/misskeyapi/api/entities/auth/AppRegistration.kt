package me.siketyan.silicagel.misskeyapi.api.entities.auth

import com.google.gson.annotations.SerializedName

class AppRegistration(
        @SerializedName("secret")
        var AppSecret: String = "") {
}