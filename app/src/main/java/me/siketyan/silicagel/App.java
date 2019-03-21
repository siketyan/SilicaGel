package me.siketyan.silicagel;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

public class App extends Application {
    public static final String NOTIFICATION_CHANNEL_ERROR = "error";

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null) return;

        NotificationChannel channel = new NotificationChannel(
            NOTIFICATION_CHANNEL_ERROR,
            getString(R.string.error),
            NotificationManager.IMPORTANCE_DEFAULT
        );

        channel.enableLights(true);
        channel.setLightColor(Color.RED);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        manager.createNotificationChannel(channel);
    }
}