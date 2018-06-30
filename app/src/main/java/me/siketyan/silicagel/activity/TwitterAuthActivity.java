package me.siketyan.silicagel.activity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import me.siketyan.silicagel.R;
import me.siketyan.silicagel.task.TwitterAuthRequestTask;

public class TwitterAuthActivity extends Activity {
    public static final String CALLBACK_URL = "https://callback.sikeserver.com/silicagel/twitter";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.authorizing_twitter);
        setTheme(android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
        setContentView(R.layout.activity_authorizing);

        WebView webView = findViewById(R.id.webview);
        new TwitterAuthRequestTask(this, webView).execute();
    }
}