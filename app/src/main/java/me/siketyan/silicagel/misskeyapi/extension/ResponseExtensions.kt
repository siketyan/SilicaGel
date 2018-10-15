package me.siketyan.silicagel.misskeyapi.extension

import com.google.gson.Gson
import okhttp3.Response
import me.siketyan.silicagel.misskeyapi.api.exception.MisskeyRequestException
import java.io.IOException
import java.lang.reflect.Type

inline fun <reified T> Response.fromJson(gson: Gson, clazz: Class<T>): T {
    try {
        val json = body().string()
        return gson.fromJson(json, clazz)
    } catch (e: IOException) {
        throw MisskeyRequestException(e)
    }
}

inline fun <reified T> Response.fromJson(gson: Gson, type: Type): T {
    try {
        val json = body().string()
        return gson.fromJson(json, type)
    } catch (e: IOException) {
        throw MisskeyRequestException(e)
    }
}
