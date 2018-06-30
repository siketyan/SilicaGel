package me.siketyan.silicagel.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.session.MediaSession;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import me.siketyan.silicagel.App;
import me.siketyan.silicagel.R;
import me.siketyan.silicagel.task.TootTask;
import me.siketyan.silicagel.task.TweetTask;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NotificationService extends NotificationListenerService {
    private static NotificationService instance;

    private static final int NOTIFICATION_ID = 114514;
    public static final String LOG_TAG = "SilicaGel";

    private static final Context APP = App.getContext();
    private static final Map<String, String> PLAYERS = new HashMap<String, String>() {
        {
            put("com.doubleTwist.cloudPlayer", APP.getString(R.string.cloudplayer));
            put("com.google.android.music", APP.getString(R.string.google_play_music));
            put("com.spotify.music", APP.getString(R.string.spotify));
            put("com.amazon.mp3", APP.getString(R.string.amazon));
            put("com.sonyericsson.music", APP.getString(R.string.sony));
            put("jp.co.aniuta.android.aniutaap", APP.getString(R.string.aniuta));
            put("com.soundcloud.android", APP.getString(R.string.soundcloud));
        }
    };

    public static boolean isNotificationAccessEnabled = false;
    private String previous;

    public NotificationService() {
        instance = this;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.d(LOG_TAG, "[Notification] " + sbn.getPackageName());
        String player = getPlayer(sbn.getPackageName());
        if (player == null) return;

        try {
            final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            if (!pref.getBoolean("monitor_notifications", true)) return;

            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);

            final Bundle extras = sbn.getNotification().extras;

            MediaSession.Token token = (MediaSession.Token) extras.get(Notification.EXTRA_MEDIA_SESSION);
            CharSequence titleSeq = extras.getCharSequence(Notification.EXTRA_TITLE);
            CharSequence artistSeq = extras.getCharSequence(Notification.EXTRA_TEXT);
            CharSequence albumSeq = extras.getCharSequence(Notification.EXTRA_SUB_TEXT);

            if (token == null) {
                Log.d(LOG_TAG, "[Skipped] There is no media session.");
                return;
            }

            String unknown = getString(R.string.unknown);
            String title = titleSeq != null ? titleSeq.toString() : unknown;
            String artist = artistSeq != null ? artistSeq.toString() : unknown;
            String album = albumSeq != null ? albumSeq.toString() : unknown;

            Log.d(LOG_TAG, "[Playing] " + title + " - " + artist + " (" + album + ") on " + player);

            String text = pref.getString("template", "")
                    .replaceAll("%title%", title)
                    .replaceAll("%artist%", artist)
                    .replaceAll("%album%", album)
                    .replaceAll("%player%", player);

            if (text.equals(previous)) return;
            previous = text;

            text = text
                    .replaceAll("%y%", String.format(Locale.ROOT, "%4d", year))
                    .replaceAll("%m%", String.format(Locale.ROOT, "%2d", month))
                    .replaceAll("%d%", String.format(Locale.ROOT, "%2d", day))
                    .replaceAll("%h%", String.format(Locale.ROOT, "%02d", hour))
                    .replaceAll("%i%", String.format(Locale.ROOT, "%02d", minute))
                    .replaceAll("%s%", String.format(Locale.ROOT, "%02d", second));

            byte[] bitmap = null;
            if (pref.getBoolean("with_cover", false)) {
                bitmap = getBitmap(extras);
            }

            new TweetTask(this, pref, text, bitmap).execute();
            new TootTask(this, pref, text, bitmap).execute();
        } catch (Exception e) {
            notifyException(this, e);
            e.printStackTrace();
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
        super.onDestroy();
        startService(new Intent(this, NotificationService.class));
    }

    private String getPlayer(String packageName) {
        for (String player : PLAYERS.keySet()) {
            if (!packageName.equals(player)) continue;
            return PLAYERS.get(player);
        }

        return null;
    }

    private byte[] getBitmap(Bundle extras) {
        try {
            Bitmap thumb = (Bitmap) extras.get(Notification.EXTRA_LARGE_ICON);

            if (thumb == null) {
                thumb = (Bitmap) extras.get(Notification.EXTRA_LARGE_ICON_BIG);
            }

            if (thumb != null) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                thumb.compress(Bitmap.CompressFormat.PNG, 0, bos);

                return bos.toByteArray();
            }
        } catch (Exception e) {
            notifyException(NotificationService.this, e);
            e.printStackTrace();
        }

        return null;
    }

    public static void notifyException(Context context, Exception e) {
        NotificationManager manager = (NotificationManager) getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null) return;

        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(getInstance(), App.NOTIFICATION_CHANNEL_ERROR);
        } else {
            builder = new Notification.Builder(getInstance());
        }

        manager.notify(
            NOTIFICATION_ID,
            builder
                .setSmallIcon(R.drawable.ic_error_black_24dp)
                .setContentTitle(context.getString(R.string.error))
                .setContentText(e.toString())
                .setStyle(new Notification.BigTextStyle().bigText(implodeStackTraces(e.getStackTrace())))
                .build()
        );
    }

    private static String implodeStackTraces(StackTraceElement[] list) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement e : list) {
            sb.append("\n").append("at ").append(e.getClassName());
        }

        return sb.substring(1);
    }

    private static NotificationService getInstance() {
        return instance;
    }
}