package me.siketyan.silicagel.cloudplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import me.siketyan.silicagel.cloudplayer.fragment.SettingsFragment;
import me.siketyan.silicagel.cloudplayer.service.NotificationService;
import me.siketyan.silicagel.cloudplayer.util.TwitterUtil;
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
}