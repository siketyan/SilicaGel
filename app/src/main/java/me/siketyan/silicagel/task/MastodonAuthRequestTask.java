package me.siketyan.silicagel.task;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import com.sys1yagi.mastodon4j.MastodonClient;
import com.sys1yagi.mastodon4j.api.Scope;
import com.sys1yagi.mastodon4j.api.entity.auth.AppRegistration;
import com.sys1yagi.mastodon4j.api.exception.Mastodon4jRequestException;
import com.sys1yagi.mastodon4j.api.method.Apps;
import me.siketyan.silicagel.activity.SettingsActivity;
import me.siketyan.silicagel.util.MastodonUtil;

public class MastodonAuthRequestTask extends AsyncTask<Void, Void, String> {
    private Context context;

    public MastodonAuthRequestTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... params) {
        MastodonClient client = MastodonUtil.getClient(context, false);
        Apps apps = new Apps(client);

        try {
            AppRegistration appRegistration = apps.createApp(
                SettingsActivity.MASTODON_APP_NAME,
                SettingsActivity.MASTODON_CALLBACK_URL,
                new Scope(Scope.Name.WRITE),
                SettingsActivity.MASTODON_WEBSITE
            ).execute();
            MastodonUtil.storeAppRegistration(context, appRegistration);

            return apps.getOAuthUrl(
                appRegistration.getClientId(),
                new Scope(Scope.Name.WRITE),
                appRegistration.getRedirectUri()
            );
        } catch (Mastodon4jRequestException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        context.startActivity(intent);
    }
}