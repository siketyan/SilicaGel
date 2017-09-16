package me.siketyan.silicagel.cloudplayer.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import me.siketyan.silicagel.cloudplayer.R;
import me.siketyan.silicagel.cloudplayer.activity.SettingsActivity;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class TwitterUtil {
    public static Twitter getTwitterInstance(Context context) {
        TwitterFactory factory = new TwitterFactory();
        Twitter twitter = factory.getInstance();
        twitter.setOAuthConsumer(TwitterApi.CONSUMER_KEY, TwitterApi.CONSUMER_SECRET);
        
        if (hasAccessToken(context)) {
            twitter.setOAuthAccessToken(loadAccessToken(context));
        }
        
        return twitter;
    }
    
    public static void storeAccessToken(Context context, AccessToken accessToken) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("twitter_token", accessToken.getToken());
        editor.putString("twitter_secret", accessToken.getTokenSecret());
        editor.apply();
    }
    
    private static AccessToken loadAccessToken(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
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