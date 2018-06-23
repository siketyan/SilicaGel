package me.siketyan.silicagel.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;
import com.sys1yagi.mastodon4j.api.entity.auth.AccessToken;
import com.sys1yagi.mastodon4j.api.entity.auth.AppRegistration;
import com.sys1yagi.mastodon4j.api.exception.Mastodon4jRequestException;
import com.sys1yagi.mastodon4j.api.method.Apps;
import me.siketyan.silicagel.R;
import me.siketyan.silicagel.util.MastodonUtil;

public class MastodonAuthActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTheme(android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
        this.setContentView(R.layout.activity_twitterauth);

        Intent intent = this.getIntent();
        if (intent == null || intent.getData() == null) {
            return;
        }

        SettingsActivity settingsActivity = SettingsActivity.getContext();
        final String authCode = intent.getData().getQueryParameter("code");
        final Apps apps = settingsActivity.apps;
        final AppRegistration appRegistration = settingsActivity.appRegistration;

        AsyncTask<Void, Void, AccessToken> task = new AsyncTask<Void, Void, AccessToken>() {
            @Override
            protected AccessToken doInBackground(Void... params) {
                try {
                    return apps.getAccessToken(
                        appRegistration.getClientId(),
                        appRegistration.getClientSecret(),
                        appRegistration.getRedirectUri(),
                        authCode,
                        "authorization_code"
                    ).execute();
                } catch (Mastodon4jRequestException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(AccessToken accessToken) {
                if (accessToken != null) {
                    showToast(getString(R.string.authorized));
                    MastodonUtil.storeApp(
                        SettingsActivity.getContext(),
                        appRegistration.getInstanceName(),
                        accessToken.getAccessToken()
                    );
                } else {
                    showToast(getString(R.string.auth_failed));
                }

                startActivity(new Intent(MastodonAuthActivity.this, SettingsActivity.class));
                finish();
            }
        };

        task.execute();
    }

    public static void showToast(String text) {
        Toast.makeText(SettingsActivity.getContext(), text, Toast.LENGTH_SHORT).show();
    }
}