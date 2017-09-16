package io.github.siketyan.silicagel.cloudplayer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import io.github.siketyan.silicagel.cloudplayer.R;
import io.github.siketyan.silicagel.cloudplayer.util.TwitterUtil;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class NotificationService extends NotificationListenerService {
    private static NotificationService instance;

    private static final int NOTIFICATION_ID = 114514;
    private static final String LOG_TAG = "SGfCP";
    private static final String PACKAGE_FILTER = "com.doubleTwist.cloudPlayer";

    public static boolean isNotificationAccessEnabled = false;
    private String previous;

    public NotificationService() {
        instance = this;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (!sbn.getPackageName().equals(PACKAGE_FILTER)) return;

        try {
            final Bundle extras;
            String title, artist, album;
            try {
                extras = sbn.getNotification().extras;
                title = extras.getCharSequence(Notification.EXTRA_TITLE).toString();
                artist = extras.getCharSequence(Notification.EXTRA_TEXT).toString();
                album = extras.getCharSequence(Notification.EXTRA_SUB_TEXT).toString();
            } catch (NullPointerException e) {
                Log.d(LOG_TAG, "[Error] Empty title, artist or album was provided.");
                return;
            }

            Log.d(LOG_TAG, "[Playing] " + title + " - " + artist + " (" + album + ")");

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
                        ByteArrayInputStream bs = null;
                        if (pref.getBoolean("with_cover", false)) {
                            try {
                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                ((Bitmap) extras.get(Notification.EXTRA_LARGE_ICON))
                                    .compress(Bitmap.CompressFormat.PNG, 0, bos);

                                byte[] bitmap = bos.toByteArray();
                                bs = new ByteArrayInputStream(bitmap);
                            } catch (Exception ignored) {}
                        }

                        if (bs != null) {
                            twitter.updateStatus(new StatusUpdate(params[0]).media("cover.png", bs));
                        } else {
                            twitter.updateStatus(params[0]);
                        }

                        return true;
                    } catch (TwitterException e) {
                        notifyException(e);
                        return false;
                    }
                }
            };

            task.execute(tweetText);
            if (task.get()) {
                Log.d(LOG_TAG, "[Tweeted] " + tweetText);
            } else {
                Log.d(LOG_TAG, "[Error] Failed to tweet.");
            }
        } catch (Exception e) {
            notifyException(e);
        }
    }

    @Override
    public IBinder onBind(Intent i) {
        IBinder binder = super.onBind(i);
        Log.d(LOG_TAG, "[Service] Enabled notification access.");
        isNotificationAccessEnabled = true;
        return binder;
    }

    @Override
    public boolean onUnbind(Intent i) {
        boolean onUnbind = super.onUnbind(i);
        Log.d(LOG_TAG, "[Service] Disabled notification access.");
        isNotificationAccessEnabled = false;
        return onUnbind;
    }

    private static void notifyException(Exception e){
        ((NotificationManager) getInstance().getSystemService(Context.NOTIFICATION_SERVICE))
            .notify(
                NOTIFICATION_ID,
                new Notification.Builder(getInstance())
                    .setSmallIcon(R.drawable.ic_error_black_24dp)
                    .setContentTitle(e.toString())
                    .setContentText(implode(e.getStackTrace(), "\n"))
                    .build()
            );
    }

    private static String implode(StackTraceElement[] list, String glue) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement e : list) {
            sb.append(glue).append("at ").append(e.getClassName());
        }

        return sb.substring(glue.length());
    }

    private static NotificationService getInstance() {
        return instance;
    }
}