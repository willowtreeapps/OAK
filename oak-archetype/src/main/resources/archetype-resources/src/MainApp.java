package $

{package};

import com.google.inject.Inject;
import com.google.inject.Injector;

import android.app.Application;
import android.content.pm.PackageManager;
import android.util.Log;

import roboguice.RoboGuice;

public class MainApp extends Application {

    public static String TAG = "${artifactId}";

    @Inject Datastore mDataStore;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        Injector i = RoboGuice.getBaseApplicationInjector(this);
        mDataStore = i.getInstance(Datastore.class);
        try {
            int newVersionCode = getPackageManager()
                    .getPackageInfo(getPackageName(), 0).versionCode;
            int oldVersionCode = mDataStore.getVersion();
            if (oldVersionCode != 0 && oldVersionCode != newVersionCode) {
                onVersionUpdate(oldVersionCode, newVersionCode);
            }
            mDataStore.persistVersion(newVersionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void onVersionUpdate(int oldVersionCode, int newVersionCode) {
        //this method is called when the version code changes, use comparison operators to control migration
    }
}

