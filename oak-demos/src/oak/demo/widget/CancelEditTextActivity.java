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
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import oak.demo.OakDemoActivity;
import oak.demo.R;
import oak.widget.CancelEditText;
import roboguice.inject.InjectView;

/**
 * User: mlake Date: 12/8/11 Time: 10:56 AM
 */
public class CancelEditTextActivity extends OakDemoActivity {

    @InjectView(R.id.cancel_edit_one) CancelEditText cancelEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cancel_edit_text_demo);

        cancelEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (cancelEditText.getText().length() == 0) {
                    Toast.makeText(CancelEditTextActivity.this,
                            "The CancelEditText was cleared", 1000)
                            .show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}
