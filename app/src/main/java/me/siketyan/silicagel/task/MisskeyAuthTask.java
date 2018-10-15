package me.siketyan.silicagel.task;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import me.siketyan.silicagel.R;
import me.siketyan.silicagel.activity.SettingsActivity;
import me.siketyan.silicagel.misskeyapi.MisskeyClient;
import me.siketyan.silicagel.misskeyapi.api.endpoints.Apps;
import me.siketyan.silicagel.misskeyapi.api.entities.auth.AccessToken;
import me.siketyan.silicagel.util.MisskeyUtil;

public class MisskeyAuthTask extends AsyncTask<Void, Void, AccessToken> {
    private Context context;
    private String authToken;

    public MisskeyAuthTask(Context context, String authToken) {
        this.context = context;
        this.authToken = authToken;
    }

    @Override
    protected AccessToken doInBackground(Void... params) {
        try {
            MisskeyClient client = MisskeyUtil.getClient(context);
            Apps apps = new Apps(client);
            return apps.getAccessToken(MisskeyUtil.getClientSecret(context),authToken).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(AccessToken accessToken) {
        if (accessToken != null) {
            showToast(context.getString(R.string.authorized));
            String token = accessToken.getAccessToken() + MisskeyUtil.getClientSecret(context);
            String sha256 = TokenToSha256(token);

            MisskeyUtil.storeApp(
                SettingsActivity.getContext(),
                MisskeyUtil.getInstanceName(context),
                sha256
            );
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

    private String TokenToSha256(String value) {
        MessageDigest md = null;
        StringBuilder sb = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md.update(value.getBytes());
        sb = new StringBuilder();
        for (byte b : md.digest()) {
            String hex = String.format("%02x", b);
            sb.append(hex);
        }
        return sb.toString();
    }
}