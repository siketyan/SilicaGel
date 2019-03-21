package me.siketyan.silicagel.task;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.webkit.CookieManager;
import android.webkit.WebView;
import me.siketyan.silicagel.activity.SocialProxyActivity;
import me.siketyan.silicagel.util.SocialProxyClient;
import me.siketyan.silicagel.util.SocialProxyUtil;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class SocialProxyInitTask extends AsyncTask<Void, Void, Void> {
    private WeakReference<Context> contextRef;
    private WeakReference<WebView> webViewRef;

    public SocialProxyInitTask(Context context, WebView webView) {
        this.contextRef = new WeakReference<>(context);
        this.webViewRef = new WeakReference<>(webView);
    }

    @Override
    protected Void doInBackground(Void... params) {
        Context context = contextRef.get();
        if (SocialProxyUtil.hasCookies(context)) {
            return null;
        }

        try {
            URL url = new URL(SocialProxyActivity.API_NEW_URL);
            URLConnection conn = url.openConnection();
            List<String> cookies = conn.getHeaderFields().get("Set-Cookie");
            SocialProxyUtil.storeCookies(context, cookies);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onPostExecute(Void v) {
        Context context = contextRef.get();
        WebView webView = webViewRef.get();

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);
        cookieManager.removeAllCookies(null);

        List<String> cookies = SocialProxyUtil.getCookies(context);
        for (String cookie : cookies) {
            cookieManager.setCookie(
                SocialProxyActivity.MANAGE_URL,
                cookie
            );
        }

        cookieManager.flush();

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new SocialProxyClient(context));
        webView.loadUrl(SocialProxyActivity.MANAGE_URL);
    }
}
