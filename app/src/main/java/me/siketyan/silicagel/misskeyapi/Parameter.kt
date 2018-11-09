package me.siketyan.silicagel.misskeyapi

import org.json.JSONObject
import me.siketyan.silicagel.misskeyapi.api.entities.Geo
import me.siketyan.silicagel.misskeyapi.api.entities.Poll
import org.json.JSONArray

class Parameter {
    private val parameters = JSONObject()
    fun append(key: String, value: String): Parameter {
        parameters.put(key, value)
        return this
    }

    fun append(key: String, value: Int): Parameter {
        parameters.put(key, value)
        return this
    }

    fun append(key: String, value: Long): Parameter {
        parameters.put(key, value)
        return this
    }

    fun append(key: String, value: Boolean): Parameter {
        parameters.put(key, value)
        return this
    }

    fun append(key: String, value: Geo): Parameter {
        parameters.put(key, value)
        return this
    }

    fun append(key: String, value: List<String>): Parameter {
        parameters.put(key, value)
        return this
    }

    fun append(key: String, value: Poll): Parameter {
        parameters.put(key, value)
        return this
    }

    fun append(key: String, value: JSONArray): Parameter {
        parameters.putOpt(key, value)
        return this
    }

    fun build(): String? = parameters.toString()
}
