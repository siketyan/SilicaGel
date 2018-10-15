package me.siketyan.silicagel.misskeyapi.api.entities

import com.google.gson.annotations.SerializedName
import me.siketyan.silicagel.misskeyapi.api.entities.DriveFile
import me.siketyan.silicagel.misskeyapi.api.entities.Geo
import me.siketyan.silicagel.misskeyapi.api.entities.Poll
import me.siketyan.silicagel.misskeyapi.api.entities.User

class createdNote(
        @SerializedName("createdNote")
        var createdNote: Note) {
}