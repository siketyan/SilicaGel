package me.siketyan.silicagel.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import me.siketyan.silicagel.R;
import me.siketyan.silicagel.activity.SettingsActivity;
import me.siketyan.silicagel.util.MastodonUtil;
import me.siketyan.silicagel.util.TwitterUtil;

public class SettingsFragment extends PreferenceFragment {
    private SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("privacy")) {
                Preference privacy = findPreference(key);
                String privacy_value = sharedPreferences.getString(key, "0");
                int privacy_i = Integer.parseInt(privacy_value);
                switch (privacy_i) {
                    case 0:
                        privacy.setSummary(R.string.privacy_public);
                        break;
                    case 1:
                        privacy.setSummary(R.string.privacy_unlisted);
                        break;
                    case 2:
                        privacy.setSummary(R.string.privacy_private);
                        break;
                    case 3:
                        privacy.setSummary(R.string.privacy_direct);
                        break;
                }
            }
        }
    };

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

        if (MastodonUtil.hasAccessToken(SettingsActivity.getContext())) {
            findPreference("mastodon_auth").setEnabled(false);
            findPreference("delete_mastodon_token").setEnabled(true);
        }

        findPreference("mastodon_auth")
                .setOnPreferenceClickListener(
                        new Preference.OnPreferenceClickListener() {
                            @Override
                            public boolean onPreferenceClick(Preference pref) {
                                SettingsActivity.getContext().MastodonLogin();
                                return false;
                            }
                        }
                );

        findPreference("delete_mastodon_token")
                .setOnPreferenceClickListener(
                        new Preference.OnPreferenceClickListener() {
                            @Override
                            public boolean onPreferenceClick(Preference preference) {
                                MastodonUtil.deleteApp(SettingsActivity.getContext());
                                SettingsActivity.getContext().recreate();
                                return false;
                            }
                        }
                );

        Preference privacy = findPreference("privacy");
        String privacy_value = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("privacy", "0");
        int privacy_i = Integer.parseInt(privacy_value);
        switch (privacy_i) {
            case 0:
                privacy.setSummary(R.string.privacy_public);
                break;
            case 1:
                privacy.setSummary(R.string.privacy_unlisted);
                break;
            case 2:
                privacy.setSummary(R.string.privacy_private);
                break;
            case 3:
                privacy.setSummary(R.string.privacy_direct);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(listener);
    }
}