package ${package};

import android.content.Context;

import android.app.Application;
import android.content.pm.PackageManager;
import android.util.Log;
import android.content.Context;

import javax.inject.Inject;
import dagger.ObjectGraph;
import ${package}.dagger.AppModule;
import ${package}.dagger.IObjectGraph;

public class MainApp extends Application implements IObjectGraph {

    public static String TAG = "${artifactId}";
    private static ObjectGraph applicationGraph;
    private static Context sContext;

    @Inject Datastore mDataStore;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        sContext = this;
        applicationGraph = ObjectGraph.create(getAppModule());
        applicationGraph.inject(this);
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

    public static Context getContext() {
        return sContext;
    }

    protected Object getAppModule() {
        return new AppModule(this);
    }

    @Override
    public ObjectGraph getObjectGraph() {
        return applicationGraph;
    }
}

