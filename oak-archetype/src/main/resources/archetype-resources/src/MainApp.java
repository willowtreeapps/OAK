package ${package};

import com.google.inject.Inject;
import com.google.inject.Injector;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import oak.OAKImageLoader;
import roboguice.RoboGuice;

public class MainApp extends Application {

    public static String TAG = "${artifactId}";

    @Inject Datastore mDataStore;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        OAKImageLoader.initialize(this, OAKImageLoader.PREFER_SD);
        Injector i = RoboGuice.getBaseApplicationInjector(this);
        mDataStore = i.getInstance(Datastore.class);



    }

}

