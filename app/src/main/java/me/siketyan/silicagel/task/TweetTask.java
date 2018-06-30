package me.siketyan.silicagel.task;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import me.siketyan.silicagel.R;
import me.siketyan.silicagel.service.NotificationService;
import me.siketyan.silicagel.util.TwitterUtil;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;

import java.io.ByteArrayInputStream;

public class TweetTask extends AsyncTask<Void, Void, Boolean> {
    private Context context;
    private SharedPreferences pref;
    private String text;
    private byte[] bitmap;

    public TweetTask(Context context, SharedPreferences pref, String text, byte[] bitmap) {
        this.context = context;
        this.pref = pref;
        this.text = text;
        this.bitmap = bitmap;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (!TwitterUtil.hasAccessToken(context)) return false;

        try {
            Twitter twitter = TwitterUtil.getTwitterInstance(context);
            ByteArrayInputStream bs = null;
            if (bitmap != null) {
                bs = new ByteArrayInputStream(bitmap);
            }

            if (bs != null) {
                twitter.updateStatus(new StatusUpdate(text).media("cover.png", bs));
            } else {
                twitter.updateStatus(text);
            }

            Log.d(NotificationService.LOG_TAG, "[Tweeted] " + text);
            return true;
        } catch (Exception e) {
            NotificationService.notifyException(context, e);
            e.printStackTrace();

            Log.d(NotificationService.LOG_TAG, "[Error] Failed to tweet.");
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (pref.getBoolean("notify_posted", true) && result) {
            Toast.makeText(context, R.string.tweeted, Toast.LENGTH_SHORT)
                .show();
        }
    }
}