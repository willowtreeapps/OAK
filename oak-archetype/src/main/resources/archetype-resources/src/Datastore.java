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


    public String getVersion() {
        return getPrefs().getString(DEVICE_VERSION, null);
    }
    public void persistVersion(String version) {
        getEditor().putString(DEVICE_VERSION, version).commit();
    }

}

