package me.siketyan.silicagel.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import me.siketyan.silicagel.R;

public class SocialProxyClient extends WebViewClient {
    private Context context;

    public SocialProxyClient(Context context) {
        this.context = context;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (!(context instanceof Activity)) return;

        Activity activity = (Activity) context;
        activity.findViewById(R.id.progress).setVisibility(View.INVISIBLE);
        activity.findViewById(R.id.progress_wrapper).setVisibility(View.INVISIBLE);
    }
}