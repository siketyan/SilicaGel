package me.siketyan.silicagel.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import android.widget.EditText;
import me.siketyan.silicagel.R;
import me.siketyan.silicagel.fragment.SettingsFragment;
import me.siketyan.silicagel.misskeyapi.Permission;
import me.siketyan.silicagel.service.NotificationService;
import me.siketyan.silicagel.util.MastodonUtil;
import me.siketyan.silicagel.util.MisskeyUtil;

public class SettingsActivity extends Activity {
    private static SettingsActivity context;
    private EditText editView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;

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

    public void startTwitterAuthorize() {
        startActivity(new Intent(this, TwitterAuthActivity.class));
    }

    public void startMastodonAuthorize() {
        editView = new EditText(this);
        new AlertDialog.Builder(SettingsActivity.getContext())
            .setTitle(R.string.mastodon_dialog_title)
            .setView(editView)
            .setPositiveButton(R.string.dialog_positive, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String instanceName = editView.getText().toString();
                    Context context = SettingsActivity.this;
                    MastodonUtil.setInstanceName(context, instanceName);

                    startActivity(new Intent(context, MastodonAuthActivity.class));
                }
            })
            .setNegativeButton(R.string.dialog_negative, null)
            .show();
    }

    public void startMisskeyAuthorize() {
        editView = new EditText(this);
        new AlertDialog.Builder(SettingsActivity.getContext())
                .setTitle(R.string.misskey_dialog_title)
                .setView(editView)
                .setPositiveButton(R.string.dialog_positive, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String instanceName = editView.getText().toString();
                        Context context = SettingsActivity.this;
                        MisskeyUtil.setInstanceName(context, instanceName);

                        startActivity(new Intent(context, MisskeyAuthActivity.class));
                    }
                })
                .setNegativeButton(R.string.dialog_negative, null)
                .show();
    }

    public static SettingsActivity getContext() {
        return context;
    }
}