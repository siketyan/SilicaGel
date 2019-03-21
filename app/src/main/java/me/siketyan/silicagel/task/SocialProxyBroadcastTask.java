package me.siketyan.silicagel.task;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;
import me.siketyan.silicagel.R;
import me.siketyan.silicagel.activity.SocialProxyActivity;
import me.siketyan.silicagel.service.NotificationService;
import me.siketyan.silicagel.util.Logger;
import me.siketyan.silicagel.util.SocialProxyUtil;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.lang.ref.WeakReference;

public class SocialProxyBroadcastTask extends AsyncTask<Void, Void, Boolean> {
    private WeakReference<Context> contextRef;
    private SharedPreferences pref;
    private String text;

    public SocialProxyBroadcastTask(Context context, SharedPreferences pref, String text) {
        this.contextRef = new WeakReference<>(context);
        this.pref = pref;
        this.text = text;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Context context = contextRef.get();
        if (!SocialProxyUtil.hasCookies(context)) {
            return false;
        }

        try {
            new OkHttpClient()
                .newCall(
                    new Request.Builder()
                        .url(SocialProxyActivity.API_BROADCAST_URL)
                        .header("Cookie", SocialProxyUtil.getSendCookie(context))
                        .post(RequestBody.create(MediaType.get("text/plain"), text))
                        .build()
                ).execute();
        } catch (Exception e) {
            NotificationService.notifyException(context, e);
            e.printStackTrace();

            Logger.error("Failed to broadcast.");
            return false;
        }

        Logger.info("Broadcasted: " + text);
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (!pref.getBoolean("notify_posted", true) || !result) return;

        Context context = contextRef.get();
        Toast
            .makeText(context, R.string.broadcasted, Toast.LENGTH_SHORT)
            .show();
    }
}
