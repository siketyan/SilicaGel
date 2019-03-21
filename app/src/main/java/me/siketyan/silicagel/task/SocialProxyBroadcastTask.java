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

public class SocialProxyBroadcastTask extends AsyncTask<Void, Void, Boolean> {
    private Context context;
    private SharedPreferences pref;
    private String text;

    public SocialProxyBroadcastTask(Context context, SharedPreferences pref, String text) {
        this.context = context;
        this.pref = pref;
        this.text = text;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (!SocialProxyUtil.hasCookies(context)) {
            return false;
        }

        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                .url(SocialProxyActivity.API_BROADCAST_URL)
                .header("Cookie", SocialProxyUtil.getSendCookie(context))
                .post(RequestBody.create(MediaType.get("text/plain"), text))
                .build();

            client.newCall(request).execute();
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
        if (pref.getBoolean("notify_posted", true) && result) {
            Toast.makeText(context, R.string.tweeted, Toast.LENGTH_SHORT)
                .show();
        }
    }
}
