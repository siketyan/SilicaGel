package me.siketyan.silicagel.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import me.siketyan.silicagel.App;
import me.siketyan.silicagel.R;
import me.siketyan.silicagel.model.Media;
import me.siketyan.silicagel.task.SocialProxyBroadcastTask;
import me.siketyan.silicagel.util.Logger;
import me.siketyan.silicagel.util.TemplateParser;

import java.util.HashMap;
import java.util.Map;

public class NotificationService extends NotificationListenerService {
    private static final int NOTIFICATION_ID = 114514;

    public static boolean isNotificationAccessEnabled = false;
    private Map<String, String> players;
    private Media previous;

    public NotificationService() {
        players = new HashMap<>();
        players.put("com.doubleTwist.cloudPlayer", getString(R.string.cloudplayer));
        players.put("com.google.android.music", getString(R.string.google_play_music));
        players.put("com.spotify.music", getString(R.string.spotify));
        players.put("com.amazon.mp3", getString(R.string.amazon));
        players.put("com.sonyericsson.music", getString(R.string.sony));
        players.put("jp.co.aniuta.android.aniutaap", getString(R.string.aniuta));
        players.put("com.soundcloud.android", getString(R.string.soundcloud));
        players.put("com.apple.android.music", getString(R.string.apple));
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Logger.debug("Notification posted from " + sbn.getPackageName());
        String player = getPlayer(sbn.getPackageName());
        if (player == null) return;

        try {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            if (!pref.getBoolean("monitor_notifications", true)) return;

            Media media = Media.create(this, sbn.getNotification());
            if (media == null || media.equals(previous)) return;
            previous = media;

            String template = pref.getString("template", "");
            String text = TemplateParser.parse(template, media, player);

            new SocialProxyBroadcastTask(this, pref, text).execute();
        } catch (Exception e) {
            notifyException(this, e);
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent i) {
        IBinder binder = super.onBind(i);
        Logger.info("Enabled notification service.");
        isNotificationAccessEnabled = true;
        return binder;
    }

    @Override
    public boolean onUnbind(Intent i) {
        boolean onUnbind = super.onUnbind(i);
        Logger.info("Disabled notification service.");
        isNotificationAccessEnabled = false;
        return onUnbind;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        startService(new Intent(this, NotificationService.class));
    }

    private String getPlayer(String packageName) {
        for (String player : players.keySet()) {
            if (!packageName.equals(player)) continue;
            return players.get(player);
        }

        return null;
    }

    @SuppressWarnings("deprecation")
    public static void notifyException(Context context, Exception e) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null) return;

        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(context, App.NOTIFICATION_CHANNEL_ERROR);
        } else {
            builder = new Notification.Builder(context);
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
}