package com.appham.photomosaicmagic.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.appham.photomosaicmagic.BaseActivity;
import com.appham.photomosaicmagic.Prefs;
import com.appham.photomosaicmagic.R;

/**
 * @author Thomas Fuchs-Martin <t.fuchsmartin@gmail.com>
 */

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String TAG = "SettingsFragment";
    private Prefs prefs;

    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.prefs = ((BaseActivity) getActivity()).getPrefs();

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.prefs);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        prefs.updatePrefs();
        prefs.save();
    }
}
