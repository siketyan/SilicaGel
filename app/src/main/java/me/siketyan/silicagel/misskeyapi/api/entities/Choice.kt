package me.siketyan.silicagel.misskeyapi.api.entities

import com.google.gson.annotations.SerializedName

class Choice(
        @SerializedName("id")
        val id: Long,

        @SerializedName("isVoted")
        val isVoted: Boolean,

        @SerializedName("text")
        val text: String,

        @SerializedName("votes")
        val votes: Long) {

}