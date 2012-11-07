/*
 * Copyright (c) 2011. WillowTree Apps, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package oak.demo.utils;

import android.os.Bundle;
import android.widget.TextView;

import oak.demo.OakDemoActivity;
import oak.demo.R;
import oak.util.OakUtils;
import roboguice.inject.InjectView;

/**
 * User: Michael Lake Date: 11/21/11 Time: 5:33 PM
 */


public class IsPackageInstalledActivity extends OakDemoActivity {

    @InjectView(R.id.packages) TextView packages;

    String[] packagesToCheck = new String[]{
            "com.android.chrome",
            "com.facebook.katana",
            "com.pandora.android",
            "com.instagram.android",
            "com.fingersoft.hillclimb",
            "com.facebook.orca",
            "com.twitter.android",
            "com.skype.raider",
            "com.netflix.mediaclient",
            "com.ludia.familyfeudandfriends",
            "com.rovio.angrybirds",
            "com.imangi.templerun",
            "com.weather.Weather",
            "com.shazam.android"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.is_package_installed_demo);

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < packagesToCheck.length; i++) {
            sb.append(packagesToCheck[i]);
            sb.append(": ");
            sb.append(OakUtils.isPackageInstalled(this, packagesToCheck[i]));
            sb.append("\n");
        }

        packages.setText(sb.toString());
    }
}
