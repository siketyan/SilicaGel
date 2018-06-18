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
    
    public static SettingsActivity getContext() {
        return context;
    }

    public void startMastodonAuthorize(final String instanceName) {
        AsyncTask<Void, Void, AppRegistration> createAppTask = new AsyncTask<Void, Void, AppRegistration>() {
            @Override
            protected AppRegistration doInBackground(Void... params) {
                MastodonClient client = new MastodonClient.Builder(instanceName, new OkHttpClient.Builder(), new Gson()).build();
                Apps apps = new Apps(client);
                try {
                    AppRegistration appRegistration = apps.createApp(
                            "SilicaGel",
                            "silicagel://mastodon",
                            new Scope(Scope.Name.WRITE),
                            ""
                    ).execute();
                    return appRegistration;
                } catch (Mastodon4jRequestException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(AppRegistration result) {
                if (result != null) {
                    MastodonOauth(result);
                }
            }
        };
        createAppTask.execute();
    }

    private void MastodonOauth(AppRegistration appRegistration) {
        String instanceName = appRegistration.getInstanceName();
        String clientId = appRegistration.getClientId();
        String clientSecret = appRegistration.getClientSecret();
        String redirectUri = appRegistration.getRedirectUri();
        MastodonUtil.INSTANCE.storeApp(context, instanceName, clientId, clientSecret, redirectUri);
        MastodonClient client = new MastodonClient.Builder(instanceName, new OkHttpClient.Builder(), new Gson()).build();
        Apps apps = new Apps(client);
        String url = apps.getOAuthUrl(clientId, new Scope(Scope.Name.WRITE), redirectUri);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    public void MastodonLogin() {
        final EditText editView = new EditText(SettingsActivity.getContext());
        AlertDialog.Builder dialog = new AlertDialog.Builder(SettingsActivity.getContext());
        dialog.setTitle("インスタンスのURLを入力してください");
        dialog.setView(editView);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String instanceName = editView.getText().toString();
                startMastodonAuthorize(instanceName);
            }
        });
        dialog.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        dialog.show();
    }
}