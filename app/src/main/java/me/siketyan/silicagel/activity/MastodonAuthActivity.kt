package me.siketyan.silicagel.activity

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import com.google.gson.Gson
import com.sys1yagi.mastodon4j.MastodonClient
import com.sys1yagi.mastodon4j.api.entity.auth.AccessToken
import com.sys1yagi.mastodon4j.api.exception.Mastodon4jRequestException
import com.sys1yagi.mastodon4j.api.method.Apps

import me.siketyan.silicagel.R
import me.siketyan.silicagel.util.MastodonUtil
import okhttp3.OkHttpClient

class MastodonAuthActivity : Activity() {

    var Instance_Name = ""
    var Client_ID = ""
    var Redirect_Uri = "silicagel://mastodon"
    var Client_Secret = ""
    lateinit var client: MastodonClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault_Light_DarkActionBar)
        setContentView(R.layout.activity_twitterauth)

        val intent = intent
        if (intent == null || intent.getData() == null) {
            return
        }
        val authCode = intent.getData().getQueryParameter("code")
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        Instance_Name = pref.getString("instance_name", null)
        Client_ID = pref.getString("clientId", null)
        Client_Secret = pref.getString("clientSecret", null)
        Redirect_Uri = pref.getString("redirectUri", null)

        client = MastodonClient.Builder(Instance_Name, OkHttpClient.Builder(), Gson())
                .build()
        val apps = Apps(client)
        val task = object : AsyncTask<String, Void, AccessToken>() {
            override fun doInBackground(vararg params: String): AccessToken? {
                try {
                    val access_token = apps.getAccessToken(Client_ID, Client_Secret, Redirect_Uri, authCode, "authorization_code").execute()
                    return access_token
                } catch (e: Mastodon4jRequestException) {
                    e.printStackTrace()
                }

                return null
            }

            override fun onPostExecute(accessToken: AccessToken?) {
                if (accessToken != null) {
                    showToast(getString(R.string.mastodon_authorized))
                    val token = accessToken.accessToken
                    MastodonUtil.storeAccessToken(SettingsActivity.getContext(), token)
                } else {
                    showToast(getString(R.string.mastodon_failed))
                }

                startActivity(Intent(this@MastodonAuthActivity, SettingsActivity::class.java))
                finish()
            }
        }

        task.execute()
    }

    companion object {

        fun showToast(text: String) {
            Toast.makeText(SettingsActivity.getContext(), text, Toast.LENGTH_SHORT).show()
        }
    }
}