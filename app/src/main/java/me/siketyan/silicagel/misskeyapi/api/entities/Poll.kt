package me.siketyan.silicagel.misskeyapi.api.entities

import com.google.gson.annotations.SerializedName

class Poll(
        @SerializedName("choices")
        val choices: kotlin.collections.List<Choice>) {
}