package net.netne.droidfx.droidfxsmartticket;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/**
 * Created by user on 5/9/2016.
 */

/**public class SettingsActivity extends PreferenceFragment {
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.temp_preference);
    }
}
**/

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{
    private static final String key_temp_preference="UNITS";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content
        getFragmentManager().beginTransaction().add(android.R.id.content, new MyPreferenceFragment()).commit();
        SharedPreferences sharedPrefs= PreferenceManager.getDefaultSharedPreferences(this);
        updatePreference("UNITS");
        sharedPrefs.registerOnSharedPreferenceChangeListener(this);
    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.temp_preference);
            Preference button=(Preference)getPreferenceManager().findPreference("exit");
            if(button!=null)
            {
                button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
                {
                    @Override
                    public boolean onPreferenceClick(Preference arg0){getActivity().finish();
                    return true;}
                });
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key)
    {
        updatePreference(key);

    }

    private void updatePreference(String key)
    {
        Preference preference=findPreference(key);
        if (preference instanceof ListPreference)
        {ListPreference listpreference=(ListPreference)preference;
            if(listpreference.getEntry().length()>0){
                listpreference.setSummary(listpreference.getEntry());
            }
            else
            {
                listpreference.setSummary("Skyblue");
            }

        }

    }
}
