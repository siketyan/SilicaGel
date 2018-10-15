package me.siketyan.silicagel.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import org.json.JSONArray;

import me.siketyan.silicagel.R;
import me.siketyan.silicagel.misskeyapi.MisskeyClient;
import me.siketyan.silicagel.misskeyapi.Permission;
import me.siketyan.silicagel.misskeyapi.api.endpoints.Apps;
import me.siketyan.silicagel.misskeyapi.api.entities.auth.AppRegistration;
import me.siketyan.silicagel.misskeyapi.api.exception.MisskeyRequestException;
import me.siketyan.silicagel.task.MisskeyAuthRequestTask;
import me.siketyan.silicagel.util.MisskeyUtil;

public class MisskeyAuthActivity extends Activity {
    public static final String CALLBACK_URL = null;
    public static final String APP_NAME = "SilicaGel";
    public static final String DESCRIPTION = "SilicaGel for Misskey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.authorizing_misskey);
        setTheme(android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
        setContentView(R.layout.activity_misskey_authorizing);

        final MisskeyClient client = MisskeyUtil.getClient(this);

        final JSONArray json = new Permission(Permission.Name.NOTE_WRITE, Permission.Name.DRIVE_WRITE).toJSONArray();

        AsyncTask<Void, Void, AppRegistration> task = new AsyncTask<Void, Void, AppRegistration>() {
            @Override
            protected AppRegistration doInBackground(Void... params) {
                Apps apps = new Apps(client);
                try {
                    return apps.createApp(APP_NAME, DESCRIPTION, CALLBACK_URL, json).execute();
                } catch (MisskeyRequestException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(AppRegistration result) {
                if (result != null) {
                    String AppSecret = result.getAppSecret();
                    Log.d("AppSecret", AppSecret);
                    MisskeyUtil.storeAppRegistration(MisskeyAuthActivity.this, result);
                    WebView webView = findViewById(R.id.webview);
                    new MisskeyAuthRequestTask(MisskeyAuthActivity.this, webView).execute();
                } else {
                    Toast.makeText(MisskeyAuthActivity.this, R.string.error, Toast.LENGTH_SHORT)
                            .show();
                    finish();
                }
            }
        };
        task.execute();
    }
}