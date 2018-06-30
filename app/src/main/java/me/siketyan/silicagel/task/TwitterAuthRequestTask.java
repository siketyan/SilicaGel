package me.siketyan.silicagel.task;

import android.content.Context;
import android.os.AsyncTask;
import android.webkit.WebView;
import me.siketyan.silicagel.activity.TwitterAuthActivity;
import me.siketyan.silicagel.util.AuthClient;
import me.siketyan.silicagel.util.TwitterUtil;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.RequestToken;

public class TwitterAuthRequestTask extends AsyncTask<Void, Void, String> {
    private Context context;
    private WebView webView;

    public TwitterAuthRequestTask(Context context, WebView webView) {
        this.context = context;
        this.webView = webView;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            Twitter twitter = TwitterUtil.getTwitterInstance(context);
            RequestToken request = twitter.getOAuthRequestToken(TwitterAuthActivity.CALLBACK_URL);
            TwitterUtil.storeRequestToken(context, request);

            return request.getAuthorizationURL();
        } catch (TwitterException e) {
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