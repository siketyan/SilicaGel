package me.siketyan.silicagel.service;

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
import me.siketyan.silicagel.R;
import me.siketyan.silicagel.util.TwitterUtil;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class NotificationService extends NotificationListenerService {
    private static NotificationService instance;

    private static final int NOTIFICATION_ID = 114514;
    private static final String LOG_TAG = "SilicaGel";
    private static final String FILTER_CLOUDPLAYER = "com.doubleTwist.cloudPlayer";
    private static final String FILTER_PLAYMUSIC = "com.google.android.music";
    private static final String FILTER_SPOTIFY = "com.spotify.music";

    public static boolean isNotificationAccessEnabled = false;
    private String previous;

    public NotificationService() {
        instance = this;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();
        if (!packageName.equals(FILTER_CLOUDPLAYER) &&
            !packageName.equals(FILTER_PLAYMUSIC) &&
            !packageName.equals(FILTER_SPOTIFY)) return;

        try {
            final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            if (!pref.getBoolean("monitor_notifications", true)) return;

            final Bundle extras;
            String title = "";
            String artist = "";
            String album = "";
            extras = sbn.getNotification().extras;
            try {
                title = extras.getCharSequence(Notification.EXTRA_TITLE).toString();
                artist = extras.getCharSequence(Notification.EXTRA_TEXT).toString();
                album = extras.getCharSequence(Notification.EXTRA_SUB_TEXT).toString();
            } catch (NullPointerException e) {
                Log.d(LOG_TAG, "[Error] Empty title, artist or album was provided.");
            }

            Log.d(LOG_TAG, "[Playing] " + title + " - " + artist + " (" + album + ")");

            String tweetText = pref.getString("template", "")
                    .replaceAll("%title%", title)
                    .replaceAll("%artist%", artist)
                    .replaceAll("%album%", album);

            if (tweetText.equals(previous)) return;
            previous = tweetText;

            AsyncTask<String, Void, Boolean> task = new AsyncTask<String, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(String... params) {
                    try {
                        Twitter twitter = TwitterUtil.getTwitterInstance(getInstance());
                        ByteArrayInputStream bs = null;
                        if (pref.getBoolean("with_cover", false)) {
                            try {
                                Bitmap thumb = (Bitmap) extras.get(Notification.EXTRA_LARGE_ICON);
                                if (thumb == null)
                                    thumb = (Bitmap) extras.get(Notification.EXTRA_LARGE_ICON_BIG);

                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                thumb.compress(Bitmap.CompressFormat.PNG, 0, bos);

                                byte[] bitmap = bos.toByteArray();
                                bs = new ByteArrayInputStream(bitmap);
                            } catch (Exception e) {
                                notifyException(e);
                            }
                        }

                        if (bs != null) {
                            twitter.updateStatus(new StatusUpdate(params[0]).media("cover.png", bs));
                        } else {
                            twitter.updateStatus(params[0]);
                        }

                        Log.d(LOG_TAG, "[Tweeted] " + params[0]);
                        return true;
                    } catch (Exception e) {
                        notifyException(e);
                        e.printStackTrace();

                        Log.d(LOG_TAG, "[Error] Failed to tweet.");
                        return false;
                    }
                }
            };

            task.execute(tweetText);
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

    @Override
    public void onDestroy() {
        startService(new Intent(this, NotificationService.class));
    }

    private static void notifyException(Exception e){
        ((NotificationManager) getInstance().getSystemService(Context.NOTIFICATION_SERVICE))
            .notify(
                NOTIFICATION_ID,
                new Notification.Builder(getInstance())
                    .setSmallIcon(R.drawable.ic_error_black_24dp)
                    .setContentTitle("Error!")
                    .setContentText(e.toString())
                    .setStyle(new Notification.BigTextStyle().bigText(implode(e.getStackTrace(), "\n")))
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