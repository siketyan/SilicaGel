package me.siketyan.silicagel.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import me.siketyan.silicagel.misskeyapi.MisskeyClient;
import me.siketyan.silicagel.misskeyapi.api.entities.auth.AppRegistration;
import okhttp3.OkHttpClient;

public class MisskeyUtil {
    private static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static MisskeyClient getClient(Context context) {
        MisskeyClient.Builder builder = new MisskeyClient
            .Builder(getInstanceName(context), new OkHttpClient.Builder(), new Gson());
        builder.debug();
        return builder.build();
    }

    public static void storeAppRegistration(Context context, AppRegistration appRegistration) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString("misskey_client_secret", appRegistration.getAppSecret());
        editor.apply();
    }

    public static void storeApp(Context context, String instanceName, String accessToken) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString("misskey_instance", instanceName);
        editor.putString("misskey_token", accessToken);
        editor.apply();
    }

    public static void deleteApp(Context context) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.remove("misskey_instance");
        editor.remove("misskey_token");
        editor.apply();
    }

    public static boolean hasAccessToken(Context context) {
        return getAccessToken(context) != null;
    }

    public static String getAccessToken(Context context) {
        return getPreferences(context).getString("misskey_token", null);
    }

    public static String getInstanceName(Context context) {
        return getPreferences(context).getString("misskey_instance", null);
    }

    public static String getClientSecret(Context context) {
        return getPreferences(context).getString("misskey_client_secret", null);
    }

    public static void setInstanceName(Context context, String instanceName) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString("misskey_instance", instanceName);
        editor.apply();
    }

    public static void storeSessionToken(Context context, String Token) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString("misskey_session_token", Token);
        editor.apply();
    }

    public static String getSessionToken(Context context) {
        return getPreferences(context).getString("misskey_session_token", null);
    }
}