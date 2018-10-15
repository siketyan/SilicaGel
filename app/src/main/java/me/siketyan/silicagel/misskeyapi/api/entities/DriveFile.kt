package me.siketyan.silicagel.misskeyapi.api.entities

import com.google.gson.annotations.SerializedName

class DriveFile(
        @SerializedName("id")
        val id: String,

        @SerializedName("createdAt")
        val createdAt: String,

        @SerializedName("userId")
        val userId: String,

        @SerializedName("user")
        val user: User,

        @SerializedName("name")
        val name: String,

        @SerializedName("md5")
        val md5: String,

        @SerializedName("type")
        val type: String,

        @SerializedName("datasize")
        val datasize: Long,

        @SerializedName("url")
        val url: String,

        @SerializedName("folderId")
        val folderId: String,

        @SerializedName("folder")
        val folder: DriveFolder,

        @SerializedName("isSensitive")
        val isSensitive: Boolean) {
}