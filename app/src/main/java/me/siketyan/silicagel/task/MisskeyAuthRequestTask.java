package me.siketyan.silicagel.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import me.siketyan.silicagel.misskeyapi.MisskeyClient;
import me.siketyan.silicagel.misskeyapi.api.endpoints.Apps;
import me.siketyan.silicagel.misskeyapi.api.entities.auth.Token;
import me.siketyan.silicagel.misskeyapi.api.exception.MisskeyRequestException;
import me.siketyan.silicagel.util.AuthClient;
import me.siketyan.silicagel.util.MisskeyUtil;

public class MisskeyAuthRequestTask extends AsyncTask<Void, Void, String> {
    private Context context;
    private WebView webView;

    public MisskeyAuthRequestTask(Context context, WebView webView) {
        this.context = context;
        this.webView = webView;
    }

    @Override
    protected String doInBackground(Void... params) {
        MisskeyClient client = MisskeyUtil.getClient(context);
        Apps apps = new Apps(client);
        try {
            Log.d("AppSecret", MisskeyUtil.getClientSecret(context));
            String AppSecret = MisskeyUtil.getClientSecret(context);
            Token token = apps.getAuthToken(AppSecret).execute();
            Log.d("Token", token.getToken());
            MisskeyUtil.storeSessionToken(context, token.getToken());
            Log.d("URL", token.getUrl());
            return token.getUrl();
        } catch (MisskeyRequestException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String uri) {
        if (uri != null) {
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.setWebViewClient(new AuthClient(context));
            webView.loadUrl(uri);
        }
    }
}