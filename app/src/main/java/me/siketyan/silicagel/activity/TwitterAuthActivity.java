package me.siketyan.silicagel.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import me.siketyan.silicagel.R;
import me.siketyan.silicagel.task.TwitterAuthTask;

public class TwitterAuthActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
        setContentView(R.layout.activity_authorizing);
        
        Intent intent = getIntent();
        if (intent == null || intent.getData() == null
            || !intent.getData().toString().startsWith(SettingsActivity.TWITTER_CALLBACK_URL)) {
            return;
        }

        new TwitterAuthTask(
            this,
            intent.getData().getQueryParameter("oauth_verifier")
        ).execute();
    }
}