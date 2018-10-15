package me.siketyan.silicagel.misskeyapi.api.endpoints

import android.util.Log
import okhttp3.MediaType
import okhttp3.RequestBody
import me.siketyan.silicagel.misskeyapi.MisskeyClient
import me.siketyan.silicagel.misskeyapi.MisskeyRequest
import me.siketyan.silicagel.misskeyapi.Parameter
import me.siketyan.silicagel.misskeyapi.Permission
import me.siketyan.silicagel.misskeyapi.api.entities.auth.AccessToken
import me.siketyan.silicagel.misskeyapi.api.entities.auth.AppRegistration
import me.siketyan.silicagel.misskeyapi.api.entities.auth.Token
import me.siketyan.silicagel.misskeyapi.api.exception.MisskeyRequestException
import org.json.JSONArray

class Apps(private val client: MisskeyClient) {
    @Throws(MisskeyRequestException::class)
    fun createApp(
            name: String,
            description: String,
            cb: String?,
            //permission: String
            permission: JSONArray
    ): MisskeyRequest<AppRegistration>? {
        var parameters = Parameter().apply {
            append("name", name)
            append("description", description)
            cb?.let { append("cb", it) }
            append("permission", permission)
        }.build()
        Log.d("parameters", parameters)
        //parameters = parameters?.replace("\\\"", "\"")
        //Log.d("replace parameters", parameters)
        return MisskeyRequest(
                {
                    client.post("app/create",
                            RequestBody.create(
                                    MediaType.parse("application/json; charset=utf-8"),
                                    parameters
                            ))
                },
                {
                    client.getSerializer().fromJson(it, AppRegistration::class.java)
                }
        )
    }

    @Throws(MisskeyRequestException::class)
    fun getAuthToken(
            appSecret: String
    ): MisskeyRequest<Token>? {
        val parameters = Parameter().apply {
            append("appSecret", appSecret)
        }.build()
        return MisskeyRequest(
                {
                    client.post("auth/session/generate",
                            RequestBody.create(
                                    MediaType.parse("application/json; charset=utf-8"),
                                    parameters
                            ))
                },
                {
                    client.getSerializer().fromJson(it, Token::class.java)
                }
        )
    }

    fun getAccessToken(
            appSecret: String,
            token: String
    ): MisskeyRequest<AccessToken> {
        val parameters = Parameter().apply {
            append("appSecret", appSecret)
            append("token", token)
        }.build()
        Log.d("param", parameters)
        return MisskeyRequest(
                {
                    client.post("auth/session/userkey",
                            RequestBody.create(
                                    MediaType.parse("application/json; charset=utf-8"),
                                    parameters
                            ))
                },
                {
                    client.getSerializer().fromJson(it, AccessToken::class.java)
                }
        )
    }
}

