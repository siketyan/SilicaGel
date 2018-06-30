package me.siketyan.silicagel.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.gson.Gson;
import com.sys1yagi.mastodon4j.MastodonClient;
import okhttp3.OkHttpClient;

public class MastodonUtil {
    private static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
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

    private static String getInstanceName(Context context) {
        return getPreferences(context).getString("mastodon_instance", null);
    }

    public static MastodonClient getClient(Context context, boolean withToken) {
        MastodonClient.Builder builder = new MastodonClient
            .Builder(getInstanceName(context), new OkHttpClient.Builder(), new Gson());

        if (withToken) {
            builder.accessToken(loadAccessToken(context));
        }

        return builder.build();
    }
}