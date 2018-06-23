package me.siketyan.silicagel.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import me.siketyan.silicagel.R;
import me.siketyan.silicagel.task.MastodonAuthTask;

public class MastodonAuthActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
        setContentView(R.layout.activity_authorizing);

        Intent intent = this.getIntent();
        if (intent == null || intent.getData() == null
            || !intent.getData().toString().startsWith(SettingsActivity.MASTODON_CALLBACK_URL)) {
            return;
        }

        new MastodonAuthTask(
            this,
            intent.getData().getQueryParameter("code")
        ).execute();
    }
}