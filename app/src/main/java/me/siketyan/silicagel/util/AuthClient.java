package me.siketyan.silicagel.util;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import me.siketyan.silicagel.R;
import me.siketyan.silicagel.activity.MastodonAuthActivity;
import me.siketyan.silicagel.activity.TwitterAuthActivity;
import me.siketyan.silicagel.task.MastodonAuthTask;
import me.siketyan.silicagel.task.TwitterAuthTask;

public class AuthClient extends WebViewClient {
    private Context context;

    public AuthClient(Context context) {
        this.context = context;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);

        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            activity.findViewById(R.id.progress).setVisibility(View.INVISIBLE);
            activity.findViewById(R.id.progress_wrapper).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        Uri uri = request.getUrl();
        String url = request.getUrl().toString();

        if (url.startsWith(TwitterAuthActivity.CALLBACK_URL)) {
            new TwitterAuthTask(
                context,
                uri.getQueryParameter("oauth_verifier")
            ).execute();
        } else if (url.startsWith(MastodonAuthActivity.CALLBACK_URL)) {
            new MastodonAuthTask(
                context,
                uri.getQueryParameter("code")
            ).execute();
        } else {
            return super.shouldOverrideUrlLoading(view, request);
        }

        return false;
    }
}