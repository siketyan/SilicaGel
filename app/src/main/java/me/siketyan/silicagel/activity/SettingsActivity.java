package me.siketyan.silicagel.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import me.siketyan.silicagel.fragment.SettingsFragment;
import me.siketyan.silicagel.service.NotificationService;

public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
}