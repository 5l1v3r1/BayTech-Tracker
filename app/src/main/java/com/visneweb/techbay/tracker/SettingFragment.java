package com.visneweb.techbay.tracker;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by riskactive on 16.03.2018.
 */

public class SettingFragment extends PreferenceFragment {

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

}
