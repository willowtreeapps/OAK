/*
 * Copyright (c) 2012. WillowTree Apps, Inc.
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

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.Date;

import oak.AsyncTool;

/**
 * User: mlake Date: 3/23/12 Time: 1:46 PM
 */
public class AsyncToolActivity extends Activity {

    AsyncTool mAsyncTool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.async_tool_demo);
        mAsyncTool = new AsyncTool(this);
    }

    public void tryMeClicked(View view) {

        final String someArgument = "Mogwai";

        //noinspection unchecked
        new AsyncTool.SimpleTask(mAsyncTool) {
            String someReturnValue;
            @Override
            public void exceptionalLabor() throws Exception {
                publishProgress("Watering " + someArgument + "...");
                Thread.sleep(2000);
                publishProgress("Planning destruction of the universe..");
                Thread.sleep(3000);
                if (new Date().getTime() % 2 == 0) {
                    throw new Exception("Sorry, the remote death star quit unexpectedly.");
                }
                someReturnValue = "ha ha, just kidding - everything's okay.";
            }
            @Override
            public void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                if (!isInError()) {
                    Toast.makeText(AsyncToolActivity.this, someReturnValue, 3000)
                            .show();
                }
            }
        }.execute();
    }
}
