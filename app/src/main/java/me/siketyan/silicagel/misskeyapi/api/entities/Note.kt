package me.siketyan.silicagel.misskeyapi.api.entities

import com.google.gson.annotations.SerializedName
import me.siketyan.silicagel.misskeyapi.api.entities.DriveFile
import me.siketyan.silicagel.misskeyapi.api.entities.Geo
import me.siketyan.silicagel.misskeyapi.api.entities.Poll
import me.siketyan.silicagel.misskeyapi.api.entities.User

class Note(
        @SerializedName("id")
        val id: String,

        @SerializedName("createdAt")
        val createdAt: String,

        @SerializedName("viaMobile")
        val viaMobile: Boolean,

        @SerializedName("text")
        val text: String,

        @SerializedName("visibility")
        val visibility: String = Visibility.Public.value,

        @SerializedName("mediaIds")
        val mediaIds: kotlin.collections.List<String> = emptyList(),

        @SerializedName("media")
        val media: kotlin.collections.List<DriveFile> = emptyList(),

        @SerializedName("userId")
        val userId: String,

        @SerializedName("user")
        val user: User,

        @SerializedName("myReaction")
        val myReaction: String,

        @SerializedName("reactionCounts")
        val reactionCounts: Pair<String, Long>,

        @SerializedName("replyId")
        val replyId: String,

        @SerializedName("reply")
        val reply: Note,

        @SerializedName("renoteId")
        val renoteId: String,

        @SerializedName("renote")
        val renote: Note,

        @SerializedName("poll")
        val poll: Poll,

        @SerializedName("geo")
        var geo: Geo) {

    enum class Visibility(val value: String) {
        Public("public"),
        Home("home"),
        Followers("followers"),
        //Specified("specified"),
        Private("private")
    }
}