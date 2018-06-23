package me.siketyan.silicagel.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.EditText;

import com.google.gson.Gson;
import com.sys1yagi.mastodon4j.MastodonClient;
import com.sys1yagi.mastodon4j.api.Scope;
import com.sys1yagi.mastodon4j.api.entity.auth.AppRegistration;
import com.sys1yagi.mastodon4j.api.exception.Mastodon4jRequestException;
import com.sys1yagi.mastodon4j.api.method.Apps;

import me.siketyan.silicagel.R;
import me.siketyan.silicagel.fragment.SettingsFragment;
import me.siketyan.silicagel.service.NotificationService;
import me.siketyan.silicagel.util.MastodonUtil;
import me.siketyan.silicagel.util.TwitterUtil;
import okhttp3.OkHttpClient;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.RequestToken;

public class SettingsActivity extends Activity {
    private static SettingsActivity context;
    
    static final String CALLBACK_URL = "silicagel://twitter";
    Twitter twitter;
    RequestToken requestToken;

    private static final String MASTODON_APP_NAME = "SilicaGel";
    private static final String MASTODON_WEBSITE = "https://github.com/Siketyan/SilicaGel";
    private static final String MASTODON_CALLBACK_URL = "silicagel://mastodon";
    private MastodonClient mastodon;
    Apps apps;
    AppRegistration appRegistration;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;
        twitter = TwitterUtil.getTwitterInstance(this);
        
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
        
        SettingsFragment fragment = new SettingsFragment();
        getFragmentManager().beginTransaction()
                            .replace(android.R.id.content, fragment)
                            .commit();

        if (!NotificationService.isNotificationAccessEnabled) {
            startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
        }
    }
    
    public void startAuthorize() {
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    requestToken = twitter.getOAuthRequestToken(CALLBACK_URL);
                    return requestToken.getAuthorizationURL();
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }
            
            @Override
            protected void onPostExecute(String url) {
                if (url != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                }
            }
        };
        
        task.execute();
    }

    private void startMastodonAuthorize(final String instanceName) {
        AsyncTask<Void, Void, Void> createAppTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                mastodon = new MastodonClient.Builder(instanceName, new OkHttpClient.Builder(), new Gson()).build();
                apps = new Apps(mastodon);

                try {
                    appRegistration = apps.createApp(
                            MASTODON_APP_NAME,
                            MASTODON_CALLBACK_URL,
                            new Scope(Scope.Name.WRITE),
                            MASTODON_WEBSITE
                    ).execute();
                } catch (Mastodon4jRequestException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void param) {
                String url = apps.getOAuthUrl(
                    appRegistration.getClientId(),
                    new Scope(Scope.Name.WRITE),
                    appRegistration.getRedirectUri()
                );

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        };

        createAppTask.execute();
    }

    public void MastodonLogin() {
        final EditText editView = new EditText(this);
        new AlertDialog.Builder(SettingsActivity.getContext())
            .setTitle(R.string.mastodon_dialog_title)
            .setView(editView)
            .setPositiveButton(R.string.mastodon_dialog_positive, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String instanceName = editView.getText().toString();
                    startMastodonAuthorize(instanceName);
                }
            })
            .setNegativeButton(R.string.mastodon_dialog_negative, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            })
            .show();
    }

    public static SettingsActivity getContext() {
        return context;
    }
}