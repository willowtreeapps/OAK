package com.demoapp;

import com.google.inject.Injector;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Assert;


import roboguice.RoboGuice;

/**
 * User: wtauser Date: 9/6/12 Time: 3:56 PM
 */
@RunWith(RobolectricTestRunner.class)
public class DatastoreTest {
    Datastore datastore;
    @Before
    public void setup(){
        Injector i = RoboGuice.getBaseApplicationInjector(Robolectric.application);
        datastore = i.getInstance(Datastore.class);
    }
    @Test
    public void versionShouldPersist(){
        int testVersion = 12345;
        datastore.persistVersion(testVersion);

        Assert.assertEquals(testVersion, datastore.getVersion());
    }
}
