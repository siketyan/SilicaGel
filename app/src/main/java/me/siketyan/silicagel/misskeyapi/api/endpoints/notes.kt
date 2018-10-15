package me.siketyan.silicagel.misskeyapi.api.endpoints

import android.util.Log
import okhttp3.MediaType
import okhttp3.RequestBody
import me.siketyan.silicagel.misskeyapi.MisskeyClient
import me.siketyan.silicagel.misskeyapi.MisskeyRequest
import me.siketyan.silicagel.misskeyapi.Parameter
import me.siketyan.silicagel.misskeyapi.api.entities.Geo
import me.siketyan.silicagel.misskeyapi.api.entities.Note
import me.siketyan.silicagel.misskeyapi.api.entities.Poll
import me.siketyan.silicagel.misskeyapi.api.entities.createdNote
import org.json.JSONArray

class notes(private val client: MisskeyClient) {
    @JvmOverloads
    fun createNote(
            i: String,
            visibility: Note.Visibility = Note.Visibility.Public,
            text: String?,
            cw: String?,
            viaMobile: Boolean?,
            geo: Geo?,
            fileIds: JSONArray?,
            renoteId: String?,
            poll: Poll?
    ): MisskeyRequest<createdNote> {
        val parameters = Parameter().apply {
            append("i", i)
            append("visibility", visibility.value)
            text?.let { append("text", it) }
            cw?.let { append("cw", it) }
            viaMobile?.let { append("viaMobile", it) }
            geo?.let { append("geo", it) }
            fileIds?.let { append("fileIds", it) }
            renoteId?.let { append("renoteId", it) }
            poll?.let { append("poll", it) }
        }.build()
        Log.d("param", parameters)
        return MisskeyRequest(
                {
                    client.post("notes/create",
                            RequestBody.create(
                                    MediaType.parse("application/json; charset=utf-8"),
                                    parameters
                            ))
                },
                {
                    client.getSerializer().fromJson(it, createdNote::class.java)
                }
        )
    }
}