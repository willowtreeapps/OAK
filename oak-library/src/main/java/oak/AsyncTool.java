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

package oak;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.Gravity;
import android.widget.Toast;

import com.google.inject.Inject;

import roboguice.inject.ContextSingleton;

/**
 * User: mlake Date: 3/23/12 Time: 10:32 AM
 */

@ContextSingleton
public class AsyncTool {

    @Inject Activity mActivity;

    private ProgressDialog mProgressDialog;
    private AlertDialog mAlertDialog;

    private static final int ERROR_DURATION = 1000;

    public AsyncTool() {
    }

    public AsyncTool(Activity activity) {
        mActivity = activity;
    }

    public AlertDialog getProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(mActivity);
            mProgressDialog.setIndeterminate(true);
        }
        return mProgressDialog;
    }


    private void setBusy(final boolean busy) {

        if (busy) {
            getProgressDialog().show();
        } else {
            try {
                getProgressDialog().dismiss();
                mProgressDialog = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static abstract class SimpleTask extends AsyncTask<Void, String, Void> {

        AsyncTool mAsyncTool;

        public SimpleTask(AsyncTool asyncTool) {
            mAsyncTool = asyncTool;
        }

        private Exception mException;

        @Override
        public void onPreExecute() {
            mAsyncTool.setBusy(true);
        }

        public abstract void exceptionalLabor() throws Exception;

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            mAsyncTool.getProgressDialog().setMessage(values[0]);
        }

        @Override
        public Void doInBackground(Void... unused) {
            try {
                exceptionalLabor();
            } catch (Exception e) {
                mException = e;
            }
            return null;
        }

        @Override
        public void onPostExecute(Void unused) {
            mAsyncTool.setBusy(false);
            if (mException != null && !mAsyncTool.mActivity.isFinishing()) {
                onError(mException);
            }
        }

        public void onError(Exception exception) {
            mAsyncTool.displayAlertDialog(exception.getMessage(), true);
        }

        public boolean isInError() {
            return mException != null;
        }
    }

    void displayAlertDialog(String message, boolean isError) {
        displayAlertDialog(message, isError, null);
    }

    void displayAlertDialog(String message, boolean isError,
            final DialogInterface.OnClickListener callback) {

        if (mAlertDialog == null) {
            String title;
            int icon;
            if (isError) {
                title = "Error";
                icon = android.R.drawable.ic_dialog_alert;
            } else {
                title = null;
                icon = android.R.drawable.ic_dialog_info;
            }

            mAlertDialog = new AlertDialog.Builder(mActivity)
                    .setIcon(icon)
                    .setTitle(title)
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mAlertDialog = null;
                            if (callback != null) {
                                callback.onClick(dialogInterface, i);
                            }
                        }
                    })
                    .show();
        } else {
            Toast toast = Toast.makeText(mActivity, message, ERROR_DURATION);
            toast.setGravity(Gravity.TOP, 0, 50);
            toast.show();
        }
    }
}
