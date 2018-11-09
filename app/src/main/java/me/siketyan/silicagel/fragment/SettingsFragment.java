package me.siketyan.silicagel.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import me.siketyan.silicagel.R;
import me.siketyan.silicagel.activity.SettingsActivity;
import me.siketyan.silicagel.enumeration.MastodonPrivacy;
import me.siketyan.silicagel.enumeration.MisskeyPrivacy;
import me.siketyan.silicagel.util.MastodonUtil;
import me.siketyan.silicagel.util.MisskeyUtil;
import me.siketyan.silicagel.util.TwitterUtil;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        if (TwitterUtil.hasAccessToken(SettingsActivity.getContext())) {
            findPreference("twitter_auth").setEnabled(false);
            findPreference("twitter_delete").setEnabled(true);
        }

        findPreference("twitter_auth")
            .setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference pref) {
                        SettingsActivity.getContext().startTwitterAuthorize();
                        return false;
                    }
                }
            );

        findPreference("twitter_delete")
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
            findPreference("mastodon_delete").setEnabled(true);
        }

        findPreference("mastodon_auth")
            .setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference pref) {
                        SettingsActivity.getContext().startMastodonAuthorize();
                        return false;
                    }
                }
            );

        findPreference("mastodon_delete")
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

        if (MisskeyUtil.hasAccessToken(SettingsActivity.getContext())) {
            findPreference("misskey_auth").setEnabled(false);
            findPreference("misskey_delete").setEnabled(true);
        }

        findPreference("misskey_auth")
                .setOnPreferenceClickListener(
                        new Preference.OnPreferenceClickListener() {
                            @Override
                            public boolean onPreferenceClick(Preference pref) {
                                SettingsActivity.getContext().startMisskeyAuthorize();
                                return false;
                            }
                        }
                );

        findPreference("misskey_delete")
                .setOnPreferenceClickListener(
                        new Preference.OnPreferenceClickListener() {
                            @Override
                            public boolean onPreferenceClick(Preference preference) {
                                MisskeyUtil.deleteApp(SettingsActivity.getContext());
                                SettingsActivity.getContext().recreate();
                                return false;
                            }
                        }
                );

        setPrivacySummary(PreferenceManager.getDefaultSharedPreferences(getActivity()));
        setMisskeyPrivacySummary(PreferenceManager.getDefaultSharedPreferences(getActivity()));
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
        if (key.equals("mastodon_privacy")) {
            setPrivacySummary(pref);
        }
        if (key.equals("misskey_privacy")) {
            setMisskeyPrivacySummary(pref);
        }
    }

    private void setPrivacySummary(SharedPreferences pref) {
        Preference listPref = findPreference("mastodon_privacy");
        MastodonPrivacy privacy = MastodonPrivacy.getByValue(pref.getString("mastodon_privacy", "public"));

        if (privacy != null) {
            listPref.setSummary(privacy.getSummaryId());
        }
    }

    private void setMisskeyPrivacySummary(SharedPreferences pref) {
        Preference listPref = findPreference("misskey_privacy");
        MisskeyPrivacy privacy = MisskeyPrivacy.getByValue(pref.getString("misskey_privacy", "public"));

        if (privacy != null) {
            listPref.setSummary(privacy.getSummaryId());
        }
    }
}