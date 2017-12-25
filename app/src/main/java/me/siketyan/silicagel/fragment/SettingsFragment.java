package me.siketyan.silicagel.fragment;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import me.siketyan.silicagel.R;
import me.siketyan.silicagel.activity.SettingsActivity;
import me.siketyan.silicagel.activity.TwitterAuthActivity;
import me.siketyan.silicagel.util.TwitterUtil;

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
                            TwitterAuthActivity.showToast(getString(R.string.twitter_already));
                            return true;
                        }
    
                        SettingsActivity.getContext().startAuthorize();
                        return false;
                    }
                }
            );
    }
}