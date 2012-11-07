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

package oak.demo.widget;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import oak.demo.OakDemoActivity;
import oak.demo.R;
import oak.widget.CustomProgressDialog;
import roboguice.inject.InjectView;

/**
 * User: Michael Lake Date: 11/21/11 Time: 5:33 PM
 */


public class CustomProgressDialogActivity extends OakDemoActivity {

    @InjectView(R.id.show_dialog) Button showDialog;

    private final int[] mProgressDrawables = {
            R.drawable.loading_00,
            R.drawable.loading_01,
            R.drawable.loading_02,
            R.drawable.loading_03,
            R.drawable.loading_04,
            R.drawable.loading_05,
            R.drawable.loading_06,
            R.drawable.loading_07,
            R.drawable.loading_08,
            R.drawable.loading_09,
            R.drawable.loading_10,
            R.drawable.loading_11,
            R.drawable.loading_12,
            R.drawable.loading_13,
            R.drawable.loading_14,
            R.drawable.loading_15,
            R.drawable.loading_16,
            R.drawable.loading_17,
            R.drawable.loading_18,
            R.drawable.loading_19,
            R.drawable.loading_20};
    private CustomProgressDialog mCpd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_progress_dialog_activity);

        mCpd = new CustomProgressDialog(this, mProgressDrawables);

        showDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCpd.isShowing()) {
                    mCpd.hide();
                } else {
                    mCpd.show();
                }
            }
        });
    }
}
