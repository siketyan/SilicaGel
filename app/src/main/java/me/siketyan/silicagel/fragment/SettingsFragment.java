package me.siketyan.silicagel.fragment;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import me.siketyan.silicagel.R;
import me.siketyan.silicagel.activity.SettingsActivity;
import me.siketyan.silicagel.util.TwitterUtil;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        if (TwitterUtil.hasAccessToken(SettingsActivity.getContext())) {
            findPreference("twitter_auth").setEnabled(false);
            findPreference("delete_token").setEnabled(true);
        }

        findPreference("twitter_auth")
            .setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference pref) {
                        SettingsActivity.getContext().startAuthorize();
                        return false;
                    }
                }
            );

        findPreference("delete_token")
            .setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        TwitterUtil.deleteAccessToken(SettingsActivity.getContext());
                        SettingsActivity.getContext().recreate();
                        return false;
                    }
                }
            );
    }
}