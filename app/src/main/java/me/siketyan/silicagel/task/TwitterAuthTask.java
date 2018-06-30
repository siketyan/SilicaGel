package me.siketyan.silicagel.task;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;
import me.siketyan.silicagel.R;
import me.siketyan.silicagel.activity.SettingsActivity;
import me.siketyan.silicagel.util.TwitterUtil;
import twitter4j.auth.AccessToken;

public class TwitterAuthTask extends AsyncTask<Void, Void, AccessToken> {
    private Context context;
    private String verifier;

    public TwitterAuthTask(Context context, String verifier) {
        this.context = context;
        this.verifier = verifier;
    }

    @Override
    protected AccessToken doInBackground(Void... params) {
        try {
            return TwitterUtil.getTwitterInstance(context)
                .getOAuthAccessToken(
                    TwitterUtil.getRequestToken(context),
                    verifier
                );
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(AccessToken accessToken) {
        if (accessToken != null) {
            showToast(context.getString(R.string.authorized));
            TwitterUtil.storeAccessToken(SettingsActivity.getContext(), accessToken);
        } else {
            showToast(context.getString(R.string.auth_failed));
        }

        context.startActivity(new Intent(context, SettingsActivity.class));
        if (context instanceof Activity) {
            ((Activity) context).finish();
        }
    }

    private static void showToast(String text) {
        Toast.makeText(SettingsActivity.getContext(), text, Toast.LENGTH_SHORT).show();
    }
}