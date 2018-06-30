package me.siketyan.silicagel.task;

import android.content.Context;
import android.os.AsyncTask;
import android.webkit.WebView;
import com.sys1yagi.mastodon4j.MastodonClient;
import com.sys1yagi.mastodon4j.api.Scope;
import com.sys1yagi.mastodon4j.api.entity.auth.AppRegistration;
import com.sys1yagi.mastodon4j.api.exception.Mastodon4jRequestException;
import com.sys1yagi.mastodon4j.api.method.Apps;
import me.siketyan.silicagel.activity.MastodonAuthActivity;
import me.siketyan.silicagel.util.AuthClient;
import me.siketyan.silicagel.util.MastodonUtil;

public class MastodonAuthRequestTask extends AsyncTask<Void, Void, String> {
    private Context context;
    private WebView webView;

    public MastodonAuthRequestTask(Context context, WebView webView) {
        this.context = context;
        this.webView = webView;
    }

    @Override
    protected String doInBackground(Void... params) {
        MastodonClient client = MastodonUtil.getClient(context, false);
        Apps apps = new Apps(client);

        try {
            AppRegistration appRegistration = apps.createApp(
                MastodonAuthActivity.APP_NAME,
                MastodonAuthActivity.CALLBACK_URL,
                new Scope(Scope.Name.WRITE),
                MastodonAuthActivity.WEBSITE
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
        if (uri != null) {
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new AuthClient(context));
            webView.loadUrl(uri);
        }
    }
}