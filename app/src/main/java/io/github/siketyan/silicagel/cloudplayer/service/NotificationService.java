package io.github.siketyan.silicagel.cloudplayer.service;

import android.app.Notification;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import io.github.siketyan.silicagel.cloudplayer.util.TwitterUtil;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class NotificationService extends NotificationListenerService {
    private static final String PACKAGE_FILTER = "com.doubleTwist.cloudPlayer";
    private String previous;
    
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (!sbn.getPackageName().equals(PACKAGE_FILTER)) return;
        
        final Bundle extras = sbn.getNotification().extras;
        String title = extras.getCharSequence(Notification.EXTRA_TITLE).toString();
        String artist = extras.getCharSequence(Notification.EXTRA_TEXT).toString();
        String album = extras.getCharSequence(Notification.EXTRA_SUB_TEXT).toString();
    
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String tweetText = pref.getString("template", "")
                               .replaceAll("%title%", title)
                               .replaceAll("%artist%", artist)
                               .replaceAll("%album%", album);
        
        if (tweetText.equals(previous)) return;
        previous = tweetText;
    
        final Twitter twitter = TwitterUtil.getTwitterInstance(this);
        AsyncTask<String, Void, Boolean> task = new AsyncTask<String, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    if (pref.getBoolean("with_cover", false)) {
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        ((Bitmap)extras.get(Notification.EXTRA_LARGE_ICON))
                                       .compress(Bitmap.CompressFormat.PNG, 0, bos);
                        
                        byte[] bitmap = bos.toByteArray();
                        ByteArrayInputStream bs = new ByteArrayInputStream(bitmap);
                        
                        twitter.updateStatus(new StatusUpdate(params[0]).media("cover.png", bs));
                    } else {
                        twitter.updateStatus(params[0]);
                    }
                    
                    return true;
                } catch (TwitterException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        };
        
        task.execute(tweetText);
    }
}