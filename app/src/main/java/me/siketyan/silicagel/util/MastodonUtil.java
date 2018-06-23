package me.siketyan.silicagel.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.gson.Gson;
import com.sys1yagi.mastodon4j.MastodonClient;
import com.sys1yagi.mastodon4j.api.entity.auth.AppRegistration;
import okhttp3.OkHttpClient;

public class MastodonUtil {
    private static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static MastodonClient getClient(Context context, boolean withToken) {
        MastodonClient.Builder builder = new MastodonClient
            .Builder(getInstanceName(context), new OkHttpClient.Builder(), new Gson());

        if (withToken) {
            builder.accessToken(loadAccessToken(context));
        }

        return builder.build();
    }

    public static void storeAppRegistration(Context context, AppRegistration appRegistration) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString("mastodon_instance", appRegistration.getInstanceName());
        editor.putString("mastodon_client_id", appRegistration.getClientId());
        editor.putString("mastodon_client_secret", appRegistration.getClientSecret());
        editor.putString("mastodon_redirect_uri", appRegistration.getRedirectUri());
        editor.apply();
    }

    public static void storeApp(Context context, String instanceName, String accessToken) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString("mastodon_instance", instanceName);
        editor.putString("mastodon_token", accessToken);
        editor.apply();
    }

    public static void deleteApp(Context context) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.remove("mastodon_instance");
        editor.remove("mastodon_token");
        editor.apply();
    }

    private static String loadAccessToken(Context context) {
        return getPreferences(context).getString("mastodon_token", null);
    }

    public static boolean hasAccessToken(Context context) {
        return loadAccessToken(context) != null;
    }

    public static String getInstanceName(Context context) {
        return getPreferences(context).getString("mastodon_instance", null);
    }

    public static String getClientId(Context context) {
        return getPreferences(context).getString("mastodon_client_id", null);
    }

    public static String getClientSecret(Context context) {
        return getPreferences(context).getString("mastodon_client_secret", null);
    }

    public static String getRedirectUri(Context context) {
        return getPreferences(context).getString("mastodon_redirect_uri", null);
    }

    public static void setInstanceName(Context context, String instanceName) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString("mastodon_instance", instanceName);
        editor.apply();
    }
}