package oak.demo;

import com.google.inject.Inject;
import com.google.inject.Provider;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import roboguice.inject.SharedPreferencesName;

/**
 * Generated from archetype
 */
public class EncryptedPreferencesProvider implements Provider<EncryptedSharedPreferences> {


    protected String preferencesName;

    @Inject protected Application application;

    public EncryptedPreferencesProvider() {
    }

    @Inject
    public EncryptedPreferencesProvider(
            EncryptedPreferencesProvider.PreferencesNameHolder preferencesNameHolder) {
        preferencesName = preferencesNameHolder.value;
    }

    public EncryptedPreferencesProvider(String preferencesName) {
        this.preferencesName = preferencesName;
    }


    @Override
    public EncryptedSharedPreferences get() {
        SharedPreferences sharedPreferences;
        if (preferencesName != null) {
            sharedPreferences = application
                    .getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        } else {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application);
        }

        return new EncryptedSharedPreferences(application, sharedPreferences);
    }

    public static class PreferencesNameHolder {

        @Inject(optional = true)
        @SharedPreferencesName
        protected String value;
    }
}
