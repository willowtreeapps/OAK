package $

import android.app.Application;
import android.preference.PreferenceManager;

{package};

import javax.inject.Singleton;

import android.content.SharedPreferences;

@Singleton
public class Datastore {

    private static final String DEVICE_VERSION = "DeviceVersion";

    EncryptedSharedPreferences encryptedSharedPreferences;

    public Datastore(Application app) {
        encryptedSharedPreferences = new EncryptedSharedPreferences(app,
                PreferenceManager.getDefaultSharedPreferences(app));
    }

    private SharedPreferences.Editor getEditor() {
        return encryptedSharedPreferences.edit();
    }

    private SharedPreferences getPrefs() {
        return encryptedSharedPreferences;
    }
    public int getVersion() {
        return getPrefs().getInt(DEVICE_VERSION, 0);
    }
    public void persistVersion(int version) {
        getEditor().putInt(DEVICE_VERSION, version).commit();
    }
}

