package me.siketyan.silicagel.misskeyapi.api.endpoints

import android.util.Log
import okhttp3.MediaType
import okhttp3.RequestBody
import me.siketyan.silicagel.misskeyapi.MisskeyClient
import me.siketyan.silicagel.misskeyapi.MisskeyRequest
import me.siketyan.silicagel.misskeyapi.Parameter
import me.siketyan.silicagel.misskeyapi.api.entities.User

class i(private val client: MisskeyClient) {
    @JvmOverloads
    fun getMe(
            i: String
    ): MisskeyRequest<User> {
        val parameters = Parameter().apply {
            append("i", i)
        }.build()
        Log.d("param", parameters)
        return MisskeyRequest(
                {
                    client.post("i",
                            RequestBody.create(
                                    MediaType.parse("application/json; charset=utf-8"),
                                    parameters
                            ))
                },
                {
                    client.getSerializer().fromJson(it, User::class.java)
                }
        )
    }
}