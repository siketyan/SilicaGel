package me.siketyan.silicagel.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class TwitterUtil {
    private static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static Twitter getTwitterInstance(Context context) {
        TwitterFactory factory = new TwitterFactory();
        Twitter twitter = factory.getInstance();
        twitter.setOAuthConsumer(TwitterApi.CONSUMER_KEY, TwitterApi.CONSUMER_SECRET);
        
        if (hasAccessToken(context)) {
            twitter.setOAuthAccessToken(loadAccessToken(context));
        }
        
        return twitter;
    }

    public static void storeRequestToken(Context context, RequestToken requestToken) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString("twitter_request_token", requestToken.getToken());
        editor.putString("twitter_request_secret", requestToken.getTokenSecret());
        editor.apply();
    }
    
    public static void storeAccessToken(Context context, AccessToken accessToken) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString("twitter_token", accessToken.getToken());
        editor.putString("twitter_secret", accessToken.getTokenSecret());
        editor.apply();
    }

    public static void deleteAccessToken(Context context) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.remove("twitter_token");
        editor.remove("twitter_secret");
        editor.apply();
    }

    public static RequestToken loadRequestToken(Context context) {
        SharedPreferences pref = getPreferences(context);
        String token = pref.getString("twitter_request_token", null);
        String tokenSecret = pref.getString("twitter_request_secret", null);

        if (token != null && tokenSecret != null) {
            return new RequestToken(token, tokenSecret);
        } else {
            return null;
        }
    }
    
    private static AccessToken loadAccessToken(Context context) {
        SharedPreferences pref = getPreferences(context);
        String token = pref.getString("twitter_token", null);
        String tokenSecret = pref.getString("twitter_secret", null);
        
        if (token != null && tokenSecret != null) {
            return new AccessToken(token, tokenSecret);
        } else {
            return null;
        }
    }

    public static boolean hasAccessToken(Context context) {
        return loadAccessToken(context) != null;
    }
}