package me.siketyan.silicagel.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.sys1yagi.mastodon4j.MastodonClient;
import com.sys1yagi.mastodon4j.api.method.Media;
import com.sys1yagi.mastodon4j.api.method.Statuses;

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

    public static String loadAccessToken(Context context) {
        return getPreferences(context).getString("mastodon_token", null);
    }

    public static boolean hasAccessToken(Context context) {
        return loadAccessToken(context) != null;
    }

    public static String getInstanceName(Context context) {
        return getPreferences(context).getString("mastodon_instance", null);
    }

    public static Statuses getStatuses(MastodonClient client) {
        return new Statuses(client);
    }

    public static Media getMedia(MastodonClient client) {
        return new Media(client);
    }
}