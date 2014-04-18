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

package oak.demo;

import android.app.Application;

import oak.util.PRNGFixes;

/**
 * User: Michael Lake Date: 9/20/11 Time: 10:53 AM
 *
 * This is just a placeholder class that lets us demonstrate the testing
 * framework
 */
public class OakApplication extends Application {

    private String message;

    @Override
    public void onCreate() {
        super.onCreate();

        message = "testMessage";

        /**
         * Ensure the keys generated for the CryptoSharedPreferences are strong and sufficiently
         * random.
         *
         * See http://android-developers.blogspot.com/2013/08/some-securerandom-thoughts.html
         */
        PRNGFixes.apply();

    }

    public String getMessage() {
        return message;
    }
}
