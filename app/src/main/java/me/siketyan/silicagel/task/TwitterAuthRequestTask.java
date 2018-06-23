package me.siketyan.silicagel.task;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import me.siketyan.silicagel.activity.SettingsActivity;
import me.siketyan.silicagel.util.TwitterUtil;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.RequestToken;

public class TwitterAuthRequestTask extends AsyncTask<Void, Void, String> {
    private Context context;

    public TwitterAuthRequestTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            Twitter twitter = TwitterUtil.getTwitterInstance(context);
            RequestToken request = twitter.getOAuthRequestToken(SettingsActivity.TWITTER_CALLBACK_URL);
            TwitterUtil.storeRequestToken(context, request);

            return request.getAuthorizationURL();
        } catch (TwitterException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String uri) {
        if (uri != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            context.startActivity(intent);
        }
    }
}