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
import android.widget.Toast;

import com.google.gson.Gson;
import com.sys1yagi.mastodon4j.MastodonClient;
import com.sys1yagi.mastodon4j.api.entity.Attachment;
import com.sys1yagi.mastodon4j.api.entity.Status;
import com.sys1yagi.mastodon4j.api.method.Media;
import com.sys1yagi.mastodon4j.api.method.Statuses;

import me.siketyan.silicagel.App;
import me.siketyan.silicagel.R;
import me.siketyan.silicagel.util.MastodonUtil;
import me.siketyan.silicagel.util.TwitterUtil;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationService extends NotificationListenerService {
    private static NotificationService instance;

    private static final int NOTIFICATION_ID = 114514;
    private static final String LOG_TAG = "SilicaGel";

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

    private MastodonClient client;
    private Statuses statuses;
    private String instanceName;
    private Media postMedia;
    private int privacy;

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

            Log.d(LOG_TAG, "[Playing] " + title + " - " + artist + " (" + album + ") on " + player);

            String tweetText = pref.getString("template", "")
                    .replaceAll("%title%", title)
                    .replaceAll("%artist%", artist)
                    .replaceAll("%album%", album)
                    .replaceAll("%player%", player);

            if (tweetText.equals(previous)) return;
            previous = tweetText;

            tweetText = tweetText
                    .replaceAll("%y%", String.format("%4d", year))
                    .replaceAll("%m%", String.format("%2d", month))
                    .replaceAll("%d%", String.format("%2d", day))
                    .replaceAll("%h%", String.format("%02d", hour))
                    .replaceAll("%i%", String.format("%02d", minute))
                    .replaceAll("%s%", String.format("%02d", second));
            Log.d(LOG_TAG, "[Tweeting] " + tweetText);

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
                                notifyException(NotificationService.this, e);
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
                        notifyException(NotificationService.this, e);
                        e.printStackTrace();

                        Log.d(LOG_TAG, "[Error] Failed to tweet.");
                        return false;
                    }
                }

                @Override
                protected void onPostExecute(Boolean b) {
                    if (pref.getBoolean("notify_tweeted", true)) {
                        Toast.makeText(NotificationService.this, R.string.tweeted, Toast.LENGTH_SHORT)
                             .show();
                    }
                }
            };

            task.execute(tweetText);

            AsyncTask<String, Void, Boolean> tootTask = new AsyncTask<String, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(String... params) {
                    try {
                        String privacy_value = pref.getString("privacy", "0");
                        int privacy_i = Integer.parseInt(privacy_value);
                        switch (privacy_i) {
                            case 0:
                                privacy = 0;
                                break;
                            case 1:
                                privacy = 1;
                                break;
                            case 2:
                                privacy = 2;
                                break;
                            case 3:
                                privacy = 3;
                                break;
                        }

                        instanceName = MastodonUtil.getInstanceName(NotificationService.this);
                        String accessToken = MastodonUtil.loadAccessToken(NotificationService.this);
                        client = new MastodonClient.Builder(instanceName, new OkHttpClient.Builder(), new Gson())
                                .accessToken(accessToken)
                                .build();
                        statuses = MastodonUtil.getStatuses(client);
                        postMedia = MastodonUtil.getMedia(client);

                        byte[] bitmap = null;
                        if (pref.getBoolean("with_cover", false)) {
                            try {
                                Bitmap thumb = (Bitmap) extras.get(Notification.EXTRA_LARGE_ICON);
                                if (thumb == null)
                                    thumb = (Bitmap) extras.get(Notification.EXTRA_LARGE_ICON_BIG);

                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                thumb.compress(Bitmap.CompressFormat.PNG, 0, bos);

                                bitmap = bos.toByteArray();
                            } catch (Exception e) {
                                notifyException(NotificationService.this, e);
                            }
                        }

                        if (bitmap != null) {
                            List<Long> media_id = new ArrayList<>();
                            MultipartBody.Part image = MultipartBody.Part.createFormData(
                                    "file",
                                    "cover.png",
                                    RequestBody.create(MediaType.parse("image/jpeg"), bitmap));
                            Attachment attachment = postMedia.postMedia(image).execute();
                            long image_id = attachment.getId();
                            media_id.add(0, image_id);
                            switch (privacy) {
                                case 0:
                                    statuses.postStatus(params[0], null, media_id, false, null, com.sys1yagi.mastodon4j.api.entity.Status.Visibility.Public).execute();
                                    break;
                                case 1:
                                    statuses.postStatus(params[0], null, media_id, false, null, com.sys1yagi.mastodon4j.api.entity.Status.Visibility.Unlisted).execute();
                                    break;
                                case 2:
                                    statuses.postStatus(params[0], null, media_id, false, null, com.sys1yagi.mastodon4j.api.entity.Status.Visibility.Private).execute();
                                    break;
                                case 3:
                                    statuses.postStatus(params[0], null, media_id, false, null, com.sys1yagi.mastodon4j.api.entity.Status.Visibility.Direct).execute();
                                    break;
                            }
                        } else {
                            switch (privacy) {
                                case 0:
                                    statuses.postStatus(params[0], null, null, false, null, com.sys1yagi.mastodon4j.api.entity.Status.Visibility.Public).execute();
                                    break;
                                case 1:
                                    statuses.postStatus(params[0], null, null, false, null, com.sys1yagi.mastodon4j.api.entity.Status.Visibility.Unlisted).execute();
                                    break;
                                case 2:
                                    statuses.postStatus(params[0], null, null, false, null, com.sys1yagi.mastodon4j.api.entity.Status.Visibility.Private).execute();
                                    break;
                                case 3:
                                    statuses.postStatus(params[0], null, null, false, null, com.sys1yagi.mastodon4j.api.entity.Status.Visibility.Direct).execute();
                                    break;
                            }
                        }

                        Log.d(LOG_TAG, "[Tooted] " + params[0]);
                        return true;
                    } catch (Exception e) {
                        notifyException(NotificationService.this, e);
                        e.printStackTrace();

                        Log.d(LOG_TAG, "[Error] Failed to toot.");
                        return false;
                    }
                }

                @Override
                protected void onPostExecute(Boolean b) {
                    if (pref.getBoolean("notify_tweeted", true)) {
                        Toast.makeText(NotificationService.this, R.string.tooted, Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            };

            tootTask.execute(tweetText);

        } catch (Exception e) {
            notifyException(this, e);
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

    private static void notifyException(Context context, Exception e) {
        ((NotificationManager) getInstance().getSystemService(Context.NOTIFICATION_SERVICE))
            .notify(
                NOTIFICATION_ID,
                new Notification.Builder(getInstance())
                    .setSmallIcon(R.drawable.ic_error_black_24dp)
                    .setContentTitle(context.getString(R.string.error))
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