package io.github.siketyan.silicagel.cloudplayer.fragment;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import io.github.siketyan.silicagel.cloudplayer.R;
import io.github.siketyan.silicagel.cloudplayer.activity.SettingsActivity;
import io.github.siketyan.silicagel.cloudplayer.activity.TwitterAuthActivity;
import io.github.siketyan.silicagel.cloudplayer.util.TwitterUtil;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        findPreference("twitter_auth")
            .setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference pref) {
                        if (TwitterUtil.hasAccessToken(SettingsActivity.getContext())) {
                            TwitterAuthActivity.showToast("既に認証済みです。");
                            return true;
                        }
    
                        SettingsActivity.getContext().startAuthorize();
                        return false;
                    }
                }
            );
    }
}