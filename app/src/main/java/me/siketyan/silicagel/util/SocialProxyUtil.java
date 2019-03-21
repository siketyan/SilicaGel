package me.siketyan.silicagel.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class SocialProxyUtil {
    public static final String PREFIX_TOKEN = "sproxy_token";
    public static final String PREFIX_SECRET = "sproxy_secret";
    private static final String COOKIE_GLUE = ";";

    public static String getSendCookie(Context context) {
        StringBuilder sendCookie = new StringBuilder();
        List<String> cookies = getCookies(context);
        for (String cookie : cookies) {
            sendCookie.append(cookie.split(COOKIE_GLUE)[0]);
            sendCookie.append(COOKIE_GLUE);
        }

        return sendCookie.toString();
    }

    public static List<String> getCookies(Context context) {
        List<String> cookies = new ArrayList<>(2);
        SharedPreferences pref = getPreferences(context);
        String token = pref.getString(PREFIX_TOKEN, null);
        String secret = pref.getString(PREFIX_SECRET, null);

        if (token != null) {
            cookies.add(token);
        }

        if (secret != null) {
            cookies.add(secret);
        }

        return cookies;
    }

    public static void storeCookies(Context context, List<String> cookies) {
        SharedPreferences.Editor editor = getPreferences(context).edit();

        for (String cookie : cookies) {
            if (cookie.startsWith(PREFIX_TOKEN)) {
                editor.putString(PREFIX_TOKEN, cookie);
            } else if (cookie.startsWith(PREFIX_SECRET)) {
                editor.putString(PREFIX_SECRET, cookie);
            }
        }

        editor.apply();
    }

    public static boolean hasCookies(Context context) {
        return getCookies(context).size() == 2;
    }

    private static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
