package ${package};

import com.google.inject.Inject;
import com.google.inject.Singleton;

import android.content.SharedPreferences;

@Singleton
public class Datastore {

    private static final String DEVICE_VERSION = "DeviceVersion";

    @Inject EncryptedSharedPreferences encryptedSharedPreferences;

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

