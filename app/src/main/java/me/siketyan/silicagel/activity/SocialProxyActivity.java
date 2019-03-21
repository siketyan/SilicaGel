package me.siketyan.silicagel.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import me.siketyan.silicagel.R;
import me.siketyan.silicagel.task.SocialProxyInitTask;

public class SocialProxyActivity extends Activity {
    public static final String API_NEW_URL = "https://sproxy.sikeserver.com/api/new";
    public static final String API_BROADCAST_URL = "https://sproxy.sikeserver.com/api/broadcast";
    public static final String MANAGE_URL = "https://sproxy.sikeserver.com/manage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Social Proxy");
        setTheme(android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
        setContentView(R.layout.activity_social_proxy);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        WebView webView = findViewById(R.id.webview);
        new SocialProxyInitTask(this, webView).execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != android.R.id.home) {
            return super.onOptionsItemSelected(item);
        }

        finish();
        return true;
    }
}
