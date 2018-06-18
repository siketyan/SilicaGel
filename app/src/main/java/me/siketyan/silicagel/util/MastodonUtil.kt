package me.siketyan.silicagel.util

import android.content.Context
import android.preference.PreferenceManager
import com.sys1yagi.mastodon4j.MastodonClient
import com.sys1yagi.mastodon4j.api.method.*

object MastodonUtil {

    fun storeApp(context: Context, instanceName: String, clientId: String, clientSecret: String, redirectUri: String) {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = pref.edit()
        editor.putString("instance_name", instanceName)
        editor.putString("clientId", clientId)
        editor.putString("clientSecret", clientSecret)
        editor.putString("redirectUri", redirectUri)
        editor.apply()
    }

    fun storeAccessToken(context: Context, accessToken: String?) {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = pref.edit()
        editor.putString("mastodon_access_token", accessToken)
        editor.apply()
    }

    fun deleteAccessToken(context: Context) {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = pref.edit()
        editor.remove("instance_name")
        editor.remove("clientId")
        editor.remove("clientSecret")
        editor.remove("redirectUri")
        editor.remove("mastodon_access_token")
        editor.apply()
    }

    fun loadAccessToken(context: Context): String? {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val accessToken = pref.getString("mastodon_access_token", null)

        return if (accessToken != null) {
            accessToken
        } else {
            null
        }
    }

    fun hasAccessToken(context: Context): Boolean {
        return loadAccessToken(context) != null
    }

    fun getInstanceName(context: Context): String? {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val instanceName = pref.getString("instance_name", null)
        return instanceName
    }

    fun getStatuses(client: MastodonClient): Statuses {
        val statuses = Statuses(client)
        return statuses
    }

    fun getMedia(client: MastodonClient): Media {
        val media = Media(client)
        return media
    }

    fun getInstances(client: MastodonClient): Public {
        val instance = Public(client)
        return instance
    }
}