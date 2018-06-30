package me.siketyan.silicagel.activity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import me.siketyan.silicagel.R;
import me.siketyan.silicagel.task.MastodonAuthRequestTask;

public class MastodonAuthActivity extends Activity {
    public static final String CALLBACK_URL = "silicagel://mastodon";
    public static final String APP_NAME = "SilicaGel";
    public static final String WEBSITE = "https://github.com/Siketyan/SilicaGel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.authorizing_mastodon);
        setTheme(android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
        setContentView(R.layout.activity_authorizing);

        WebView webView = findViewById(R.id.webview);
        new MastodonAuthRequestTask(this, webView).execute();
    }
}