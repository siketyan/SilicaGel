package me.siketyan.silicagel.cloudplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;
import me.siketyan.silicagel.cloudplayer.R;
import me.siketyan.silicagel.cloudplayer.util.TwitterUtil;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;

public class TwitterAuthActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
        setContentView(R.layout.activity_twitterauth);
        
        Intent intent = getIntent();
        if (intent == null || intent.getData() == null
            || !intent.getData().toString().startsWith(SettingsActivity.CALLBACK_URL)) {
            return;
        }
        
        String verifier = intent.getData().getQueryParameter("oauth_verifier");
        AsyncTask<String, Void, AccessToken> task = new AsyncTask<String, Void, AccessToken>() {
            @Override
            protected AccessToken doInBackground(String... params) {
                try {
                    return SettingsActivity.getContext().twitter
                                           .getOAuthAccessToken(
                                                SettingsActivity.getContext().requestToken,
                                                params[0]
                                            );
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                
                return null;
            }
        
            @Override
            protected void onPostExecute(AccessToken accessToken) {
                if (accessToken != null) {
                    showToast("Twitterの認証を完了しました。");
                    TwitterUtil.storeAccessToken(SettingsActivity.getContext(), accessToken);
                } else {
                    showToast("Twitterの認証に失敗しました。");
                }
            
                startActivity(new Intent(TwitterAuthActivity.this, SettingsActivity.class));
                finish();
            }
        };
        
        task.execute(verifier);
    }
    
    public static void showToast(String text) {
        Toast.makeText(SettingsActivity.getContext(), text, Toast.LENGTH_SHORT).show();
    }
}