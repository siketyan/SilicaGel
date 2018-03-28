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
import me.siketyan.silicagel.activity.SettingsActivity;
import me.siketyan.silicagel.util.TwitterUtil;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;

public class NotificationService extends NotificationListenerService {
    private static NotificationService instance;

    private static final SettingsActivity settingActivity = SettingsActivity.getContext();

    private static final int NOTIFICATION_ID = 114514;
    private static final String LOG_TAG = "SilicaGel";
    private static final String FILTER_CLOUDPLAYER = "com.doubleTwist.cloudPlayer";
    private static final String FILTER_PLAYMUSIC = "com.google.android.music";
    private static final String FILTER_SPOTIFY = "com.spotify.music";
    private static final String FILTER_AMAZON = "com.amazon.mp3";

    private static final HashMap<String, String> PLAYER_LIST = new HashMap<String, String>() {
        {
            put(FILTER_CLOUDPLAYER, settingActivity.getString(R.string.cloudplayer));
            put(FILTER_PLAYMUSIC, settingActivity.getString(R.string.google_play_music));
            put(FILTER_SPOTIFY, settingActivity.getString(R.string.spotify));
            put(FILTER_AMAZON, settingActivity.getString(R.string.amazon));
        }
    };

    public static boolean isNotificationAccessEnabled = false;
    private String previous;

    public NotificationService() {
        instance = this;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        Log.d(LOG_TAG, "onNotificationPosted");
        String packageName = sbn.getPackageName();
        if (!packageName.equals(FILTER_CLOUDPLAYER) &&
            !packageName.equals(FILTER_PLAYMUSIC) &&
            !packageName.equals(FILTER_SPOTIFY) &&
            !packageName.equals(FILTER_AMAZON)) return;

        try {
            final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            if (!pref.getBoolean("monitor_notifications", true)) return;

            final Bundle extras = sbn.getNotification().extras;
            String title = "";
            String artist = "";
            String album = "";

            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);

            try {
                title = extras.getCharSequence(Notification.EXTRA_TITLE).toString();
                artist = extras.getCharSequence(Notification.EXTRA_TEXT).toString();
                album = extras.getCharSequence(Notification.EXTRA_SUB_TEXT).toString();
            } catch (NullPointerException e) {
                Log.d(LOG_TAG, "[Error] Empty title, artist or album was provided.");
            }

            if(title == null || title.isEmpty()) return;

            Log.d(LOG_TAG, "[Playing] " + title + " - " + artist + " (" + album + ")");

            String tweetText = pref.getString("template", "")
                    .replaceAll("%title%", title)
                    .replaceAll("%artist%", artist)
                    .replaceAll("%album%", album)
                    .replaceAll("%player%", PLAYER_LIST.get(packageName));

            if (tweetText.equals(previous)) return;
            previous = tweetText;

            tweetText = tweetText
                    .replaceAll("%y%", String.format("%4d", year))
                    .replaceAll("%m%", String.format("%2d", month))
                    .replaceAll("%d%", String.format("%2d", day))
                    .replaceAll("%h%", String.format("%02d", hour))
                    .replaceAll("%i%", String.format("%02d", minute))
                    .replaceAll("%s%", String.format("%02d", second));
            Log.d("tweetText", tweetText);

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