package me.siketyan.silicagel.misskeyapi.api.entities

import com.google.gson.annotations.SerializedName

class User(
        @SerializedName("id")
        val id: String,

        @SerializedName("createdAt")
        val createdAt: String,

        @SerializedName("name")
        val name: String,

        @SerializedName("username")
        val userName: String,

        @SerializedName("description")
        val description: String,

        @SerializedName("avatarId")
        val avatarId: String,

        @SerializedName("avatarUrl")
        val avatarUrl: String,

        @SerializedName("bannerId")
        val bannerId: String,

        @SerializedName("bannerUrl")
        val bannerUrl: String,

        @SerializedName("driveCapacity")
        val driveCapacity: Long,

        @SerializedName("followersCount")
        val followersCount: Long,

        @SerializedName("followingCount")
        val followingCount: Long,

        @SerializedName("isFollowing")
        val isFollowing: Boolean,

        @SerializedName("isFollowed")
        val isFollowed: Boolean,

        @SerializedName("isMuted")
        val isMuted: Boolean,

        @SerializedName("notesCount")
        val notesCount: Long,

        @SerializedName("pinnedNote")
        val pinnedNote: Note,

        @SerializedName("pinnedNoteId")
        val pinnedNoteId: String,

        @SerializedName("host")
        val host: String,

        @SerializedName("twitter")
        val twitter: Twitter,

        @SerializedName("isBot")
        val isBot: Boolean,

        @SerializedName("isCat")
        val isCat: Boolean,

        @SerializedName("profile")
        val profile: Profile,

        @SerializedName("avatarColor")
        val avatarColor: kotlin.collections.List<Long>,

        @SerializedName("bannerColor")
        val bannerColor: kotlin.collections.List<Long>
        ) {
}