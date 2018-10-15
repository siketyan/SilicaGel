package me.siketyan.silicagel.misskeyapi

import com.google.gson.JsonParser
import okhttp3.Response
import me.siketyan.silicagel.misskeyapi.api.exception.MisskeyRequestException
import java.lang.Exception

open class MisskeyRequest<T>(
        private val executor: () -> Response,
        private val mapper: (String) -> Any
) {
    interface Action1<T> {
        fun invoke(arg: T)
    }

    private var action: (String) -> Unit = {}

    @JvmSynthetic
    fun doOnJson(action: (String) -> Unit) = apply {
        this.action = action
    }

    fun doOnJson(action: Action1<String>) = apply {
        this.action = { action.invoke(it) }
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(MisskeyRequestException::class)
    fun execute(): T {
        val response = executor()
        if (response.isSuccessful) {
            try {
                val body = response.body().string()
                val element = JsonParser().parse(body)
                if (element.isJsonObject) {
                    action(body)
                    return mapper(body) as T
                } else {
                    val list = arrayListOf<Any>()
                    element.asJsonArray.forEach {
                        val json = it.toString()
                        action(json)
                        list.add(mapper(json))
                    }
                    return list as T
                }
            } catch (e: Exception) {
                throw MisskeyRequestException(e)
            }
        } else {
            throw MisskeyRequestException(response)
        }
    }
}