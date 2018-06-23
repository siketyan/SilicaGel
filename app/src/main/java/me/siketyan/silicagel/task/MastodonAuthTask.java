package me.siketyan.silicagel.task;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;
import com.sys1yagi.mastodon4j.MastodonClient;
import com.sys1yagi.mastodon4j.api.entity.auth.AccessToken;
import com.sys1yagi.mastodon4j.api.exception.Mastodon4jRequestException;
import com.sys1yagi.mastodon4j.api.method.Apps;
import me.siketyan.silicagel.R;
import me.siketyan.silicagel.activity.SettingsActivity;
import me.siketyan.silicagel.util.MastodonUtil;

public class MastodonAuthTask extends AsyncTask<Void, Void, AccessToken> {
    private Context context;
    private String authCode;

    public MastodonAuthTask(Context context, String authCode) {
        this.context = context;
        this.authCode = authCode;
    }

    @Override
    protected AccessToken doInBackground(Void... params) {
        try {
            MastodonClient client = MastodonUtil.getClient(context, false);
            Apps apps = new Apps(client);

            return apps.getAccessToken(
                MastodonUtil.getClientId(context),
                MastodonUtil.getClientSecret(context),
                MastodonUtil.getRedirectUri(context),
                authCode,
                "authorization_code"
            ).execute();
        } catch (Mastodon4jRequestException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(com.sys1yagi.mastodon4j.api.entity.auth.AccessToken accessToken) {
        if (accessToken != null) {
            showToast(context.getString(R.string.authorized));
            MastodonUtil.storeApp(
                SettingsActivity.getContext(),
                MastodonUtil.getInstanceName(context),
                accessToken.getAccessToken()
            );
        } else {
            showToast(context.getString(R.string.auth_failed));
        }

        context.startActivity(new Intent(context, SettingsActivity.class));
        if (context instanceof Activity) {
            ((Activity) context).finish();
        }
    }

    private static void showToast(String text) {
        Toast.makeText(SettingsActivity.getContext(), text, Toast.LENGTH_SHORT).show();
    }
}