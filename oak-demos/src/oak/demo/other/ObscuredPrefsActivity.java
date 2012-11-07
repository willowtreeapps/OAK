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

package oak.demo.other;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import oak.demo.EncryptedPreferences;
import oak.demo.OakDemoActivity;
import oak.demo.R;
import roboguice.inject.InjectView;

/**
 * User: mlake Date: 12/19/11 Time: 9:17 AM
 */

// START SNIPPET: obscured_prefs
public class ObscuredPrefsActivity extends OakDemoActivity {

    private static final String MY_APP_PREFENCES_NAME = "my_app_prefences_name";

    private SharedPreferences mNormalSharedPreferences;
    private EncryptedPreferences mEncryptedPreferences;
    @InjectView(R.id.my_edittext) private EditText mEditText;
    @InjectView(R.id.saved_content) private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.obscured_prefs_demo);

        mNormalSharedPreferences = getSharedPreferences(MY_APP_PREFENCES_NAME, MODE_PRIVATE);

        //EncryptedPreferences is defined by you and extends ObscuredSharedPreferences
        mEncryptedPreferences = new EncryptedPreferences(this, mNormalSharedPreferences);

        //load up saved value into textview for demonstration
        mTextView.setText(mNormalSharedPreferences.getString("first_name", ""));

    }

    public void saveEncryptedClicked(View view) {
        //save input as encrypted
        mEncryptedPreferences.edit()
                .putString("first_name", mEditText.getText().toString())
                .commit();

        showRawContents();
    }

    public void saveNormallyClicked(View view) {
        //save unencrypted
        mNormalSharedPreferences.edit()
                .putString("first_name", mEditText.getText().toString())
                .commit();

        showRawContents();
    }

    private void showRawContents() {
        //demonstrate what could be viewed by a light-weight hacker
        mTextView.setText(mNormalSharedPreferences.getString("first_name", ""));

        //note: to read encrypted data, you would just use mEncryptedPreferences.getString(...
    }
}
// END SNIPPET: obscured_prefs

