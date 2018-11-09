package me.siketyan.silicagel.misskeyapi.api.entities

import com.google.gson.annotations.SerializedName

class DriveFolder(
        @SerializedName("id")
        val id: String,

        @SerializedName("createdAt")
        val createdAt: String,

        @SerializedName("userId")
        val userId: String,

        @SerializedName("parentId")
        val parentId: DriveFolder,

        @SerializedName("name")
        val name: String,

        @SerializedName("foldersCount")
        val foldersCount: Long,

        @SerializedName("filesCount")
        val filesCount: Long) {
}